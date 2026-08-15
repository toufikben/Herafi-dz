package com.example.data.remote

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

@JsonClass(generateAdapter = true)
data class SupabaseSignUpRequest(
    val email: String,
    val password: String,
    val data: Map<String, String> = emptyMap()
)

@JsonClass(generateAdapter = true)
data class SupabaseSignInRequest(
    val email: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class SupabaseAuthIdentity(
    val id: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseAuthUser(
    val id: String,
    val email: String? = null,
    @Json(name = "user_metadata") val userMetadata: Map<String, String>? = null,
    val identities: List<SupabaseAuthIdentity>? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseAuthResponse(
    @Json(name = "access_token") val accessToken: String? = null,
    @Json(name = "refresh_token") val refreshToken: String? = null,
    val user: SupabaseAuthUser? = null
)

interface SupabaseAuthApi {
    @POST("signup")
    @Headers("Content-Type: application/json")
    suspend fun signUp(@Body request: SupabaseSignUpRequest): SupabaseAuthResponse

    @POST("token?grant_type=password")
    @Headers("Content-Type: application/json")
    suspend fun signIn(@Body request: SupabaseSignInRequest): SupabaseAuthResponse

    @GET("user")
    suspend fun getCurrentUser(): SupabaseAuthUser
}

private class SupabaseAuthHeadersInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("apikey", BuildConfig.SUPABASE_ANON_KEY)
            .addHeader("Accept", "application/json")
            .build()
        return chain.proceed(request)
    }
}

object SupabaseAuthApiProvider {
    fun create(): SupabaseAuthApi? {
        val baseUrl = BuildConfig.SUPABASE_URL.trim().let {
            if (it.isBlank() || it.contains("YOUR_PROJECT_REF")) return null
            if (it.endsWith("/")) it else "$it/"
        }
        if (BuildConfig.SUPABASE_ANON_KEY.isBlank() || BuildConfig.SUPABASE_ANON_KEY.contains("YOUR_PUBLIC_ANON_KEY")) {
            return null
        }
        return Retrofit.Builder()
            .baseUrl("${baseUrl}auth/v1/")
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(SupabaseAuthHeadersInterceptor())
                    .addInterceptor(
                        HttpLoggingInterceptor { message ->
                            android.util.Log.d("SupabaseHttp", message)
                        }.apply {
                            if (BuildConfig.DEBUG) setLevel(HttpLoggingInterceptor.Level.BASIC)
                        }
                    )
                    .build()
            )
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(SupabaseAuthApi::class.java)
    }
}
