package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CraftsmanRepository
import com.example.data.ServiceRequestRepository
import com.example.data.ServiceRequestResult
import com.example.data.db.AppDatabase
import com.example.data.db.CraftsmanEntity
import com.example.data.db.ReviewEntity
import com.example.data.db.ServiceRequestEntity
import com.example.data.remote.SupabaseApiProvider
import com.example.data.remote.SupabaseCraftsmanSync
import com.example.data.remote.SyncResult
import com.example.data.model.AppLanguage
import com.example.data.model.SortOption
import com.example.data.model.TradeCategories
import com.example.data.model.TradeCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class MainTab {
    EXPLORE,
    SAVED,
    ADD_WORKER
}

data class UiFilterState(
    val selectedCategoryKey: String = "ALL",
    val selectedWilayaCode: Int = 0, // 0 = All Wilayas
    val searchQuery: String = "",
    val sortOption: SortOption = SortOption.HIGHEST_RATED,
    val minRatingScore: Double = 0.0,
    val activeTab: MainTab = MainTab.EXPLORE,
    val selectedLanguage: AppLanguage = AppLanguage.AR
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CraftsmanRepository
    private val userRepository: com.example.data.UserRepository
    private lateinit var serviceRequestRepository: ServiceRequestRepository

    val currentUser = MutableStateFlow<com.example.data.db.UserEntity?>(null)
    val showAuthDialog = MutableStateFlow(false)
    val showServiceRequestsDialog = MutableStateFlow(false)
    val showSettingsDialog = MutableStateFlow(false)
    val requestNotificationsEnabled = MutableStateFlow(true)
    val notificationIntervalSeconds = MutableStateFlow(30)
    val craftsmenNotificationEnabled = MutableStateFlow(false)
    val isCraftsmanAvailable = MutableStateFlow(true)
    val passwordUpdateInProgress = MutableStateFlow(false)
    val passwordUpdateResult = MutableStateFlow<String?>(null) // null | success message | error message
    val serviceRequests = MutableStateFlow<List<ServiceRequestEntity>>(emptyList())
    val userNotification = MutableStateFlow<String?>(null)
    var pendingAuthAction: String? = null // "RATE", "REQUEST_SERVICE", "REQUESTS" or "ACCOUNT"

    init {
        val database = AppDatabase.getInstance(application)
        repository = CraftsmanRepository(database.craftsmanDao())
        userRepository = com.example.data.UserRepository(database.userDao(), application)
        serviceRequestRepository = ServiceRequestRepository(
            dao = database.serviceRequestDao(),
            userRepository = userRepository,
            context = application
        )

        viewModelScope.launch {
            currentUser.value = userRepository.getSavedUser()
        }

        // Start live request updates once the user is signed in.
        viewModelScope.launch {
            currentUser.collect { user ->
                if (user != null) startLiveRequestUpdates()
            }
        }

        // Remote data is optional during migration. Room remains the immediate source of UI data.
        viewModelScope.launch {
            when (val result = SupabaseCraftsmanSync(
                dao = database.craftsmanDao(),
                api = SupabaseApiProvider.create()
            ).refreshPublishedCraftsmen()) {
                is SyncResult.Success -> if (result.importedCount > 0) {
                    userNotification.value = "تم تحديث ${result.importedCount} حرفي من الخادم"
                }
                SyncResult.NotConfigured -> Unit
                is SyncResult.Failed -> {
                    // Keep the cached Room data usable; do not block the user on network failure.
                    userNotification.value = buildString {
                        append("تعذر تحديث البيانات، يتم عرض النسخة المحلية")
                        result.httpCode?.let { append(" (HTTP $it)") }
                        if (result.message.isNotBlank()) append(": ${result.message}")
                    }.take(220)
                }
            }
            // Background refresh: keep the cached list current while the user browses.
            startCraftsmenSyncLoop()
        }
    }

    val filterState = MutableStateFlow(UiFilterState())

    // Bookmarks Flow
    val bookmarkIds: StateFlow<List<String>> = repository.getBookmarkIds()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Raw Craftsmen Flow
    private val allCraftsmen: StateFlow<List<CraftsmanEntity>> = repository.getAllCraftsmen()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Pagination is intentionally kept in the ViewModel so Room remains the offline source.
    private val craftsmenPageSize = 20
    private val loadedCraftsmenCount = MutableStateFlow(craftsmenPageSize)

    // Filter and sort the cached data once per relevant state change, then expose only the loaded page.
    private val allFilteredCraftsmen: StateFlow<List<CraftsmanEntity>> = combine(
        allCraftsmen,
        filterState.debounce(250).stateIn(viewModelScope, SharingStarted.Eagerly, filterState.value),
        bookmarkIds
    ) { craftsmen, filter, bookmarks ->
        var result = craftsmen
        val bookmarkSet = bookmarks.toSet()

        // Tab filter
        if (filter.activeTab == MainTab.SAVED) {
            result = result.filter { bookmarkSet.contains(it.id) }
        }

        // Category filter
        if (filter.selectedCategoryKey != "ALL") {
            result = result.filter { it.categoryKey == filter.selectedCategoryKey }
        }

        // Wilaya filter
        if (filter.selectedWilayaCode != 0) {
            result = result.filter { it.wilayaCode == filter.selectedWilayaCode }
        }

        // Minimum rating filter
        if (filter.minRatingScore > 0) {
            result = result.filter { it.ratingScore >= filter.minRatingScore }
        }

        // Search query
        if (filter.searchQuery.isNotBlank()) {
            val q = filter.searchQuery.trim().lowercase()
            result = result.filter { worker ->
                worker.name.lowercase().contains(q) ||
                        worker.commune.lowercase().contains(q) ||
                        worker.description.lowercase().contains(q) ||
                        worker.skillsCsv.lowercase().contains(q) ||
                        TradeCategories.getByKey(worker.categoryKey).nameAr.contains(q) ||
                        TradeCategories.getByKey(worker.categoryKey).nameFr.lowercase().contains(q)
            }
        }

        // Helper ranking tier function:
        // Tier 1: Highly rated craftsmen (ratingScore >= 7.0 and ratingCount > 0)
        // Tier 2: Unrated / New craftsmen (ratingCount == 0 or ratingScore == 0.0)
        // Tier 3: Lower rated craftsmen (ratingScore < 7.0 with ratingCount > 0)
        fun getRankTier(worker: CraftsmanEntity): Int {
            return when {
                worker.ratingCount > 0 && worker.ratingScore >= 7.0 -> 1
                worker.ratingCount == 0 || worker.ratingScore == 0.0 -> 2
                else -> 3
            }
        }

        // Sorting
        when (filter.sortOption) {
            SortOption.HIGHEST_RATED -> result.sortedWith(
                compareBy<CraftsmanEntity> { getRankTier(it) }
                    .thenByDescending { it.ratingScore }
                    .thenByDescending { it.yearsExperience }
            )
            SortOption.MOST_REVIEWS -> result.sortedByDescending { it.ratingCount }
            SortOption.NEAREST -> result.sortedBy { it.distanceKmSimulated }
            SortOption.PRICE_LOW -> result.sortedBy { it.dailyRateDzd }
        }
    }.flowOn(Dispatchers.Default).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val filteredCraftsmen: StateFlow<List<CraftsmanEntity>> = combine(
        allFilteredCraftsmen,
        loadedCraftsmenCount
    ) { craftsmen, loadedCount ->
        craftsmen.take(loadedCount)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val hasMoreCraftsmen: StateFlow<Boolean> = combine(
        allFilteredCraftsmen,
        loadedCraftsmenCount
    ) { craftsmen, loadedCount ->
        loadedCount < craftsmen.size
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    private fun resetCraftsmenPagination() {
        loadedCraftsmenCount.value = craftsmenPageSize
    }

    fun loadNextCraftsmenPage() {
        if (hasMoreCraftsmen.value) {
            loadedCraftsmenCount.value += craftsmenPageSize
        }
    }

    // Selected Craftsman for Details
    val selectedCraftsmanId = MutableStateFlow<String?>(null)

    val selectedCraftsman: StateFlow<CraftsmanEntity?> = selectedCraftsmanId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.getCraftsmanById(id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Reviews for selected craftsman
    val selectedCraftsmanReviews: StateFlow<List<ReviewEntity>> = selectedCraftsmanId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getReviewsForCraftsman(id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Show Rating Dialog
    val showRatingDialog = MutableStateFlow(false)

    // Show Service Request Dialog
    val showServiceRequestDialog = MutableStateFlow(false)
    val dialogImageUrls = MutableStateFlow<List<String>>(emptyList())

    fun selectCategory(categoryKey: String) {
        resetCraftsmenPagination()
        filterState.value = filterState.value.copy(selectedCategoryKey = categoryKey)
    }

    fun selectWilaya(wilayaCode: Int) {
        resetCraftsmenPagination()
        filterState.value = filterState.value.copy(selectedWilayaCode = wilayaCode)
    }

    fun setSearchQuery(query: String) {
        resetCraftsmenPagination()
        filterState.value = filterState.value.copy(searchQuery = query)
    }

    fun setSortOption(sortOption: SortOption) {
        resetCraftsmenPagination()
        filterState.value = filterState.value.copy(sortOption = sortOption)
    }

    fun setMinRating(minScore: Double) {
        resetCraftsmenPagination()
        filterState.value = filterState.value.copy(minRatingScore = minScore)
    }

    fun setActiveTab(tab: MainTab) {
        resetCraftsmenPagination()
        filterState.value = filterState.value.copy(activeTab = tab)
    }

    fun setLanguage(language: AppLanguage) {
        filterState.value = filterState.value.copy(selectedLanguage = language)
    }

    fun openCraftsmanDetails(id: String) {
        selectedCraftsmanId.value = id
        viewModelScope.launch {
            SupabaseCraftsmanSync(
                dao = AppDatabase.getInstance(getApplication()).craftsmanDao(),
                api = SupabaseApiProvider.create()
            ).refreshReviewsForCraftsman(id)
        }
    }

    fun closeCraftsmanDetails() {
        selectedCraftsmanId.value = null
    }

    fun toggleBookmark(id: String) {
        viewModelScope.launch {
            repository.toggleBookmark(id, bookmarkIds.value)
        }
    }

    fun openRatingDialog() {
        if (currentUser.value == null) {
            pendingAuthAction = "RATE"
            showAuthDialog.value = true
        } else {
            showRatingDialog.value = true
        }
    }

    fun closeRatingDialog() {
        showRatingDialog.value = false
    }

    fun openServiceRequestDialog() {
        if (currentUser.value == null) {
            pendingAuthAction = "REQUEST_SERVICE"
            showAuthDialog.value = true
        } else {
            dialogImageUrls.value = emptyList()
            showServiceRequestDialog.value = true
        }
    }

    fun closeServiceRequestDialog() {
        showServiceRequestDialog.value = false
        dialogImageUrls.value = emptyList()
    }

    fun openServiceRequests() {
        if (currentUser.value == null) {
            pendingAuthAction = "REQUESTS"
            showAuthDialog.value = true
        } else {
            showServiceRequestsDialog.value = true
            refreshServiceRequests()
        }
    }

    fun closeServiceRequests() {
        showServiceRequestsDialog.value = false
    }

    fun refreshServiceRequests() {
        viewModelScope.launch {
            val synced = serviceRequestRepository.syncPendingRequests()
            serviceRequests.value = serviceRequestRepository.getForCurrentUser().first()
            val result = serviceRequestRepository.refreshCurrentUserRequests()
            serviceRequests.value = serviceRequestRepository.getForCurrentUser().first()
            if (synced > 0) userNotification.value = "تمت مزامنة $synced طلب"
            if (result.isFailure && serviceRequests.value.any { it.syncState != ServiceRequestEntity.SYNCED }) {
                userNotification.value = "توجد طلبات محلية بانتظار الاتصال"
            }
        }
    }

    fun retryPendingServiceRequests() {
        refreshServiceRequests()
    }

    fun updateServiceRequestStatus(remoteRequestId: String, newStatus: String) {
        viewModelScope.launch {
            val result = serviceRequestRepository.updateRequestStatusForCraftsman(remoteRequestId, newStatus)
            if (result.isSuccess) {
                userNotification.value = "تم تحديث حالة الطلب"
                refreshServiceRequests()
            } else {
                userNotification.value = "تعذر تحديث حالة الطلب"
            }
        }
    }

    // ------------------------------------------------------------------
    // Live request polling: periodically refreshes the current user's
    // requests (customer or craftsman role) and shows a system notification
    // when a new request arrives or a status changes.
    // ------------------------------------------------------------------
    // Periodic craftsmen sync: pulls published profiles from Supabase every 60s so
    // new craftsmen and rating changes appear without a manual refresh.
    private fun startCraftsmenSyncLoop() {
        viewModelScope.launch {
            val sync = SupabaseCraftsmanSync(
                dao = AppDatabase.getInstance(getApplication()).craftsmanDao(),
                api = SupabaseApiProvider.create()
            )
            while (true) {
                try {
                    kotlinx.coroutines.delay(60_000L)
                    when (val result = sync.refreshPublishedCraftsmen()) {
                        is SyncResult.Success -> if (result.importedCount > 0) {
                            userNotification.value = "تم تحديث ${result.importedCount} حرفي من الخادم"
                        }
                        else -> Unit // Keep browsing on cached Room data; next tick retries.
                    }
                } catch (e: kotlinx.coroutines.CancellationException) {
                    throw e
                } catch (_: Throwable) {
                    // Swallow transient failures; the next tick will retry.
                }
            }
        }
    }

    fun startLiveRequestUpdates() {
        viewModelScope.launch {
            var lastSnapshot = emptySet<String>()
            while (true) {
                try {
                    kotlinx.coroutines.delay(30_000L)
                    if (currentUser.value == null) continue
                    // Offline-first: retry sending locally-persisted requests and
                    // re-uploading their pending photos once connectivity returns.
                    runCatching { serviceRequestRepository.syncPendingRequests() }
                    val savedUser = userRepository.getSavedUser()
                    val isCraftsman = savedUser?.userType.equals("CRAFTSMAN", ignoreCase = true)
                    val result = if (isCraftsman) {
                        serviceRequestRepository.refreshForCraftsmanDirect()
                    } else {
                        serviceRequestRepository.refreshCurrentUserRequests()
                    }
                    if (result.isSuccess) {
                        val requests = if (isCraftsman) {
                            serviceRequestRepository.getAssignedRequests()
                        } else {
                            serviceRequestRepository.getForCurrentUser().first()
                        }
                        serviceRequests.value = requests
                        val snapshot = requests
                            .map { "${it.remoteId ?: it.id}:${it.status}" }
                            .toSet()
                        if (lastSnapshot.isNotEmpty() && snapshot != lastSnapshot) {
                            val newlyOpened = requests.firstOrNull { it.status == ServiceRequestEntity.STATUS_OPEN }
                            val statusChanged = requests.firstOrNull { req ->
                                val oldKey = lastSnapshot.firstOrNull { s -> s.startsWith("${req.remoteId ?: req.id}:") }
                                    ?.substringAfterLast(":")
                                oldKey != null && oldKey != req.status
                            }
                            val title = if (isCraftsman && newlyOpened != null) "طلب خدمة جديد!" else "تحديث في طلباتك"
                            val body = when {
                                isCraftsman && newlyOpened != null -> "لديك طلب خدمة جديد بانتظار ردك. افتح التطبيق للاطلاع على التفاصيل."
                                !isCraftsman && statusChanged != null -> "حالة طلبك تغيّرت إلى: ${requestStatusName(statusChanged.status)}. افتح التطبيق للاطلاع."
                                else -> "تحديث جديد على طلباتك. افتح التطبيق للاطلاع."
                            }
                            com.example.ui.notifications.RequestNotifier.notify(
                                getApplication(),
                                title = title,
                                body = body
                            )
                        }
                        lastSnapshot = snapshot
                    }
                } catch (e: kotlinx.coroutines.CancellationException) {
                    throw e
                } catch (_: Throwable) {
                    // Swallow transient failures; the next tick will retry.
                }
            }
        }
    }

    private fun requestStatusName(status: String): String = when (status) {
        ServiceRequestEntity.STATUS_OPEN -> "جديد"
        ServiceRequestEntity.STATUS_QUOTED -> "تسعير"
        ServiceRequestEntity.STATUS_ACCEPTED -> "مقبول"
        ServiceRequestEntity.STATUS_IN_PROGRESS -> "قيد التنفيذ"
        ServiceRequestEntity.STATUS_COMPLETED -> "مكتمل"
        ServiceRequestEntity.STATUS_CANCELLED -> "ملغي"
        else -> status
    }

    fun updateDialogImageUrls(urls: List<String>) {
        dialogImageUrls.value = urls
    }

    fun submitServiceRequest(
        categoryKey: String,
        wilayaCode: String,
        commune: String,
        description: String
    ) {
        val craftsmanId = selectedCraftsmanId.value ?: return
        val imageUrls = dialogImageUrls.value
        dialogImageUrls.value = emptyList()
        viewModelScope.launch {
            when (val result = serviceRequestRepository.createRequest(
                craftsmanId = craftsmanId,
                categoryKey = categoryKey,
                wilayaCode = wilayaCode,
                commune = commune,
                description = description,
                imageUrls = imageUrls
            )) {
                is ServiceRequestResult.Success -> userNotification.value = "تم إرسال طلب الخدمة إلى الحرفي"
                is ServiceRequestResult.SavedOffline -> userNotification.value = "تم حفظ الطلب محليًا وسيتم إرساله عند توفر الاتصال"
                is ServiceRequestResult.Error -> userNotification.value = when (result.message) {
                    "auth_required" -> "يجب تسجيل الدخول لإرسال طلب خدمة"
                    "description_invalid" -> "اكتب وصفًا بين 10 و2000 حرف"
                    else -> "تعذر إنشاء طلب الخدمة"
                }
            }
            showServiceRequestDialog.value = false
        }
    }

    fun openAuthDialog(action: String? = "ACCOUNT") {
        pendingAuthAction = action
        showAuthDialog.value = true
    }

    fun closeAuthDialog() {
        showAuthDialog.value = false
        pendingAuthAction = null
    }

    fun loginUser(email: String, password: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            when (val res = userRepository.loginUser(email, password)) {
                is com.example.data.AuthResult.Success -> {
                    currentUser.value = res.user
                    if (res.user.userType.equals("CRAFTSMAN", ignoreCase = true)) {
                        syncOwnedCraftsmanAfterLogin(res.user.id)
                    }
                    showAuthDialog.value = false
                    userNotification.value = "تم تسجيل الدخول بنجاح! مرحباً ${res.user.fullName}"
                    if (pendingAuthAction == "RATE") {
                        showRatingDialog.value = true
                    } else if (pendingAuthAction == "REQUEST_SERVICE") {
                        showServiceRequestDialog.value = true
                    } else if (pendingAuthAction == "REQUESTS") {
                        showServiceRequestsDialog.value = true
                        refreshServiceRequests()
                    }
                    pendingAuthAction = null
                    onResult(null)
                }
                is com.example.data.AuthResult.EmailConfirmationRequired -> {
                    onResult("error_email_confirmation_required")
                }
                is com.example.data.AuthResult.Error -> {
                    onResult(res.messageKey)
                }
            }
        }
    }

    fun registerUser(
        fullName: String,
        email: String,
        password: String,
        phone: String = "",
        wilayaCode: Int = 16,
        onResult: (String?) -> Unit
    ) {
        viewModelScope.launch {
            when (val res = userRepository.registerUser(fullName, email, password, "CLIENT", phone, wilayaCode)) {
                is com.example.data.AuthResult.Success -> {
                    currentUser.value = res.user
                    showAuthDialog.value = false
                    userNotification.value = "تم إنشاء حساب الزبون بنجاح! مرحباً ${res.user.fullName}"
                    if (pendingAuthAction == "RATE") {
                        showRatingDialog.value = true
                    } else if (pendingAuthAction == "REQUEST_SERVICE") {
                        showServiceRequestDialog.value = true
                    } else if (pendingAuthAction == "REQUESTS") {
                        showServiceRequestsDialog.value = true
                        refreshServiceRequests()
                    }
                    pendingAuthAction = null
                    onResult(null)
                }
                is com.example.data.AuthResult.EmailConfirmationRequired -> {
                    showAuthDialog.value = false
                    userNotification.value = Localization.authErrorMessage(
                        filterState.value.selectedLanguage,
                        "email_confirmation_sent"
                    )
                    pendingAuthAction = null
                    onResult(null)
                }
                is com.example.data.AuthResult.Error -> {
                    onResult(res.messageKey)
                }
            }
        }
    }

    fun registerCraftsmanUser(
        fullName: String,
        email: String,
        password: String,
        categoryKey: String,
        phone: String,
        whatsapp: String,
        wilayaCode: Int,
        commune: String,
        dailyRateDzd: Int,
        yearsExperience: Int,
        description: String,
        skillsCsv: String,
        onResult: (String?) -> Unit
    ) {
        viewModelScope.launch {
            when (val res = userRepository.registerUser(fullName, email, password, "CRAFTSMAN", phone, wilayaCode)) {
                is com.example.data.AuthResult.Success -> {
                    currentUser.value = res.user
                    // Add craftsman profile to directory
                    repository.registerNewCraftsman(
                        ownerId = res.user.id,
                        name = fullName,
                        categoryKey = categoryKey,
                        phone = phone,
                        whatsapp = whatsapp,
                        wilayaCode = wilayaCode,
                        commune = commune,
                        dailyRateDzd = dailyRateDzd,
                        yearsExperience = yearsExperience,
                        description = description,
                        skillsCsv = skillsCsv
                    )
                    val syncResult = SupabaseCraftsmanSync(
                        dao = AppDatabase.getInstance(getApplication()).craftsmanDao(),
                        api = SupabaseApiProvider.create(userRepository.getSupabaseAccessToken())
                    ).upsertOwnedCraftsman(
                        ownerId = res.user.id,
                        local = repository.getCraftsmanByOwnerId(res.user.id)
                            ?: error("local_craftsman_profile_missing")
                    )
                    showAuthDialog.value = false
                    userNotification.value = if (syncResult is SyncResult.Failed) {
                        "تم إنشاء الحساب وحفظ الملف محليًا، لكن تعذر رفعه للسحابة وسيعادَت المزامنة لاحقًا"
                    } else {
                        "تم تسجيلك كحرفي بنجاح! ملفك الحرفي متاح في دليل Herafi DZ"
                    }
                    pendingAuthAction = null
                    onResult(null)
                }
                is com.example.data.AuthResult.EmailConfirmationRequired -> {
                    repository.registerNewCraftsman(
                        ownerId = res.userId,
                        name = fullName,
                        categoryKey = categoryKey,
                        phone = phone,
                        whatsapp = whatsapp,
                        wilayaCode = wilayaCode,
                        commune = commune,
                        dailyRateDzd = dailyRateDzd,
                        yearsExperience = yearsExperience,
                        description = description,
                        skillsCsv = skillsCsv
                    )
                    showAuthDialog.value = false
                    userNotification.value = Localization.authErrorMessage(
                        filterState.value.selectedLanguage,
                        "email_confirmation_sent"
                    )
                    pendingAuthAction = null
                    onResult(null)
                }
                is com.example.data.AuthResult.Error -> {
                    onResult(res.messageKey)
                }
            }
        }
    }

    private fun syncOwnedCraftsmanAfterLogin(ownerId: String) {
        viewModelScope.launch {
            val sync = SupabaseCraftsmanSync(
                dao = AppDatabase.getInstance(getApplication()).craftsmanDao(),
                api = SupabaseApiProvider.create(userRepository.getSupabaseAccessToken())
            )
            val local = repository.getCraftsmanByOwnerId(ownerId)
            val result = if (local != null) {
                sync.upsertOwnedCraftsman(ownerId, local)
            } else {
                sync.restoreOwnedCraftsman(ownerId)
            }
            if (result is SyncResult.Failed) {
                userNotification.value = "تعذر مزامنة ملفك الحرفي، يتم الاحتفاظ بالنسخة المحلية"
            }
        }
    }

    fun logoutUser() {
        userRepository.logoutUser()
        currentUser.value = null
        showSettingsDialog.value = false
        userNotification.value = "تم تسجيل الخروج"
    }

    // ------------------------------------------------------------------
    // Settings screen: account (password change, logout), notification
    // preferences, and craftsman profile editing / reset.
    // ------------------------------------------------------------------
    fun openSettings() {
        val user = currentUser.value
        if (user != null && user.userType.equals("CRAFTSMAN", ignoreCase = true)) {
            viewModelScope.launch {
                val local = repository.getCraftsmanByOwnerId(user.id)
                isCraftsmanAvailable.value = local?.isAvailable ?: true
            }
        }
        showSettingsDialog.value = true
    }

    fun closeSettings() {
        showSettingsDialog.value = false
        passwordUpdateResult.value = null
    }

    fun changePassword(newPassword: String) {
        if (passwordUpdateInProgress.value) return
        viewModelScope.launch {
            passwordUpdateInProgress.value = true
            passwordUpdateResult.value = null
            val ok = runCatching { userRepository.changePassword(newPassword) }.getOrDefault(false)
            passwordUpdateResult.value = if (ok) {
                "تم تغيير كلمة المرور بنجاح"
            } else {
                "تعذر تغيير كلمة المرور. تحقق من اتصالك بالإنترنت وحاول مرة أخرى"
            }
            passwordUpdateInProgress.value = false
        }
    }

    fun toggleRequestNotifications(enabled: Boolean) {
        requestNotificationsEnabled.value = enabled
    }

    fun toggleCraftsmanNotifications(enabled: Boolean) {
        craftsmenNotificationEnabled.value = enabled
    }

    fun updateNotificationInterval(seconds: Int) {
        notificationIntervalSeconds.value = seconds
    }

    fun toggleCraftsmanAvailability(available: Boolean) {
        isCraftsmanAvailable.value = available
        viewModelScope.launch {
            val uid = userRepository.getCurrentUserId() ?: return@launch
            val local = repository.getCraftsmanByOwnerId(uid) ?: run {
                repository.registerNewCraftsman(
                    ownerId = uid,
                    name = currentUser.value?.fullName ?: "حرفي",
                    categoryKey = "BUILDER",
                    phone = currentUser.value?.phone ?: "",
                    whatsapp = "",
                    wilayaCode = currentUser.value?.wilayaCode ?: 16,
                    commune = "",
                    dailyRateDzd = 0,
                    yearsExperience = 0,
                    description = "",
                    skillsCsv = ""
                )
                repository.getCraftsmanByOwnerId(uid)
            } ?: return@launch
            repository.updateOwnedCraftsmanAvailability(local.id, available)
            val api = SupabaseApiProvider.create(userRepository.getSupabaseAccessToken())
            val body = com.example.data.remote.UpsertCraftsmanBody(
                owner_id = uid,
                name = local.name,
                category_key = local.categoryKey,
                wilaya_code = local.wilayaCode.coerceIn(0, 58).toString(),
                commune = local.commune,
                phone = local.phone,
                whatsapp = local.whatsapp.ifBlank { local.phone },
                description = local.description,
                daily_rate_dzd = local.dailyRateDzd,
                years_experience = local.yearsExperience,
                skills_csv = local.skillsCsv,
                status = if (available) "published" else "pending",
                is_available = available
            )
            val syncResult = runCatching { api?.upsertOwnedCraftsman(profile = body) }
            if (syncResult.isFailure || syncResult.getOrNull() == null) {
                userNotification.value = "تم تحديث التوفر محليًا؛ تعذر مزامنته مع الخادم"
            } else {
                userNotification.value = if (available) "أصبحت متاحًا لاستقبال الطلبات" else "أصبحت غير متاح مؤقتًا"
            }
        }
    }

    fun updateCraftsmanField(field: String, value: String) {
        if (field != "profile") return
        val parts = value.split("|")
        if (parts.size != 6) return
        val uid = userRepository.getCurrentUserId() ?: return
        val name = parts.getOrNull(0).orEmpty()
        val phone = parts.getOrNull(1).orEmpty()
        val wilaya = parts.getOrNull(2).orEmpty()
        val commune = parts.getOrNull(3).orEmpty()
        val rate = parts.getOrNull(4).orEmpty()
        val description = parts.getOrNull(5).orEmpty()
        viewModelScope.launch {
            val local = repository.getCraftsmanByOwnerId(uid) ?: run {
                repository.registerNewCraftsman(
                    ownerId = uid,
                    name = name.ifBlank { currentUser.value?.fullName ?: "حرفي" },
                    categoryKey = "BUILDER",
                    phone = phone,
                    whatsapp = "",
                    wilayaCode = wilaya.toIntOrNull()?.coerceIn(1, 58) ?: 16,
                    commune = commune,
                    dailyRateDzd = rate.toIntOrNull()?.coerceIn(0, 10_000_000) ?: 0,
                    yearsExperience = 0,
                    description = description,
                    skillsCsv = ""
                )
                repository.getCraftsmanByOwnerId(uid)
            } ?: return@launch
            val available = local.isAvailable
            val newName = name.ifBlank { local.name }
            val newPhone = phone.ifBlank { local.phone }
            val newWilaya = wilaya.toIntOrNull()?.coerceIn(1, 58) ?: local.wilayaCode
            val newCommune = commune.ifBlank { local.commune }
            val newRate = rate.toIntOrNull()?.coerceIn(0, 10_000_000) ?: local.dailyRateDzd
            val newDescription = description.ifBlank { local.description }
            repository.updateOwnedCraftsmanProfile(
                id = local.id,
                name = newName,
                phone = newPhone,
                wilayaCode = newWilaya,
                commune = newCommune,
                dailyRateDzd = newRate,
                description = newDescription,
                available = available
            )
            val api = SupabaseApiProvider.create(userRepository.getSupabaseAccessToken())
            val body = com.example.data.remote.UpsertCraftsmanBody(
                owner_id = uid,
                name = newName,
                category_key = local.categoryKey,
                wilaya_code = newWilaya.toString(),
                commune = newCommune,
                phone = newPhone,
                whatsapp = local.whatsapp.ifBlank { newPhone },
                description = newDescription,
                daily_rate_dzd = newRate,
                years_experience = local.yearsExperience,
                skills_csv = local.skillsCsv,
                status = if (available) "published" else "pending",
                is_available = available
            )
            val syncResult = runCatching { api?.upsertOwnedCraftsman(profile = body) }
            if (syncResult.isFailure || syncResult.getOrNull() == null) {
                userNotification.value = "تم حفظ التعديلات محليًا؛ تعذر مزامنتها مع الخادم"
            } else {
                userNotification.value = "تم تحديث ملفك الحرفي بنجاح"
            }
        }
    }

    fun resetCraftsman() {
        val uid = userRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            val local = repository.getCraftsmanByOwnerId(uid)
            if (local != null) {
                repository.deleteOwnedCraftsman(local.id)
            }
            // REST DELETE on craftsmen is not reliably supported; reset the remote row to
            // the unverified "pending" state so it disappears from the published directory.
            val api = SupabaseApiProvider.create(userRepository.getSupabaseAccessToken())
            if (local != null) {
                val pendingBody = com.example.data.remote.UpsertCraftsmanBody(
                    owner_id = uid,
                    name = local.name,
                    category_key = local.categoryKey,
                    wilaya_code = local.wilayaCode.coerceIn(1, 58).toString(),
                    commune = local.commune,
                    phone = local.phone,
                    whatsapp = local.whatsapp.ifBlank { null },
                    description = local.description,
                    daily_rate_dzd = local.dailyRateDzd,
                    years_experience = local.yearsExperience,
                    skills_csv = local.skillsCsv,
                    status = "pending",
                    is_available = false
                )
                runCatching { api?.updateOwnedCraftsman(ownerId = "eq.$uid", body = pendingBody) }
            }
            userNotification.value = "تم إعادة تعيين ملفك الحرفي إلى الحالة الأولية"
            closeSettings()
        }
    }

    fun submitRating(
        reviewerName: String,
        scoreTen: Double,
        comment: String,
        qualityScore: Double,
        punctualityScore: Double,
        priceScore: Double,
        tagsCsv: String
    ) {
        val workerId = selectedCraftsmanId.value ?: return
        viewModelScope.launch {
            // Block self-review on the UI side as well (server trigger also enforces it)
            val selectedEntity = selectedCraftsman.value
            if (selectedEntity?.ownerId != null && selectedEntity.ownerId == currentUser.value?.id) {
                userNotification.value = "لا يمكنك تقييم ملفك الخاص"
                showRatingDialog.value = false
                return@launch
            }
            repository.submitReview(
                craftsmanId = workerId,
                reviewerName = reviewerName,
                scoreTen = scoreTen,
                comment = comment,
                qualityScore = qualityScore,
                punctualityScore = punctualityScore,
                priceScore = priceScore,
                tagsCsv = tagsCsv,
                currentCraftsman = selectedEntity,
                currentUserId = currentUser.value?.id
            )
            showRatingDialog.value = false
            userNotification.value = "تمت إضافة تقييمك بنجاح! شكراً لك."
        }
    }

    fun registerWorker(
        name: String,
        categoryKey: String,
        phone: String,
        whatsapp: String,
        wilayaCode: Int,
        commune: String,
        dailyRateDzd: Int,
        yearsExperience: Int,
        description: String,
        skillsCsv: String
    ) {
        viewModelScope.launch {
            repository.registerNewCraftsman(
                name = name,
                categoryKey = categoryKey,
                phone = phone,
                whatsapp = whatsapp,
                wilayaCode = wilayaCode,
                commune = commune,
                dailyRateDzd = dailyRateDzd,
                yearsExperience = yearsExperience,
                description = description,
                skillsCsv = skillsCsv
            )
            filterState.value = filterState.value.copy(activeTab = MainTab.EXPLORE)
            userNotification.value = "تم تسجيل الحرفي بنجاح وإضافته إلى الدليل!"
        }
    }

    fun clearNotification() {
        userNotification.value = null
    }

    fun clearAllFilters() {
        filterState.value = filterState.value.copy(
            selectedCategoryKey = "ALL",
            selectedWilayaCode = 0,
            searchQuery = "",
            minRatingScore = 0.0,
            sortOption = SortOption.HIGHEST_RATED
        )
    }
}
