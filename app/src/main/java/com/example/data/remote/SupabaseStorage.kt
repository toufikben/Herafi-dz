package com.example.data.remote

import android.content.Context
import com.example.BuildConfig
import com.example.data.db.AppDatabase
import com.example.data.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Authenticated Supabase Storage client for request photos.
 * Uploads go to the `request-photos` bucket under a user-scoped prefix
 * (`{userId}/{requestId}/{file}.jpg`) so RLS policies keep each user
 * inside their own folder.
 *
 * Offline support: images that fail to upload (no network) are persisted
 * locally via [savePendingBytes] and later re-uploaded via
 * [uploadPendingFiles] once connectivity returns.
 */
class SupabaseStorage(context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
    private val userRepository = UserRepository(AppDatabase.getInstance(context.applicationContext).userDao(), context.applicationContext)

    private val baseUrl = BuildConfig.SUPABASE_URL.trim().trimEnd('/')
    private val anonKey = BuildConfig.SUPABASE_ANON_KEY

    private fun userId(): String = userRepository.getCurrentUserId() ?: "anon"

    private fun objectPath(requestId: String, fileName: String): String =
        "${userId()}/$requestId/$fileName"

    /** Synchronous upload of [bytes] belonging to [requestId]. Returns the public URL or throws. */
    fun upload(bytes: ByteArray, fileName: String, requestId: String): String =
        uploadInternal(bytes.toRequestBody(JPEG_MEDIA), objectPath(requestId, fileName))

    /** Overload without requestId kept for backwards compatibility. */
    fun upload(bytes: ByteArray, fileName: String): String =
        upload(bytes, fileName, UUID.randomUUID().toString())

    /**
     * Upload a locally persisted photo saved earlier by [savePendingBytes].
     * Returns the public URL on success and deletes the local file so it is
     * never uploaded twice; throws on failure.
     */
    suspend fun uploadPendingFile(localFile: File, requestId: String): String =
        withContext(Dispatchers.IO) {
            val bytes = localFile.readBytes()
            val url = uploadInternal(bytes.toRequestBody(JPEG_MEDIA), objectPath(requestId, localFile.name))
            runCatching { localFile.delete() }
            url
        }

    /** Upload several locally persisted photos; returns (uploaded urls, still-failed files). */
    suspend fun uploadPendingFiles(files: List<File>, requestId: String): Pair<List<String>, List<File>> =
        withContext(Dispatchers.IO) {
            val uploaded = mutableListOf<String>()
            val failed = mutableListOf<File>()
            for (file in files) {
                runCatching { uploaded += uploadPendingFile(file, requestId) }.onFailure { failed += file }
            }
            uploaded to failed
        }

    private fun uploadInternal(body: okhttp3.RequestBody, objectPath: String): String {
        val request = Request.Builder()
            .url("$baseUrl/storage/v1/object/request-photos/$objectPath")
            .post(body)
            .addHeader("apikey", anonKey)
            .addHeader("Authorization", "Bearer ${userRepository.getSupabaseAccessToken().orEmpty()}")
            .build()
        val response = client.newCall(request).execute()
        response.use {
            if (!it.isSuccessful) {
                throw IllegalStateException("storage_upload_failed: ${it.code}")
            }
        }
        return "$baseUrl/storage/v1/object/public/request-photos/$objectPath"
    }

    companion object {
        private val JPEG_MEDIA = "image/jpeg".toMediaType()

        /** Directory used to persist photos that failed to upload while offline. */
        fun pendingDir(context: Context): File =
            File(context.applicationContext.cacheDir, "herafi-photo-pending").apply { mkdirs() }

        /** Persist [bytes] locally so they can be re-uploaded later. Returns the saved file. */
        fun savePendingBytes(context: Context, bytes: ByteArray, fileName: String): File {
            val dir = pendingDir(context)
            val file = File(dir, "${UUID.randomUUID()}_$fileName")
            file.writeBytes(bytes)
            return file
        }
    }
}
