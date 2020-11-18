package com.livestreaming.channelize.io.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.livestreaming.channelize.io.BuildConfig
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
class RetrofitModule(private val baseUrl: String) {

    private lateinit var client: OkHttpClient

    @Provides
    @Singleton
    fun providesRetrofitInstance(): Retrofit {

        val mainInterceptor = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                requestBuilder.addHeader("publicApiKey", "a")
                requestBuilder.addHeader("publicApiKey", "a")
                requestBuilder.addHeader("publicApiKey", "a")
                val request = requestBuilder.build()
                return chain.proceed(request = request)
            }

        }

        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        client = if (BuildConfig.DEBUG) {
            OkHttpClient.Builder().addInterceptor(mainInterceptor)
                .addInterceptor(httpLoggingInterceptor).connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).build()

        } else {
            OkHttpClient.Builder().addInterceptor(mainInterceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).build()
        }

        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory()).client(client).build()

    }


}

