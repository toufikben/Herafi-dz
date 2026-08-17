package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collect
import com.example.R
import com.example.data.model.AppLanguage
import com.example.ui.Localization
import com.example.ui.MainTab
import com.example.ui.MainViewModel
import com.example.ui.components.AuthDialog
import com.example.ui.components.CraftsmanCard
import com.example.ui.components.CraftsmanDetailSheet
import com.example.ui.components.FilterBar
import com.example.ui.components.shareCraftsmanProfile
import com.example.ui.components.RatingSubmissionDialog
import com.example.ui.components.ServiceRequestDialog
import com.example.ui.components.ServiceRequestsDialog
import com.example.ui.components.TradeCategoryChips
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val craftsmen by viewModel.filteredCraftsmen.collectAsStateWithLifecycle()
    val hasMoreCraftsmen by viewModel.hasMoreCraftsmen.collectAsStateWithLifecycle()
    val bookmarkIds by viewModel.bookmarkIds.collectAsStateWithLifecycle()
    val bookmarkIdSet = remember(bookmarkIds) { bookmarkIds.toSet() }
    val selectedCraftsman by viewModel.selectedCraftsman.collectAsStateWithLifecycle()
    val selectedReviews by viewModel.selectedCraftsmanReviews.collectAsStateWithLifecycle()
    val showRatingDialog by viewModel.showRatingDialog.collectAsStateWithLifecycle()
    val showServiceRequestDialog by viewModel.showServiceRequestDialog.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val showAuthDialog by viewModel.showAuthDialog.collectAsStateWithLifecycle()
    val showServiceRequestsDialog by viewModel.showServiceRequestsDialog.collectAsStateWithLifecycle()
    val serviceRequests by viewModel.serviceRequests.collectAsStateWithLifecycle()
    val notificationMessage by viewModel.userNotification.collectAsStateWithLifecycle()

    val language = filterState.selectedLanguage
    val layoutDirection = if (language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    val craftsmanListState = rememberLazyListState()

    // Prefetch the next batch as soon as the user is 5 items away from the end
    // of the currently loaded list, so scrolling never stalls at the bottom.
    LaunchedEffect(craftsmanListState, craftsmen.size, hasMoreCraftsmen) {
        var requestedForSize = -1
        snapshotFlow {
            craftsmanListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        }.collect { lastVisibleIndex ->
            val nearEnd = lastVisibleIndex >= craftsmen.size - 5
            if (nearEnd && hasMoreCraftsmen && requestedForSize != craftsmen.size) {
                requestedForSize = craftsmen.size
                viewModel.loadNextCraftsmenPage()
            }
        }
    }

    // Toast notifications
    LaunchedEffect(notificationMessage) {
        notificationMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            viewModel.clearNotification()
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GoldAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Handyman,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = Localization.appTitle(language),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = Localization.appSubTitle(language),
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyPrimary),
                    actions = {
                        if (currentUser != null) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White.copy(alpha = 0.15f),
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clickable { viewModel.openAuthDialog("ACCOUNT") }
                                    .testTag("user_profile_chip")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = null,
                                        tint = GoldAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = currentUser!!.fullName.take(12),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        } else {
                            IconButton(
                                onClick = { viewModel.openAuthDialog("ACCOUNT") },
                                modifier = Modifier.testTag("login_icon_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = Localization.loginTitle(language),
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    modifier = Modifier.testTag("app_top_bar")
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("bottom_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = filterState.activeTab == MainTab.EXPLORE,
                        onClick = { viewModel.setActiveTab(MainTab.EXPLORE) },
                        icon = { Icon(Icons.Default.Handyman, contentDescription = null) },
                        label = { Text(Localization.allWorkersTab(language)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyPrimary,
                            selectedTextColor = NavyPrimary
                        ),
                        modifier = Modifier.testTag("tab_explore")
                    )

                    NavigationBarItem(
                        selected = filterState.activeTab == MainTab.SAVED,
                        onClick = { viewModel.setActiveTab(MainTab.SAVED) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (bookmarkIds.isNotEmpty()) {
                                        Badge { Text(bookmarkIds.size.toString()) }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (filterState.activeTab == MainTab.SAVED) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null
                                )
                            }
                        },
                        label = { Text(Localization.bookmarksTab(language)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GoldAccent,
                            selectedTextColor = GoldAccent
                        ),
                        modifier = Modifier.testTag("tab_saved")
                    )

                    NavigationBarItem(
                        selected = filterState.activeTab == MainTab.ADD_WORKER,
                        onClick = { viewModel.setActiveTab(MainTab.ADD_WORKER) },
                        icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                        label = { Text(Localization.addWorkerButton(language)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyPrimary,
                            selectedTextColor = NavyPrimary
                        ),
                        modifier = Modifier.testTag("tab_add_worker")
                    )
                }
            },
            floatingActionButton = {
                if (filterState.activeTab == MainTab.EXPLORE) {
                    FloatingActionButton(
                        onClick = { viewModel.setActiveTab(MainTab.ADD_WORKER) },
                        containerColor = GoldAccent,
                        contentColor = Color.White,
                        modifier = Modifier.testTag("fab_add_worker")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Worker")
                    }
                }
            },
            modifier = modifier
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (filterState.activeTab) {
                    MainTab.EXPLORE, MainTab.SAVED -> {
                        LazyColumn(
                            state = craftsmanListState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            // Hero Banner
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_craftsmen_hero),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(
                                                        Color.Black.copy(alpha = 0.5f),
                                                        NavyPrimary.copy(alpha = 0.85f)
                                                    )
                                                )
                                            )
                                    )

                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = if (language == AppLanguage.AR) "ابحث عن أفضل الحرفيين والعمال في الجزائر"
                                            else "Trouvez les meilleurs artisans en Algérie",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (language == AppLanguage.AR) "تقييم حقيقي 10/10 لاتخاذ القرار الصحيح لأعمالك"
                                            else "Evaluations 10/10 vérifiées par les utilisateurs",
                                            color = GoldAccent,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }

                            // Filter & Search Controls
                            item {
                                FilterBar(
                                    searchQuery = filterState.searchQuery,
                                    selectedWilayaCode = filterState.selectedWilayaCode,
                                    selectedSortOption = filterState.sortOption,
                                    selectedLanguage = language,
                                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                    onWilayaSelect = { viewModel.selectWilaya(it) },
                                    onSortSelect = { viewModel.setSortOption(it) },
                                    onLanguageSelect = { viewModel.setLanguage(it) },
                                    onResetFilters = { viewModel.clearAllFilters() }
                                )
                            }

                            // Trade Category Horizontal Chips
                            item {
                                TradeCategoryChips(
                                    selectedCategoryKey = filterState.selectedCategoryKey,
                                    language = language,
                                    onSelectCategory = { viewModel.selectCategory(it) }
                                )
                            }

                            // Section Title & Result Count
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (filterState.activeTab == MainTab.SAVED) Localization.bookmarksTab(language)
                                        else Localization.allWorkersTab(language),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = "${craftsmen.size} ${if (language == AppLanguage.AR) "حرفي" else "artisans"}",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            // Empty state
                            if (craftsmen.isEmpty()) {
                                item {
                                    Card(
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(32.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.SearchOff,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(48.dp)
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = Localization.noWorkersFound(language),
                                                style = MaterialTheme.typography.titleMedium,
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Button(
                                                onClick = { viewModel.clearAllFilters() },
                                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                                            ) {
                                                Icon(Icons.Default.Refresh, contentDescription = null)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(Localization.clearFilters(language))
                                            }
                                        }
                                    }
                                }
                            } else {
                                items(
                                    items = craftsmen,
                                    key = { it.id }
                                ) { craftsman ->
                                    val isBookmarked = bookmarkIdSet.contains(craftsman.id)
                                    CraftsmanCard(
                                        craftsman = craftsman,
                                        isBookmarked = isBookmarked,
                                        language = language,
                                        onToggleBookmark = { viewModel.toggleBookmark(craftsman.id) },
                                        onClickDetails = { viewModel.openCraftsmanDetails(craftsman.id) },
                                        onOpenRatingDialog = { viewModel.openRatingDialog() },
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            if (hasMoreCraftsmen) {
                                item(key = "craftsmen_loading_more") {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            strokeWidth = 2.dp,
                                            color = GoldAccent
                                        )
                                    }
                                }
                            }
                        }
                    }

                    MainTab.ADD_WORKER -> {
                        AddWorkerScreen(
                            language = language,
                            onRegisterWorker = { name, catKey, phone, wa, wilaya, commune, rate, exp, desc, skills ->
                                viewModel.registerWorker(name, catKey, phone, wa, wilaya, commune, rate, exp, desc, skills)
                            }
                        )
                    }
                }

                // Craftsman Detail Modal Sheet
                selectedCraftsman?.let { craftsman ->
                    CraftsmanDetailSheet(
                        craftsman = craftsman,
                        reviews = selectedReviews,
                        isBookmarked = bookmarkIdSet.contains(craftsman.id),
                        language = language,
                        onDismiss = { viewModel.closeCraftsmanDetails() },
                        onToggleBookmark = { viewModel.toggleBookmark(craftsman.id) },
                        onShareProfile = { shareCraftsmanProfile(context, craftsman, language) },
                        onOpenRatingDialog = { viewModel.openRatingDialog() },
                        onRequestService = { viewModel.openServiceRequestDialog() }
                    )
                }

                if (showServiceRequestDialog && selectedCraftsman != null) {
                    ServiceRequestDialog(
                        craftsman = selectedCraftsman!!,
                        language = language,
                        onDismiss = { viewModel.closeServiceRequestDialog() },
                        onSubmit = { categoryKey, wilayaCode, commune, description ->
                            viewModel.submitServiceRequest(categoryKey, wilayaCode, commune, description)
                        }
                    )
                }

                // Auth Dialog
                if (showAuthDialog) {
                    AuthDialog(
                        language = language,
                        currentUser = currentUser,
                        pendingAction = viewModel.pendingAuthAction,
                        onDismiss = { viewModel.closeAuthDialog() },
                        onLogout = { viewModel.logoutUser() },
                        onOpenServiceRequests = { viewModel.openServiceRequests() },
                        onLogin = { email, password, onError ->
                            viewModel.loginUser(email, password, onError)
                        },
                        onRegister = { fullName, email, password, phone, wilayaCode, onError ->
                            viewModel.registerUser(fullName, email, password, phone, wilayaCode, onResult = onError)
                        },
                        onRegisterCraftsman = { fullName, email, password, categoryKey, phone, whatsapp, wilayaCode, commune, dailyRateDzd, yearsExperience, description, skillsCsv, onError ->
                            viewModel.registerCraftsmanUser(
                                fullName, email, password, categoryKey, phone, whatsapp, wilayaCode, commune, dailyRateDzd, yearsExperience, description, skillsCsv, onResult = onError
                            )
                        }
                    )
                }

                if (showServiceRequestsDialog) {
                    ServiceRequestsDialog(
                        requests = serviceRequests,
                        language = language,
                        onDismiss = { viewModel.closeServiceRequests() },
                        onRefresh = { viewModel.refreshServiceRequests() },
                        onRetry = { viewModel.retryPendingServiceRequests() },
                        isCraftsman = currentUser?.userType == "craftsman",
                        onUpdateStatus = { remoteRequestId, newStatus ->
                            viewModel.updateServiceRequestStatus(remoteRequestId, newStatus)
                        }
                    )
                }

                // Rating Submission Dialog
                if (showRatingDialog && selectedCraftsman != null) {
                    RatingSubmissionDialog(
                        craftsman = selectedCraftsman!!,
                        language = language,
                        currentUser = currentUser,
                        onDismiss = { viewModel.closeRatingDialog() },
                        onSubmitRating = { name, score, comment, qual, punct, price, tags ->
                            viewModel.submitRating(name, score, comment, qual, punct, price, tags)
                        }
                    )
                }
            }
        }
    }
}
