package com.example.data.remote

import com.example.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
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

    @GET("rest/v1/reviews")
    @Headers("Accept-Profile: public")
    suspend fun getReviewsForCraftsman(
        @Query("craftsman_id") craftsmanId: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "created_at.desc"
    ): List<RemoteReview>

    @GET("rest/v1/service_requests")
    @Headers("Accept-Profile: public")
    suspend fun getServiceRequestsForCustomer(
        @Query("customer_id") customerId: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "created_at.desc"
    ): List<RemoteServiceRequest>

    @POST("rest/v1/service_requests")
    @Headers("Content-Profile: public", "Prefer: return=representation")
    suspend fun createServiceRequest(
        @Body request: CreateServiceRequestBody
    ): List<RemoteServiceRequest>
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

data class RemoteReview(
    val id: String,
    val craftsman_id: String,
    val reviewer_id: String,
    val score_ten: Double,
    val comment: String,
    val created_at: String
)

data class CreateServiceRequestBody(
    val customer_id: String,
    val craftsman_id: String?,
    val category_key: String,
    val wilaya_code: String,
    val commune: String,
    val description: String,
    val status: String = "open"
)

data class RemoteServiceRequest(
    val id: String,
    val customer_id: String,
    val craftsman_id: String?,
    val category_key: String,
    val wilaya_code: String,
    val commune: String,
    val description: String,
    val status: String,
    val created_at: String? = null,
    val updated_at: String? = null
)

private class SupabaseHeadersInterceptor(
    private val accessToken: String?
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
            .addHeader("apikey", BuildConfig.SUPABASE_ANON_KEY)
            .addHeader("Accept", "application/json")
        if (!accessToken.isNullOrBlank()) builder.addHeader("Authorization", "Bearer $accessToken")
        val request = builder.build()
        return chain.proceed(request)
    }
}

object SupabaseApiProvider {
    fun create(accessToken: String? = null): SupabaseApi? {
        val baseUrl = BuildConfig.SUPABASE_URL.trim().let {
            if (it.isBlank() || it.contains("YOUR_PROJECT_REF")) return null
            if (it.endsWith("/")) it else "$it/"
        }
        if (BuildConfig.SUPABASE_ANON_KEY.isBlank() || BuildConfig.SUPABASE_ANON_KEY.contains("YOUR_PUBLIC_ANON_KEY")) {
            return null
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(SupabaseHeadersInterceptor(accessToken))
            .build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(SupabaseApi::class.java)
    }
}
