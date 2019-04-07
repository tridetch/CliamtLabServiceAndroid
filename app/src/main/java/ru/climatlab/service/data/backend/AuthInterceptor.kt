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
        if (!request.url().encodedPath().contains("/api/login.php")) {
            val token: String = PreferencesRepository.getToken()
            request = request.newBuilder()
                    .addHeader("token", token)
                    .addHeader("Accept-Language", "${Locale.getDefault().language}-${Locale.getDefault().language.toUpperCase()}")
                    .build()
        }

        return chain.proceed(request)
    }
}