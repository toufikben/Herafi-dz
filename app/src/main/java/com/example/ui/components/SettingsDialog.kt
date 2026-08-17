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
    onUpdateNotificationInterval: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = "الإعدادات",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Box(modifier = Modifier.height(520.dp)) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    if (currentUser != null) {
                        AccountSection(
                            currentUser = currentUser,
                            isUpdatingPassword = isUpdatingPassword,
                            passwordResult = passwordResult,
                            onChangePassword = onChangePassword,
                            onLogout = onLogout
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    }
                    NotificationSection(
                        requestNotificationsEnabled = requestNotificationsEnabled,
                        notificationIntervalSeconds = notificationIntervalSeconds,
                        craftsmenNotificationEnabled = craftsmenNotificationEnabled,
                        onToggleRequestNotifications = onToggleRequestNotifications,
                        onToggleCraftsmanNotifications = onToggleCraftsmanNotifications,
                        onUpdateNotificationInterval = onUpdateNotificationInterval
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    if (isCraftsman) {
                        CraftsmanSection(
                            isAvailable = isCraftsmanAvailable,
                            craftsmenNotificationEnabled = craftsmenNotificationEnabled,
                            onUpdateCraftsmanField = onUpdateCraftsmanField,
                            onToggleAvailability = onToggleCraftsmanAvailability,
                            onResetCraftsman = onResetCraftsman
                        )
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = "يمكنك التسجيل كحرفي للحصول على خيارات إدارة ملفك الحرفي.",
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
                Text("إغلاق")
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
    onChangePassword: (String) -> Unit,
    onLogout: () -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    val ctx = LocalContext.current

    SectionHeader(icon = Icons.Default.Password, title = "إعدادات الحساب")
    Text(
        text = "المستخدم: ${currentUser.fullName}\nالبريد: ${currentUser.email}",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = newPassword,
        onValueChange = { newPassword = it; passwordError = null },
        label = { Text("كلمة المرور الجديدة") },
        placeholder = { Text("8 أحرف على الأقل") },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = confirmPassword,
        onValueChange = { confirmPassword = it; passwordError = null },
        label = { Text("تأكيد كلمة المرور") },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth()
    )

    val resultText = passwordResult ?: passwordError

    if (resultText != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (passwordResult?.contains("بنجاح") == true) Icons.Default.CheckCircle else Icons.Default.Close,
                contentDescription = null,
                tint = if (passwordResult?.contains("بنجاح") == true)
                    MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error,
                modifier = Modifier.width(18.dp).height(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = resultText,
                color = if (passwordResult?.contains("بنجاح") == true)
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
                        passwordError = "كلمة المرور يجب أن تكون 8 أحرف على الأقل"
                    }
                    newPassword != confirmPassword -> {
                        passwordError = "كلمتا المرور غير متطابقتين"
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
                Text("جاري التحديث...")
            } else {
                Icon(Icons.Default.Lock, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("تغيير كلمة المرور")
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
        Text("تسجيل الخروج")
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
    onUpdateNotificationInterval: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val intervals = listOf(15, 30, 60)

    SectionHeader(icon = Icons.Default.Notifications, title = "التنبيهات")
    ToggleRow(
        title = "تنبيهات حالة الطلبات",
        description = "إشعار عند تغير حالة طلبك أو وصول رد من حرفي",
        checked = requestNotificationsEnabled,
        onCheckedChange = onToggleRequestNotifications
    )
    Spacer(modifier = Modifier.height(8.dp))
    if (requestNotificationsEnabled) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "تحديث كل:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                TextField(
                    value = when (notificationIntervalSeconds) {
                        15 -> "15 ثانية"
                        60 -> "دقيقة"
                        else -> "30 ثانية"
                    },
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
                                Text(
                                    when (seconds) {
                                        15 -> "15 ثانية (أسرع)"
                                        60 -> "دقيقة (توفير طاقة)"
                                        else -> "30 ثانية (افتراضي)"
                                    }
                                )
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
        title = "تنبيهات الحرفيين",
        description = "إشعار عند وصول حرفيين جدد أو تغير التقييمات",
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
    onResetCraftsman: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var wilayaCode by remember { mutableStateOf(16) }
    var commune by remember { mutableStateOf("") }
    var dailyRate by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var wilayaExpanded by remember { mutableStateOf(false) }
    var updateResult by remember { mutableStateOf<String?>(null) }

    SectionHeader(icon = Icons.Default.Person, title = "ملف الحرفي")
    ToggleRow(
        title = "متاح لاستقبال الطلبات",
        description = "إيقاف التوفر يخفيك من قائمة البحث مؤقتًا",
        checked = isAvailable,
        onCheckedChange = onToggleAvailability
    )
    Spacer(modifier = Modifier.height(12.dp))

    Text(
        text = "تعديل معلومات ملفك (اكتب ما تريد تغييره فقط):",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = name,
        onValueChange = { name = it; updateResult = null },
        label = { Text("الاسم المهني") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = phone,
        onValueChange = { phone = it; updateResult = null },
        label = { Text("الهاتف") },
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
                value = AlgeriaWilayas.list.find { it.code == wilayaCode }?.nameAr ?: "الجزائر العاصمة",
                onValueChange = {},
                readOnly = true,
                label = { Text("الولاية") },
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
            label = { Text("البلدية") },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = dailyRate,
        onValueChange = { dailyRate = it; updateResult = null },
        label = { Text("الأجر اليومي (دج) — اختياري") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = description,
        onValueChange = { description = it; updateResult = null },
        label = { Text("الوصف المهني") },
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
            wilayaCode = 16
            updateResult = "تم تحديث ملفك"
            name = ""; phone = ""; commune = ""; dailyRate = ""; description = ""
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Default.Edit, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text("حفظ التعديلات")
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
        Text("حذف ملف الحرفي (إعادة التعيين)")
    }
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
