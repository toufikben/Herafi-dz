package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.db.CraftsmanEntity
import com.example.data.model.AppLanguage

@Composable
fun ServiceRequestDialog(
    craftsman: CraftsmanEntity,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSubmit: (categoryKey: String, wilayaCode: String, commune: String, description: String) -> Unit
) {
    var commune by remember { mutableStateOf(craftsman.commune) }
    var description by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    LaunchedEffect(description) {
        error = false
    }

    val title = when (language) {
        AppLanguage.AR -> "طلب خدمة من ${craftsman.name}"
        AppLanguage.FR -> "Demander un service à ${craftsman.name}"
        AppLanguage.EN -> "Request a service from ${craftsman.name}"
    }
    val descriptionLabel = when (language) {
        AppLanguage.AR -> "صف المشكلة أو الخدمة المطلوبة"
        AppLanguage.FR -> "Décrivez le problème ou le service"
        AppLanguage.EN -> "Describe the problem or service"
    }
    val submitLabel = when (language) {
        AppLanguage.AR -> "إرسال الطلب"
        AppLanguage.FR -> "Envoyer la demande"
        AppLanguage.EN -> "Send request"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = commune,
                    onValueChange = { commune = it.take(100) },
                    label = { Text(if (language == AppLanguage.AR) "البلدية" else "Commune") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it.take(2000) },
                    label = { Text(descriptionLabel) },
                    minLines = 4,
                    maxLines = 6,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )
                if (error) {
                    Text(
                        text = if (language == AppLanguage.AR) "اكتب وصفًا بين 10 و2000 حرف" else "Description must contain 10 to 2000 characters",
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (description.trim().length !in 10..2000) {
                    error = true
                } else {
                    onSubmit(craftsman.categoryKey, craftsman.wilayaCode.toString(), commune.trim(), description.trim())
                }
            }) {
                Text(submitLabel)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(if (language == AppLanguage.AR) "إلغاء" else "Cancel")
            }
        }
    )
}
