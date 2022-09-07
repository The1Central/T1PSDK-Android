package th.co.the1.t1p.sdk.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import th.co.the1.t1p.sdk.domain.T1PEnvironment
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private const val TIME_OUT = 15L

    var t1PEnvironment: T1PEnvironment? = null

    fun getInstance(): T1PSdkApi {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(getBaseUrl(t1PEnvironment))
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(T1PSdkApi::class.java)
    }

    private fun getBaseUrl(t1PEnvironment: T1PEnvironment?): String {
        return t1PEnvironment?.baseUrl ?: ""
    }
}
