package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.db.CraftsmanEntity
import com.example.data.model.AppLanguage
import com.example.data.remote.SupabaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.ui.Localization

private const val MAX_PHOTOS = 3
private const val MAX_PHOTO_BYTES: Long = 4L * 1024 * 1024

@Composable
fun ServiceRequestDialog(
    craftsman: CraftsmanEntity,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSubmit: (categoryKey: String, wilayaCode: String, commune: String, description: String, imageUrls: List<String>) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var commune by remember { mutableStateOf(craftsman.commune) }
    var description by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var uploading by remember { mutableStateOf(false) }
    var uploadError by remember { mutableStateOf<String?>(null) }
    // Previews (URI) + ready-to-upload bytes indexed together.
    var pendingUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var pendingData by remember { mutableStateOf<List<Pair<ByteArray, String>>>(emptyList()) }
    // Already-uploaded public URLs.
    var uploadedUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    val storage = remember { SupabaseStorage(context) }

    LaunchedEffect(description) {
        error = false
    }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = MAX_PHOTOS)
    ) { uris ->
        if (uris.isEmpty()) return@rememberLauncherForActivityResult
        scope.launch {
            val loaded = withContext(Dispatchers.IO) {
                uris.take(MAX_PHOTOS).mapNotNull { uri ->
                    runCatching {
                        val size = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                            if (cursor.moveToFirst()) cursor.getLong(cursor.getColumnIndexOrThrow(OpenableColumns.SIZE)) else 0L
                        } ?: 0L
                        if (size > MAX_PHOTO_BYTES) return@mapNotNull null // Skip huge files early.
                        val inputStream = context.contentResolver.openInputStream(uri) ?: return@mapNotNull null
                        val original = BitmapFactory.decodeStream(inputStream)
                        inputStream.close()
                        original ?: return@mapNotNull null
                        val resized = scaleToWidth(original, 800)
                        if (resized !== original) original.recycle()
                        val bytes = compressToJpeg(resized)
                        resized.recycle()
                        val ext = context.contentResolver.getType(uri)?.substringAfter('/') ?: "jpg"
                        val sanitizedExt = if (ext == "jpeg" || ext == "jpg") "jpg" else if (ext == "png") "png" else "jpg"
                        Pair(bytes, "${uri.lastPathSegment?.hashCode()?.toUInt()?.toString(16) ?: "p"}.${sanitizedExt}")
                    }.getOrNull()
                }
            }
            pendingUris = uris.take(MAX_PHOTOS).take(loaded.size)
            pendingData = loaded
            uploadError = null
        }
    }

    val photoLabel = Localization.Ui.text("photos_count", language, "size" to pendingData.size.toString(), "max" to MAX_PHOTOS.toString())
    val addPhotoLabel = Localization.Ui.text("add_photos_label", language)

    val title = Localization.Ui.text("request_dialog_title", language, "name" to craftsman.name)
    val descriptionLabel = Localization.Ui.text("describe_placeholder", language)
    val submitLabel = Localization.Ui.text("send_request_button", language)
    val uploadingLabel = Localization.Ui.text("uploading_photos", language)

    fun submitRequest() {
        if (uploading) return
        if (description.trim().length !in 10..2000) {
            error = true
            return
        }
        scope.launch {
            uploading = true
            uploadError = null
            try {
                // Upload each photo individually: succeeded URLs go into the
                // request immediately; bytes that failed (offline, timeout, ...)
                // are encoded as raw payloads so the repository can persist
                // them locally and re-upload them once connectivity returns.
                val newUrls = withContext(Dispatchers.IO) {
                    pendingData.map { (bytes, fileName) ->
                        runCatching {
                            storage.upload(bytes, fileName)
                        }.getOrElse { "__pending__:${Base64.encodeToString(bytes, Base64.NO_WRAP)}" }
                    }
                }
                // Keep the __pending__ placeholders (raw Base64 payloads) so the
                // repository can persist them locally and re-upload them later
                // when connectivity returns (offline-first photo flow).
                uploadedUrls = newUrls.filter { !it.startsWith("__pending__:") }
                onSubmit(
                    craftsman.categoryKey,
                    craftsman.wilayaCode.toString(),
                    commune.trim(),
                    description.trim(),
                    newUrls
                )
            } catch (_: Throwable) {
                uploadError = Localization.Ui.text("connection_lost_photos", language)
                // Even without photos, a description-only request still works.
                onSubmit(
                    craftsman.categoryKey,
                    craftsman.wilayaCode.toString(),
                    commune.trim(),
                    description.trim(),
                    emptyList()
                )
            } finally {
                uploading = false
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = commune,
                    onValueChange = { commune = it.take(100) },
                    label = { Text(Localization.Ui.text("commune_label", language)) },
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
                        text = Localization.Ui.text("description_hint", language),
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                        Spacer(Modifier.size(4.dp))
                        Text(addPhotoLabel)
                    }
                    Text(
                        text = photoLabel,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                if (pendingUris.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pendingUris.forEachIndexed { index, uri ->
                            Box {
                                AsyncImage(
                                    model = ImageRequest.Builder(context).data(uri).crossfade(true).build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                IconButton(
                                    onClick = {
                                        pendingUris = pendingUris.toMutableList().apply { removeAt(index) }
                                        pendingData = pendingData.toMutableList().apply { removeAt(index) }
                                    },
                                    modifier = Modifier.align(Alignment.TopEnd).size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                if (uploadError != null) {
                    Text(
                        text = uploadError!!,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        },
        confirmButton = {
            if (uploading) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    Text(uploadingLabel)
                }
            } else {
                Button(onClick = ::submitRequest) {
                    Text(submitLabel)
                }
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, enabled = !uploading) {
                Text(Localization.Ui.text("cancel_button", language))
            }
        }
    )
}

internal fun scaleToWidth(bitmap: Bitmap, maxPx: Int): Bitmap {
    if (bitmap.width <= maxPx) return bitmap
    val ratio = maxPx.toFloat() / bitmap.width
    val height = (bitmap.height * ratio).toInt().coerceAtLeast(1)
    return Bitmap.createScaledBitmap(bitmap, maxPx, height, true)
}

internal fun compressToJpeg(bitmap: Bitmap): ByteArray {
    val output = java.io.ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, output)
    return output.toByteArray()
}
