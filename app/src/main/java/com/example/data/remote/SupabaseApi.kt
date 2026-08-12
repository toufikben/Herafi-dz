package com.example.data.remote

import com.example.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * Read-only Supabase gateway for the migration phase.
 * Never use the service_role key in this client.
 */
interface SupabaseApi {
    @GET("rest/v1/craftsmen")
    @Headers("Accept-Profile: public")
    suspend fun getPublishedCraftsmen(
        @Query("status") status: String = "eq.published",
        @Query("select") select: String = "*",
        @Query("order") order: String = "rating_score.desc"
    ): List<RemoteCraftsman>
}

data class RemoteCraftsman(
    val id: String,
    val owner_id: String?,
    val name: String,
    val category_key: String,
    val wilaya_code: String,
    val commune: String,
    val phone: String,
    val whatsapp: String?,
    val description: String,
    val daily_rate_dzd: Int?,
    val years_experience: Int,
    val skills_csv: String,
    val is_verified: Boolean,
    val status: String,
    val rating_score: Double,
    val rating_count: Int
)

private class SupabaseHeadersInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("apikey", BuildConfig.SUPABASE_ANON_KEY)
            .addHeader("Accept", "application/json")
            .build()
        return chain.proceed(request)
    }
}

object SupabaseApiProvider {
    fun create(): SupabaseApi? {
        val baseUrl = BuildConfig.SUPABASE_URL.trim().let {
            if (it.isBlank() || it.contains("YOUR_PROJECT_REF")) return null
            if (it.endsWith("/")) it else "$it/"
        }
        if (BuildConfig.SUPABASE_ANON_KEY.isBlank() || BuildConfig.SUPABASE_ANON_KEY.contains("YOUR_PUBLIC_ANON_KEY")) {
            return null
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(SupabaseHeadersInterceptor())
            .build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .build()
            .create(SupabaseApi::class.java)
    }
}
