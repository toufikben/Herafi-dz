package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlgeriaWilaya
import com.example.data.model.AlgeriaWilayas
import com.example.data.model.AppLanguage
import com.example.data.model.TradeCategories
import com.example.data.model.TradeCategory
import com.example.ui.Localization
import com.example.ui.theme.NavyPrimary

@Composable
fun AddWorkerScreen(
    language: AppLanguage,
    onRegisterWorker: (
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
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }

    var selectedCategory by remember { mutableStateOf(TradeCategories.BUILDER) }
    var selectedWilaya by remember { mutableStateOf(AlgeriaWilayas.list[15]) } // Default Algiers
    var commune by remember { mutableStateOf("") }

    var dailyRateStr by remember { mutableStateOf("4000") }
    var yearsExpStr by remember { mutableStateOf("8") }

    var description by remember { mutableStateOf("") }
    var skillsCsv by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf<String?>(null) }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var wilayaDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = Localization.registerWorkerTitle(language),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = Localization.Ui.text("worker_intro_text", language),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Form Fields
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(Localization.fullName(language)) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("add_worker_name_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Job Trade Dropdown
        Text(
            text = Localization.Ui.text("trade_specialty_label", language),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { categoryDropdownExpanded = true }
                    .testTag("add_worker_category_dropdown")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(selectedCategory.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = when (language) {
                            AppLanguage.AR -> selectedCategory.nameAr
                            AppLanguage.FR -> selectedCategory.nameFr
                            AppLanguage.EN -> selectedCategory.nameEn
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }

            DropdownMenu(
                expanded = categoryDropdownExpanded,
                onDismissRequest = { categoryDropdownExpanded = false }
            ) {
                TradeCategories.list.filter { it.key != "ALL" }.forEach { cat ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = when (language) {
                                    AppLanguage.AR -> cat.nameAr
                                    AppLanguage.FR -> cat.nameFr
                                    AppLanguage.EN -> cat.nameEn
                                }
                            )
                        },
                        onClick = {
                            selectedCategory = cat
                            categoryDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Phone & WhatsApp
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(Localization.phoneLabel(language)) },
                leadingIcon = { Icon(Icons.Default.Call, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("add_worker_phone_input")
            )

            OutlinedTextField(
                value = whatsapp,
                onValueChange = { whatsapp = it },
                label = { Text(Localization.whatsappLabel(language)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("add_worker_whatsapp_input")
            )
        }

        if (formError != null) {
            Text(
                text = formError!!,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Wilaya Dropdown
        Text(
            text = Localization.selectWilaya(language),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { wilayaDropdownExpanded = true }
                    .testTag("add_worker_wilaya_dropdown")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = AlgeriaWilayas.getNameForLanguage(selectedWilaya, language),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }

            DropdownMenu(
                expanded = wilayaDropdownExpanded,
                onDismissRequest = { wilayaDropdownExpanded = false },
                modifier = Modifier.height(300.dp)
            ) {
                AlgeriaWilayas.list.forEach { w ->
                    DropdownMenuItem(
                        text = { Text(text = AlgeriaWilayas.getNameForLanguage(w, language)) },
                        onClick = {
                            selectedWilaya = w
                            wilayaDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = commune,
            onValueChange = { commune = it },
            label = { Text(Localization.communeLabel(language)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("add_worker_commune_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = dailyRateStr,
                onValueChange = { dailyRateStr = it },
                label = { Text(Localization.dailyRateLabel(language)) },
                leadingIcon = { Icon(Icons.Default.Money, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("add_worker_rate_input")
            )

            OutlinedTextField(
                value = yearsExpStr,
                onValueChange = { yearsExpStr = it },
                label = { Text(Localization.experienceLabel(language)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("add_worker_exp_input")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(Localization.descriptionLabel(language)) },
            minLines = 3,
            maxLines = 4,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("add_worker_desc_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = skillsCsv,
            onValueChange = { skillsCsv = it },
            label = { Text(Localization.skillsLabel(language)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("add_worker_skills_input")
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                formError = null
                when {
                    name.isBlank() -> formError = Localization.authErrorMessage(language, "error_name_empty")
                    phone.isBlank() -> formError = Localization.Ui.text("phone_required_error", language)
                    else -> onRegisterWorker(
                        name,
                        selectedCategory.key,
                        phone,
                        whatsapp,
                        selectedWilaya.code,
                        commune,
                        dailyRateStr.toIntOrNull() ?: 4000,
                        yearsExpStr.toIntOrNull() ?: 5,
                        description,
                        skillsCsv
                    )
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("submit_new_worker_button")
        ) {
            Text(
                text = Localization.addWorkerButton(language),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
