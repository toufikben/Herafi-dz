package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CraftsmanRepository
import com.example.data.db.AppDatabase
import com.example.data.db.CraftsmanEntity
import com.example.data.db.ReviewEntity
import com.example.data.model.AppLanguage
import com.example.data.model.SortOption
import com.example.data.model.TradeCategories
import com.example.data.model.TradeCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
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

    val currentUser = MutableStateFlow<com.example.data.db.UserEntity?>(null)
    val showAuthDialog = MutableStateFlow(false)
    var pendingAuthAction: String? = null // "RATE" or "ACCOUNT"

    init {
        val database = AppDatabase.getInstance(application)
        repository = CraftsmanRepository(database.craftsmanDao())
        userRepository = com.example.data.UserRepository(database.userDao(), application)

        viewModelScope.launch {
            currentUser.value = userRepository.getSavedUser()
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

    // Filtered Craftsmen
    val filteredCraftsmen: StateFlow<List<CraftsmanEntity>> = combine(
        allCraftsmen,
        filterState,
        bookmarkIds
    ) { craftsmen, filter, bookmarks ->
        var result = craftsmen

        // Tab filter
        if (filter.activeTab == MainTab.SAVED) {
            result = result.filter { bookmarks.contains(it.id) }
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
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

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

    // Show Toast Message
    val userNotification = MutableStateFlow<String?>(null)

    fun selectCategory(categoryKey: String) {
        filterState.value = filterState.value.copy(selectedCategoryKey = categoryKey)
    }

    fun selectWilaya(wilayaCode: Int) {
        filterState.value = filterState.value.copy(selectedWilayaCode = wilayaCode)
    }

    fun setSearchQuery(query: String) {
        filterState.value = filterState.value.copy(searchQuery = query)
    }

    fun setSortOption(sortOption: SortOption) {
        filterState.value = filterState.value.copy(sortOption = sortOption)
    }

    fun setMinRating(minScore: Double) {
        filterState.value = filterState.value.copy(minRatingScore = minScore)
    }

    fun setActiveTab(tab: MainTab) {
        filterState.value = filterState.value.copy(activeTab = tab)
    }

    fun setLanguage(language: AppLanguage) {
        filterState.value = filterState.value.copy(selectedLanguage = language)
    }

    fun openCraftsmanDetails(id: String) {
        selectedCraftsmanId.value = id
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
                    showAuthDialog.value = false
                    userNotification.value = "تم تسجيل الدخول بنجاح! مرحباً ${res.user.fullName}"
                    if (pendingAuthAction == "RATE") {
                        showRatingDialog.value = true
                    }
                    pendingAuthAction = null
                    onResult(null)
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
                    }
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
                    userNotification.value = "تم تسجيلك كحرفي بنجاح! ملفك الحرفي أخيرًا متاح في دليل Herafi DZ"
                    pendingAuthAction = null
                    onResult(null)
                }
                is com.example.data.AuthResult.Error -> {
                    onResult(res.messageKey)
                }
            }
        }
    }

    fun logoutUser() {
        userRepository.logoutUser()
        currentUser.value = null
        userNotification.value = "تم تسجيل الخروج"
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
            repository.submitReview(
                craftsmanId = workerId,
                reviewerName = reviewerName,
                scoreTen = scoreTen,
                comment = comment,
                qualityScore = qualityScore,
                punctualityScore = punctualityScore,
                priceScore = priceScore,
                tagsCsv = tagsCsv,
                currentCraftsman = selectedCraftsman.value
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
