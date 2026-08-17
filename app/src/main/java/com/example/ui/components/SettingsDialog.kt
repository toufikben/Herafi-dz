package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.db.UserEntity
import com.example.data.model.AlgeriaWilayas
import com.example.data.model.AppLanguage
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.example.ui.Localization

private const val PASSWORD_MIN_LENGTH = 8

/**
 * Settings screen: account (password change, logout), notification
 * preferences, and craftsman profile reset / editing.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    currentUser: UserEntity?,
    isCraftsman: Boolean,
    language: AppLanguage,
    requestNotificationsEnabled: Boolean,
    notificationIntervalSeconds: Int,
    isCraftsmanAvailable: Boolean,
    craftsmenNotificationEnabled: Boolean,
    isUpdatingPassword: Boolean,
    passwordResult: String?, // Success message | null | error message
    onDismiss: () -> Unit,
    onChangePassword: (String) -> Unit,
    onLogout: () -> Unit,
    onResetCraftsman: () -> Unit,
    onUpdateCraftsmanField: (field: String, value: String) -> Unit,
    onToggleRequestNotifications: (Boolean) -> Unit,
    onToggleCraftsmanAvailability: (Boolean) -> Unit,
    onToggleCraftsmanNotifications: (Boolean) -> Unit,
    onUpdateNotificationInterval: (Int) -> Unit,
    themeMode: String = "system",
    selectedLanguage: String = "ar",
    onThemeModeChange: (String) -> Unit = {},
    onLanguageChange: (String) -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = Localization.settingsTitle(language),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Box(modifier = Modifier.height(520.dp)) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    AppearanceSection(
                        themeMode = themeMode,
                        selectedLanguage = selectedLanguage,
                        language = language,
                        onThemeModeChange = onThemeModeChange,
                        onLanguageChange = onLanguageChange
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    if (currentUser != null) {
                        AccountSection(
                            currentUser = currentUser,
                            isUpdatingPassword = isUpdatingPassword,
                            passwordResult = passwordResult,
                            onChangePassword = onChangePassword,
                            onLogout = onLogout,
                            language = language
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    }
                    NotificationSection(
                        requestNotificationsEnabled = requestNotificationsEnabled,
                        notificationIntervalSeconds = notificationIntervalSeconds,
                        craftsmenNotificationEnabled = craftsmenNotificationEnabled,
                        onToggleRequestNotifications = onToggleRequestNotifications,
                        onToggleCraftsmanNotifications = onToggleCraftsmanNotifications,
                        onUpdateNotificationInterval = onUpdateNotificationInterval,
                        language = language
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    if (isCraftsman) {
                        CraftsmanSection(
                            isAvailable = isCraftsmanAvailable,
                            craftsmenNotificationEnabled = craftsmenNotificationEnabled,
                            onUpdateCraftsmanField = onUpdateCraftsmanField,
                            onToggleAvailability = onToggleCraftsmanAvailability,
                            onResetCraftsman = onResetCraftsman,
                            language = language,
                        )
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = Localization.craftsmanClientHint(language),
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(Localization.settingsClose(language))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountSection(
    currentUser: UserEntity,
    isUpdatingPassword: Boolean,
    passwordResult: String?,
    language: AppLanguage,
    onChangePassword: (String) -> Unit,
    onLogout: () -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    val ctx = LocalContext.current

    SectionHeader(icon = Icons.Default.Password, title = Localization.accountSectionTitle(language))
    Text(
        text = Localization.accountInfoLine(language, currentUser.fullName, currentUser.email),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = newPassword,
        onValueChange = { newPassword = it; passwordError = null },
        label = { Text(Localization.newPasswordLabel(language)) },
        placeholder = { Text(Localization.newPasswordPlaceholder(language)) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = confirmPassword,
        onValueChange = { confirmPassword = it; passwordError = null },
        label = { Text(Localization.confirmPasswordLabel(language)) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth()
    )

    val resultText = passwordResult ?: passwordError

    if (resultText != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (passwordResult == Localization.Ui.text("password_changed_ok", language)) Icons.Default.CheckCircle else Icons.Default.Close,
                contentDescription = null,
                tint = if (passwordResult == Localization.Ui.text("password_changed_ok", language))
                    MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error,
                modifier = Modifier.width(18.dp).height(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = resultText,
                color = if (passwordResult == Localization.Ui.text("password_changed_ok", language))
                    MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

    Spacer(modifier = Modifier.height(12.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = {
                when {
                    newPassword.length < PASSWORD_MIN_LENGTH -> {
                        passwordError = Localization.passwordTooShortError(language)
                    }
                    newPassword != confirmPassword -> {
                        passwordError = Localization.passwordMismatchError(language)
                    }
                    else -> {
                        passwordError = null
                        onChangePassword(newPassword)
                        newPassword = ""
                        confirmPassword = ""
                    }
                }
            },
            enabled = !isUpdatingPassword,
            modifier = Modifier.weight(1f)
        ) {
            if (isUpdatingPassword) {
                Text(Localization.changePasswordUpdating(language))
            } else {
                Icon(Icons.Default.Lock, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(Localization.changePasswordButton(language))
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))
    OutlinedButton(
        onClick = onLogout,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        )
    ) {
        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text(Localization.logoutCaption(language))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationSection(
    requestNotificationsEnabled: Boolean,
    notificationIntervalSeconds: Int,
    craftsmenNotificationEnabled: Boolean,
    onToggleRequestNotifications: (Boolean) -> Unit,
    onToggleCraftsmanNotifications: (Boolean) -> Unit,
    onUpdateNotificationInterval: (Int) -> Unit,
    language: AppLanguage,
) {
    var expanded by remember { mutableStateOf(false) }
    val intervals = listOf(15, 30, 60)

    SectionHeader(icon = Icons.Default.Notifications, title = Localization.notificationsSectionTitle(language))
    ToggleRow(
        title = Localization.requestNotificationsTitle(language),
        description = Localization.requestNotificationsDescription(language),
        checked = requestNotificationsEnabled,
        onCheckedChange = onToggleRequestNotifications
    )
    Spacer(modifier = Modifier.height(8.dp))
    if (requestNotificationsEnabled) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = Localization.refreshIntervalLabel(language),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                TextField(
                    value = Localization.refreshIntervalValue(language, notificationIntervalSeconds),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor()
                        .weight(1f),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    textStyle = MaterialTheme.typography.bodyMedium
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    intervals.forEach { seconds ->
                        DropdownMenuItem(
                            text = {
                                Text(Localization.refreshIntervalOption(language, seconds))
                            },
                            onClick = {
                                onUpdateNotificationInterval(seconds)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    ToggleRow(
        title = Localization.craftsmanNotificationsTitle(language),
        description = Localization.craftsmanNotificationsDescription(language),
        checked = craftsmenNotificationEnabled,
        onCheckedChange = onToggleCraftsmanNotifications
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CraftsmanSection(
    isAvailable: Boolean,
    craftsmenNotificationEnabled: Boolean,
    onUpdateCraftsmanField: (String, String) -> Unit,
    onToggleAvailability: (Boolean) -> Unit,
    onResetCraftsman: () -> Unit,
    language: AppLanguage,
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var wilayaCode by remember { mutableStateOf(16) }
    var commune by remember { mutableStateOf("") }
    var dailyRate by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var wilayaExpanded by remember { mutableStateOf(false) }
    var updateResult by remember { mutableStateOf<String?>(null) }

    SectionHeader(icon = Icons.Default.Person, title = Localization.craftsmanSectionTitle(language))
    ToggleRow(
        title = Localization.availabilityToggleTitle(language),
        description = Localization.availabilityToggleDescription(language),
        checked = isAvailable,
        onCheckedChange = onToggleAvailability
    )
    Spacer(modifier = Modifier.height(12.dp))

    Text(
        text = Localization.editProfileHint(language),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = name,
        onValueChange = { name = it; updateResult = null },
        label = { Text(Localization.professionalNameLabel(language)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = phone,
        onValueChange = { phone = it; updateResult = null },
        label = { Text(Localization.phoneFieldLabel(language)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        ExposedDropdownMenuBox(
            expanded = wilayaExpanded,
            onExpandedChange = { wilayaExpanded = it },
            modifier = Modifier.weight(1f)
        ) {
            TextField(
                value = AlgeriaWilayas.list.find { it.code == wilayaCode }?.nameAr ?: Localization.Ui.text("wilaya_default", language),
                onValueChange = {},
                readOnly = true,
                label = { Text(Localization.wilayaFieldLabel(language)) },
                modifier = Modifier.menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = wilayaExpanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                textStyle = MaterialTheme.typography.bodyMedium
            )
            ExposedDropdownMenu(
                expanded = wilayaExpanded,
                onDismissRequest = { wilayaExpanded = false }
            ) {
                (1..58).forEach { code ->
                    DropdownMenuItem(
                        text = { Text("${code.toString().padStart(2, '0')} - ${AlgeriaWilayas.list.find { it.code == code }?.nameAr ?: ""}") },
                        onClick = {
                            wilayaCode = code
                            wilayaExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
            value = commune,
            onValueChange = { commune = it; updateResult = null },
            label = { Text(Localization.communeFieldLabel(language)) },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = dailyRate,
        onValueChange = { dailyRate = it; updateResult = null },
        label = { Text(Localization.dailyRateOptionalLabel(language)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = description,
        onValueChange = { description = it; updateResult = null },
        label = { Text(Localization.professionalDescriptionLabel(language)) },
        modifier = Modifier.fillMaxWidth().height(70.dp),
        maxLines = 3
    )

    if (updateResult != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = updateResult.orEmpty(),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodySmall
        )
    }

    Spacer(modifier = Modifier.height(12.dp))
    Button(
        onClick = {
            val rateInt = dailyRate.toIntOrNull()
            onUpdateCraftsmanField("profile", "$name|$phone|$wilayaCode|$commune|$rateInt|$description")
            updateResult = Localization.updateSuccess(language)
            name = ""; phone = ""; commune = ""; dailyRate = ""; description = ""
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Default.Edit, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text(Localization.saveEditsButton(language))
    }
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedButton(
        onClick = onResetCraftsman,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        )
    ) {
                Icon(Icons.Default.DeleteForever, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text(Localization.resetCraftsmanButton(language))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppearanceSection(
    themeMode: String,
    selectedLanguage: String,
    language: AppLanguage,
    onThemeModeChange: (String) -> Unit,
    onLanguageChange: (String) -> Unit
) {
    var themeExpanded by remember { mutableStateOf(false) }
    var langExpanded by remember { mutableStateOf(false) }
    val themes = listOf("system", "light", "dark")
    val langs = listOf("ar", "fr", "en")

    SectionHeader(icon = Icons.Default.Edit, title = Localization.appearanceSectionTitle(language))

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = Localization.themeLabel(language),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        ExposedDropdownMenuBox(
            expanded = themeExpanded,
            onExpandedChange = { themeExpanded = it },
            modifier = Modifier.weight(1f)
        ) {
            TextField(
                value = when (themeMode) {
                    "light" -> Localization.themeLight(language)
                    "dark" -> Localization.themeDark(language)
                    else -> Localization.themeSystem(language)
                },
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = themeExpanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                textStyle = MaterialTheme.typography.bodyMedium
            )
            ExposedDropdownMenu(
                expanded = themeExpanded,
                onDismissRequest = { themeExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(Localization.themeSystem(language)) },
                    onClick = { onThemeModeChange("system"); themeExpanded = false }
                )
                DropdownMenuItem(
                    text = { Text(Localization.themeLight(language)) },
                    onClick = { onThemeModeChange("light"); themeExpanded = false }
                )
                DropdownMenuItem(
                    text = { Text(Localization.themeDark(language)) },
                    onClick = { onThemeModeChange("dark"); themeExpanded = false }
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = Localization.languageLabel(language),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        ExposedDropdownMenuBox(
            expanded = langExpanded,
            onExpandedChange = { langExpanded = it },
            modifier = Modifier.weight(1f)
        ) {
            TextField(
                value = langs.firstOrNull { it == selectedLanguage }?.let { AppLanguage.entries.firstOrNull { l -> l.code == it }?.nativeName } ?: language.nativeName,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = langExpanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                textStyle = MaterialTheme.typography.bodyMedium
            )
            ExposedDropdownMenu(
                expanded = langExpanded,
                onDismissRequest = { langExpanded = false }
            ) {
                langs.forEach { code ->
                    val lang = AppLanguage.entries.firstOrNull { it.code == code }
                    DropdownMenuItem(
                        text = { Text(lang?.nativeName ?: code) },
                        onClick = { onLanguageChange(code); langExpanded = false }
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = Localization.languageChangeHint(language),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun SectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
