package com.example.simplenote.core.network.business

import okhttp3.*

class AuthInterceptor(private val tokenProvider: () -> String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val excludedPaths = listOf("/api/auth/register/", "/api/auth/token/", "/api/auth/token/refresh/")

        return if (excludedPaths.any { originalRequest.url.encodedPath.endsWith(it) }) {
            val newRequest = originalRequest.newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(newRequest)
        } else {
            val newRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer ${tokenProvider()}")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(newRequest)
        }
    }
}