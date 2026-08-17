package com.example.data.remote

import android.content.Context
import com.example.BuildConfig
import com.example.data.db.AppDatabase
import com.example.data.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.UUID

/**
 * Minimal authenticated Supabase Storage client for request photos.
 * Uploads go to the `request-photos` bucket under a user-scoped prefix.
 */
class SupabaseStorage(context: Context) {

    private val client = OkHttpClient()
    private val userRepository = UserRepository(AppDatabase.getInstance(context.applicationContext).userDao(), context.applicationContext)

    private val baseUrl = BuildConfig.SUPABASE_URL.trim().trimEnd('/')
    private val anonKey = BuildConfig.SUPABASE_ANON_KEY

    fun upload(bytes: ByteArray, fileName: String): String {
        val userId = userRepository.getCurrentUserId()
        val objectPath = "${userId ?: "anon"}/${UUID.randomUUID()}/$fileName"
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", fileName, bytes.toRequestBody("image/jpeg".toMediaType()))
            .build()
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
}
