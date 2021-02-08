package com.ilkeruzer.basketballheatmap.data.api

import android.annotation.SuppressLint
import com.ilkeruzer.basketballheatmap.BuildConfig.PROJECT_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class NetworkModule {

    fun service(): ServiceInterface {

        val httpClient = getHttpClient()

        val clt = httpClient.build()
        //val clt =
        //        getUnsafeOkHttpClient(httpClient).build() //(for user certificate https handshake problem)
        val retrofit = getRetrofitBuilder(clt).build()

        return retrofit.create(ServiceInterface::class.java)
    }
}

fun getUnsafeOkHttpClient(builder: OkHttpClient.Builder): OkHttpClient.Builder {
    try {
        // Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory = sslContext.socketFactory

        //val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        builder.hostnameVerifier({ hostname, session -> true })
        return builder
    } catch (e: Exception) {
        e.printStackTrace()
        throw RuntimeException(e)
    }

}

fun getHttpClient(addAccessToken: Boolean = true): OkHttpClient.Builder {


    val httpClient = OkHttpClient.Builder()
    httpClient.connectTimeout(1, TimeUnit.MINUTES)
    httpClient.readTimeout(1, TimeUnit.MINUTES)
    httpClient.callTimeout(1, TimeUnit.MINUTES)




    return httpClient
}

fun getRetrofitBuilder(client: OkHttpClient): Retrofit.Builder {
    return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(PROJECT_URL)
            .client(client)
}