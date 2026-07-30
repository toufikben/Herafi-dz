package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.db.UserEntity
import com.example.data.model.AlgeriaWilayas
import com.example.data.model.AppLanguage
import com.example.data.model.TradeCategories
import com.example.ui.Localization
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthDialog(
    language: AppLanguage,
    currentUser: UserEntity? = null,
    pendingAction: String?,
    onDismiss: () -> Unit,
    onLogout: () -> Unit = {},
    onLogin: (email: String, password: String, onError: (String?) -> Unit) -> Unit,
    onRegister: (fullName: String, email: String, password: String, onError: (String?) -> Unit) -> Unit,
    onRegisterCraftsman: (
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
        onError: (String?) -> Unit
    ) -> Unit = { _, _, _, _, _, _, _, _, _, _, _, _, _ -> }
) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("CLIENT") } // "CLIENT" or "CRAFTSMAN"

    // Client/Common state
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var phone by remember { mutableStateOf("") }
    var selectedWilayaCode by remember { mutableIntStateOf(16) } // Algiers default

    // Craftsman state
    var selectedCategoryKey by remember { mutableStateOf(TradeCategories.PLUMBER.key) }
    var whatsapp by remember { mutableStateOf("") }
    var commune by remember { mutableStateOf("") }
    var dailyRateDzd by remember { mutableStateOf("3000") }
    var yearsExperience by remember { mutableStateOf("5") }
    var description by remember { mutableStateOf("") }
    var skillsCsv by remember { mutableStateOf("") }

    // Dropdown expanded states
    var wilayaDropdownExpanded by remember { mutableStateOf(false) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    var errorMessageKey by remember { mutableStateOf<String?>(null) }

    val tradeCategoryList = remember { TradeCategories.list.filter { it.key != "ALL" } }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp)
                .testTag("auth_dialog_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (currentUser != null) Localization.accountProfileTitle(language)
                        else if (isRegisterMode) Localization.signUpTitle(language)
                        else Localization.loginTitle(language),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("close_auth_dialog_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = Localization.cancel(language)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (currentUser != null) {
                    // LOGGED IN PROFILE VIEW
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(NavyPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (currentUser.userType == "CRAFTSMAN") Icons.Default.Handyman else Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = currentUser.fullName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = currentUser.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (currentUser.userType == "CRAFTSMAN") GoldAccent.copy(alpha = 0.2f) else EmeraldGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (currentUser.userType == "CRAFTSMAN") {
                                    if (language == AppLanguage.AR) "حساب حرفي معتمد 🛠️" else "Compte Artisan 🛠️"
                                } else {
                                    if (language == AppLanguage.AR) "حساب زبون مسجل 👤" else "Compte Client 👤"
                                },
                                color = if (currentUser.userType == "CRAFTSMAN") NavyPrimary else EmeraldGreen,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                onLogout()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("logout_button")
                        ) {
                            Text(
                                text = Localization.logoutButton(language),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // NOT LOGGED IN FORM
                    if (pendingAction == "RATE") {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EmeraldGreen.copy(alpha = 0.12f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = EmeraldGreen,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = Localization.loginRequiredToRateNotice(language),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EmeraldGreen,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    // Mode Tabs (Login / Register)
                    TabRow(
                        selectedTabIndex = if (isRegisterMode) 1 else 0,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[if (isRegisterMode) 1 else 0]),
                                color = NavyPrimary
                            )
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .fillMaxWidth()
                    ) {
                        Tab(
                            selected = !isRegisterMode,
                            onClick = {
                                isRegisterMode = false
                                errorMessageKey = null
                            },
                            text = {
                                Text(
                                    text = Localization.loginButton(language),
                                    fontWeight = if (!isRegisterMode) FontWeight.Bold else FontWeight.Normal,
                                    color = if (!isRegisterMode) NavyPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.testTag("tab_login")
                        )
                        Tab(
                            selected = isRegisterMode,
                            onClick = {
                                isRegisterMode = true
                                errorMessageKey = null
                            },
                            text = {
                                Text(
                                    text = Localization.signUpButton(language),
                                    fontWeight = if (isRegisterMode) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isRegisterMode) NavyPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.testTag("tab_signup")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Scrollable area for form fields
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Error Banner
                        AnimatedVisibility(visible = errorMessageKey != null) {
                            errorMessageKey?.let { key ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 10.dp)
                                ) {
                                    Text(
                                        text = Localization.authErrorMessage(language, key),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.padding(10.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        // ROLE SELECTION when registering
                        if (isRegisterMode) {
                            Text(
                                text = Localization.selectRolePrompt(language),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Client Card
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (selectedRole == "CLIENT") NavyPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(
                                            width = if (selectedRole == "CLIENT") 2.dp else 1.dp,
                                            color = if (selectedRole == "CLIENT") NavyPrimary else Color.Transparent,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            selectedRole = "CLIENT"
                                            errorMessageKey = null
                                        }
                                        .testTag("role_client_btn")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = if (selectedRole == "CLIENT") NavyPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = Localization.clientRole(language),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = if (selectedRole == "CLIENT") FontWeight.Bold else FontWeight.Medium,
                                            color = if (selectedRole == "CLIENT") NavyPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                // Craftsman Card
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (selectedRole == "CRAFTSMAN") GoldAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(
                                            width = if (selectedRole == "CRAFTSMAN") 2.dp else 1.dp,
                                            color = if (selectedRole == "CRAFTSMAN") NavyPrimary else Color.Transparent,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            selectedRole = "CRAFTSMAN"
                                            errorMessageKey = null
                                        }
                                        .testTag("role_craftsman_btn")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Handyman,
                                            contentDescription = null,
                                            tint = if (selectedRole == "CRAFTSMAN") NavyPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = Localization.craftsmanRole(language),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = if (selectedRole == "CRAFTSMAN") FontWeight.Bold else FontWeight.Medium,
                                            color = if (selectedRole == "CRAFTSMAN") NavyPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        // COMMON / CLIENT FIELDS
                        if (isRegisterMode) {
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = {
                                    fullName = it
                                    errorMessageKey = null
                                },
                                label = {
                                    Text(
                                        if (selectedRole == "CRAFTSMAN") {
                                            if (language == AppLanguage.AR) "اسم الحرفي / الورشة بالكامل" else "Nom complet / Atelier"
                                        } else Localization.fullName(language)
                                    )
                                },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Person, contentDescription = null)
                                },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NavyPrimary,
                                    focusedLabelColor = NavyPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_full_name_input")
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // EMAIL FIELD
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                errorMessageKey = null
                            },
                            label = { Text(Localization.emailLabel(language)) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Email, contentDescription = null)
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NavyPrimary,
                                focusedLabelColor = NavyPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_email_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // PASSWORD FIELD
                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                errorMessageKey = null
                            },
                            label = { Text(Localization.passwordLabel(language)) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = if (isRegisterMode && selectedRole == "CRAFTSMAN") ImeAction.Next else ImeAction.Done
                            ),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NavyPrimary,
                                focusedLabelColor = NavyPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_password_input")
                        )

                        // CRAFTSMAN SPECIFIC EXTRA FIELDS
                        if (isRegisterMode && selectedRole == "CRAFTSMAN") {
                            Spacer(modifier = Modifier.height(12.dp))

                            // TRADE CATEGORY DROPDOWN
                            Text(
                                text = if (language == AppLanguage.AR) "اختر الحرفة / التخصص:" else "Spécialité / Métier :",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )

                            ExposedDropdownMenuBox(
                                expanded = categoryDropdownExpanded,
                                onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                val currentCat = tradeCategoryList.find { it.key == selectedCategoryKey } ?: TradeCategories.PLUMBER
                                val catName = when (language) {
                                    AppLanguage.AR -> currentCat.nameAr
                                    AppLanguage.FR -> currentCat.nameFr
                                    AppLanguage.EN -> currentCat.nameEn
                                }

                                OutlinedTextField(
                                    value = catName,
                                    onValueChange = {},
                                    readOnly = true,
                                    leadingIcon = { Icon(imageVector = currentCat.icon, contentDescription = null, tint = NavyPrimary) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NavyPrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                        .testTag("trade_category_dropdown")
                                )

                                ExposedDropdownMenu(
                                    expanded = categoryDropdownExpanded,
                                    onDismissRequest = { categoryDropdownExpanded = false }
                                ) {
                                    tradeCategoryList.forEach { cat ->
                                        val name = when (language) {
                                            AppLanguage.AR -> cat.nameAr
                                            AppLanguage.FR -> cat.nameFr
                                            AppLanguage.EN -> cat.nameEn
                                        }
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = cat.icon, contentDescription = null, modifier = Modifier.size(20.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(name)
                                                }
                                            },
                                            onClick = {
                                                selectedCategoryKey = cat.key
                                                categoryDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // PHONE & WHATSAPP
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text(Localization.phoneLabel(language)) },
                                leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("craftsman_phone_input")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = whatsapp,
                                onValueChange = { whatsapp = it },
                                label = { Text(if (language == AppLanguage.AR) "رقم الواتساب (اختياري)" else "Numéro WhatsApp") },
                                leadingIcon = { Icon(imageVector = Icons.Default.Chat, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("craftsman_whatsapp_input")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // WILAYA DROPDOWN
                            Text(
                                text = Localization.wilayaFilterLabel(language),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )

                            ExposedDropdownMenuBox(
                                expanded = wilayaDropdownExpanded,
                                onExpandedChange = { wilayaDropdownExpanded = !wilayaDropdownExpanded },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                val currentWilaya = AlgeriaWilayas.getByCode(selectedWilayaCode)
                                OutlinedTextField(
                                    value = AlgeriaWilayas.getNameForLanguage(currentWilaya, language),
                                    onValueChange = {},
                                    readOnly = true,
                                    leadingIcon = { Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = NavyPrimary) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = wilayaDropdownExpanded) },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.menuAnchor().fillMaxWidth().testTag("wilaya_dropdown")
                                )

                                ExposedDropdownMenu(
                                    expanded = wilayaDropdownExpanded,
                                    onDismissRequest = { wilayaDropdownExpanded = false }
                                ) {
                                    AlgeriaWilayas.list.forEach { wilaya ->
                                        DropdownMenuItem(
                                            text = { Text(AlgeriaWilayas.getNameForLanguage(wilaya, language)) },
                                            onClick = {
                                                selectedWilayaCode = wilaya.code
                                                wilayaDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // COMMUNE
                            OutlinedTextField(
                                value = commune,
                                onValueChange = { commune = it },
                                label = { Text(Localization.communeLabel(language)) },
                                leadingIcon = { Icon(imageVector = Icons.Default.LocationOn, contentDescription = null) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("craftsman_commune_input")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // DAILY RATE & YEARS EXPERIENCE
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = dailyRateDzd,
                                    onValueChange = { dailyRateDzd = it },
                                    label = { Text(Localization.dailyRateLabel(language)) },
                                    leadingIcon = { Icon(imageVector = Icons.Default.AttachMoney, contentDescription = null) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f).testTag("craftsman_rate_input")
                                )

                                OutlinedTextField(
                                    value = yearsExperience,
                                    onValueChange = { yearsExperience = it },
                                    label = { Text(Localization.experienceLabel(language)) },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Badge, contentDescription = null) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f).testTag("craftsman_exp_input")
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // DESCRIPTION
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text(Localization.descriptionLabel(language)) },
                                minLines = 2,
                                maxLines = 4,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("craftsman_desc_input")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // SKILLS
                            OutlinedTextField(
                                value = skillsCsv,
                                onValueChange = { skillsCsv = it },
                                label = { Text(Localization.skillsLabel(language)) },
                                leadingIcon = { Icon(imageVector = Icons.Default.Work, contentDescription = null) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("craftsman_skills_input")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ACTION SUBMIT BUTTON
                    Button(
                        onClick = {
                            if (isRegisterMode) {
                                if (selectedRole == "CRAFTSMAN") {
                                    onRegisterCraftsman(
                                        fullName,
                                        email,
                                        password,
                                        selectedCategoryKey,
                                        if (phone.isBlank()) "0550000000" else phone,
                                        if (whatsapp.isBlank()) phone else whatsapp,
                                        selectedWilayaCode,
                                        commune,
                                        dailyRateDzd.toIntOrNull() ?: 3000,
                                        yearsExperience.toIntOrNull() ?: 5,
                                        description,
                                        skillsCsv
                                    ) { err -> errorMessageKey = err }
                                } else {
                                    onRegister(fullName, email, password) { err ->
                                        errorMessageKey = err
                                    }
                                }
                            } else {
                                onLogin(email, password) { err ->
                                    errorMessageKey = err
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("auth_submit_button")
                    ) {
                        Text(
                            text = if (isRegisterMode) {
                                if (selectedRole == "CRAFTSMAN") (if (language == AppLanguage.AR) "تسجيل كحرفي جديد 🛠️" else "S'inscrire comme Artisan 🛠️")
                                else Localization.signUpButton(language)
                            } else Localization.loginButton(language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Toggle Mode Footer Link
                    Text(
                        text = if (isRegisterMode) Localization.haveAccountPrompt(language) else Localization.noAccountPrompt(language),
                        style = MaterialTheme.typography.bodySmall,
                        color = NavyPrimary,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isRegisterMode = !isRegisterMode
                                errorMessageKey = null
                            }
                            .padding(vertical = 4.dp)
                            .testTag("toggle_auth_mode_text")
                    )
                }
            }
        }
    }
}
