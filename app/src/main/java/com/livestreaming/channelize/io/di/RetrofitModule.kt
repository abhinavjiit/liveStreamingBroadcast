package com.livestreaming.channelize.io.di

import android.content.Context
import android.util.Base64
import com.channelize.apisdk.utils.ChannelizePreferences
import com.channelize.apisdk.utils.Logcat
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.livestreaming.channelize.io.BuildConfig
import com.livestreaming.channelize.io.SharedPrefUtils
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.UnsupportedEncodingException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
class RetrofitModule(
    private val lscBaseUrl: String,
    private val context: Context
    , private val productListBaseUrl: String,
    private val channelizeCorBaseUrl: String
) {

    private lateinit var client: OkHttpClient

    @Provides
    @Singleton
    @com.livestreaming.channelize.io.di.Retrofit
    fun providesRetrofitInstance(): Retrofit {
        val mainInterceptor = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                SharedPrefUtils.getPublicApiKey(context)?.let { publicApiKey ->
                    requestBuilder.addHeader("Public-key", publicApiKey)
                    requestBuilder.addHeader("Content_Type", "application/json")
                }
                addAuthHeader(requestBuilder)
                val request = requestBuilder.build()
                return chain.proceed(request = request)
            }
        }

        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        client = if (BuildConfig.DEBUG) {
            OkHttpClient.Builder().addInterceptor(mainInterceptor)
                .retryOnConnectionFailure(true)
                .addInterceptor(httpLoggingInterceptor).connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
                .build()
        } else {
            OkHttpClient.Builder().addInterceptor(mainInterceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
                .build()
        }

        return Retrofit.Builder().baseUrl(lscBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory()).client(client).build()
    }

    private fun addAuthHeader(requestBuilder: Request.Builder) {
        val accessToken = ChannelizePreferences.getAccessToken(context)
        var base64EncodedAccessToken: String? = null
        if (!accessToken.isNullOrBlank()) {
            try {
                base64EncodedAccessToken = Base64.encodeToString(
                    accessToken
                        .toByteArray(charset("UTF-8")), Base64.NO_WRAP
                )
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
        }
        requestBuilder.addHeader("Authorization", "Bearer ".plus(base64EncodedAccessToken))
        Logcat.d("Header", requestBuilder.toString())
    }


    @Provides
    @ProductsListRetrofit
    @Singleton
    fun providesRetrofitInstanceForProductList(): Retrofit {
        val mainInterceptor = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                val request = requestBuilder.build()
                return chain.proceed(request = request)
            }
        }

        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        client = if (BuildConfig.DEBUG) {
            OkHttpClient.Builder().addInterceptor(mainInterceptor)
                .retryOnConnectionFailure(true)
                .addInterceptor(httpLoggingInterceptor).connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
                .build()

        } else {
            OkHttpClient.Builder().addInterceptor(mainInterceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
                .build()
        }

        return Retrofit.Builder().baseUrl(productListBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory()).client(client).build()
    }

    @Provides
    @Singleton
    @CoreUrlRetrofit
    fun providesRetrofitInstanceForGettingAppID(): Retrofit {
        val mainInterceptor = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                SharedPrefUtils.getPublicApiKey(context)?.let { publicApiKey ->
                    requestBuilder.addHeader("Public-key", publicApiKey)
                    requestBuilder.addHeader("Content_Type", "application/json")
                }
                addAuthHeader(requestBuilder)
                val request = requestBuilder.build()
                return chain.proceed(request = request)
            }

        }

        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        client = if (BuildConfig.DEBUG) {
            OkHttpClient.Builder().addInterceptor(mainInterceptor)
                .retryOnConnectionFailure(true)
                .addInterceptor(httpLoggingInterceptor).connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
                .build()

        } else {
            OkHttpClient.Builder().addInterceptor(mainInterceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
                .build()
        }

        return Retrofit.Builder().baseUrl(channelizeCorBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory()).client(client).build()
    }

}

