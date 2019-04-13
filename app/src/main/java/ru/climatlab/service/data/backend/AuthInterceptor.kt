package ru.climatlab.service.data.backend

import okhttp3.Interceptor
import okhttp3.Response
import ru.climatlab.service.data.PreferencesRepository
import java.util.*

/**
 * @author ilya
 */
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()
        val requestBuilder = request.newBuilder().addHeader(
            "Accept-Language",
            "${Locale.getDefault().language}-${Locale.getDefault().language.toUpperCase()}"
        )
        if (!request.url().encodedPath().contains("/api/login.php")) {
            val token: String = PreferencesRepository.getToken()
            requestBuilder.addHeader("token", token)
                .addHeader(
                    "Accept-Language",
                    "${Locale.getDefault().language}-${Locale.getDefault().language.toUpperCase()}"
                )
                .apply {
                    if (request.url().toString().contains("?")) url("${request.url()}&token=${PreferencesRepository.getToken()}") else url(
                        "${request.url()}?token=${PreferencesRepository.getToken()}"
                    )
                }
        }
        request = requestBuilder.build()

        return chain.proceed(request)
    }
}