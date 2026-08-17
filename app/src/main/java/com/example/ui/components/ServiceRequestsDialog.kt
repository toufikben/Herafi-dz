package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.db.ServiceRequestEntity
import com.example.data.model.AppLanguage

@Composable
fun ServiceRequestsDialog(
    requests: List<ServiceRequestEntity>,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    isCraftsman: Boolean = false,
    onUpdateStatus: (remoteRequestId: String, newStatus: String) -> Unit = { _, _ -> }
) {
    val pendingCount = requests.count { it.syncState != ServiceRequestEntity.SYNCED }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(if (language == AppLanguage.AR) "طلباتي" else if (language == AppLanguage.FR) "Mes demandes" else "My requests")
                IconButton(onClick = onRefresh, modifier = Modifier.testTag("refresh_requests_button")) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                }
            }
        },
        text = {
            Column {
                if (pendingCount > 0) {
                    Text(
                        text = if (language == AppLanguage.AR) "$pendingCount طلب بانتظار المزامنة" else if (language == AppLanguage.FR) "$pendingCount demande(s) en attente de synchronisation" else "$pendingCount request(s) pending sync",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(8.dp))
                }
                if (requests.isEmpty()) {
                    Text(
                        text = if (language == AppLanguage.AR) "لا توجد طلبات بعد" else if (language == AppLanguage.FR) "Aucune demande" else "No requests yet",
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                } else {
                    LazyColumn(modifier = Modifier.height(360.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(requests, key = { it.id }) { request ->
                            RequestRow(request, language, onRetry, isCraftsman, onUpdateStatus)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == AppLanguage.AR) "إغلاق" else if (language == AppLanguage.FR) "Fermer" else "Close")
            }
        }
    )
}

@Composable
private fun RequestRow(
    request: ServiceRequestEntity,
    language: AppLanguage,
    onRetry: () -> Unit,
    isCraftsman: Boolean,
    onUpdateStatus: (remoteRequestId: String, newStatus: String) -> Unit
) {
    val status = when (request.status) {
        ServiceRequestEntity.STATUS_OPEN -> if (language == AppLanguage.AR) "مفتوح" else if (language == AppLanguage.FR) "Ouverte" else "Open"
        ServiceRequestEntity.STATUS_QUOTED -> if (language == AppLanguage.AR) "وصل عرض سعر" else if (language == AppLanguage.FR) "Devis reçu" else "Quoted"
        ServiceRequestEntity.STATUS_ACCEPTED -> if (language == AppLanguage.AR) "مقبول" else if (language == AppLanguage.FR) "Acceptée" else "Accepted"
        ServiceRequestEntity.STATUS_IN_PROGRESS -> if (language == AppLanguage.AR) "قيد التنفيذ" else if (language == AppLanguage.FR) "En cours" else "In progress"
        ServiceRequestEntity.STATUS_COMPLETED -> if (language == AppLanguage.AR) "مكتمل" else if (language == AppLanguage.FR) "Terminée" else "Completed"
        ServiceRequestEntity.STATUS_CANCELLED -> if (language == AppLanguage.AR) "ملغى" else if (language == AppLanguage.FR) "Annulée" else "Cancelled"
        else -> request.status
    }
    val syncText = when (request.syncState) {
        ServiceRequestEntity.SYNCED -> ""
        ServiceRequestEntity.SYNC_FAILED -> if (language == AppLanguage.AR) "لم تتم المزامنة" else if (language == AppLanguage.FR) "Synchronisation échouée" else "Sync failed"
        else -> if (language == AppLanguage.AR) "بانتظار المزامنة" else if (language == AppLanguage.FR) "En attente" else "Pending sync"
    }
    val otherParty = when {
        isCraftsman && request.customerDisplayName != null ->
            (if (language == AppLanguage.AR) "العميل: " else if (language == AppLanguage.FR) "Client: " else "Customer: ") + request.customerDisplayName
        !isCraftsman && request.craftsmanName != null ->
            (if (language == AppLanguage.AR) "الحرفي: " else if (language == AppLanguage.FR) "Artisan: " else "Craftsman: ") + request.craftsmanName
        else -> ""
    }
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))) {
        Column(Modifier.padding(12.dp)) {
            Text(request.categoryKey, fontWeight = FontWeight.Bold)
            Text("${request.wilayaCode} • ${request.commune}", style = MaterialTheme.typography.bodySmall)
            if (otherParty.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(otherParty, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(4.dp))
            Text(request.description, maxLines = 3, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(status, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                if (syncText.isNotBlank()) {
                    Spacer(Modifier.width(8.dp))
                    Text(syncText, style = MaterialTheme.typography.labelSmall)
                }
                if (request.syncState != ServiceRequestEntity.SYNCED) {
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = onRetry,
                        contentPadding = ButtonDefaults.ContentPadding,
                        modifier = Modifier.testTag("retry_request_button")
                    ) { Text(if (language == AppLanguage.AR) "إعادة" else if (language == AppLanguage.FR) "Réessayer" else "Retry") }
                }
                if (isCraftsman && request.syncState == ServiceRequestEntity.SYNCED) {
                    Spacer(Modifier.weight(1f))
                    craftsmanActions(request, language, onUpdateStatus)
                }
            }
        }
    }
}

@Composable
private fun craftsmanActions(
    request: ServiceRequestEntity,
    language: AppLanguage,
    onUpdateStatus: (String, String) -> Unit
) {
    when (request.status) {
        ServiceRequestEntity.STATUS_OPEN -> {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(
                    onClick = { onUpdateStatus(request.remoteId ?: request.id, ServiceRequestEntity.STATUS_QUOTED) }
                ) {
                    Text(if (language == AppLanguage.AR) "أرسل تسعير" else if (language == AppLanguage.FR) "Deviser" else "Quote")
                }
                TextButton(
                    onClick = { onUpdateStatus(request.remoteId ?: request.id, ServiceRequestEntity.STATUS_CANCELLED) }
                ) {
                    Text(if (language == AppLanguage.AR) "رفض" else if (language == AppLanguage.FR) "Refuser" else "Decline")
                }
            }
        }
        ServiceRequestEntity.STATUS_QUOTED -> {
            TextButton(
                onClick = { onUpdateStatus(request.remoteId ?: request.id, ServiceRequestEntity.STATUS_ACCEPTED) }
            ) {
                Text(if (language == AppLanguage.AR) "تأكيد القبول" else if (language == AppLanguage.FR) "Confirmer" else "Confirm accept")
            }
        }
        ServiceRequestEntity.STATUS_ACCEPTED -> {
            TextButton(
                onClick = { onUpdateStatus(request.remoteId ?: request.id, ServiceRequestEntity.STATUS_IN_PROGRESS) }
            ) {
                Text(if (language == AppLanguage.AR) "بدء التنفيذ" else if (language == AppLanguage.FR) "Démarrer" else "Start work")
            }
        }
        ServiceRequestEntity.STATUS_IN_PROGRESS -> {
            TextButton(
                onClick = { onUpdateStatus(request.remoteId ?: request.id, ServiceRequestEntity.STATUS_COMPLETED) }
            ) {
                Text(if (language == AppLanguage.AR) "إكمال" else if (language == AppLanguage.FR) "Terminer" else "Complete")
            }
        }
    }
}
