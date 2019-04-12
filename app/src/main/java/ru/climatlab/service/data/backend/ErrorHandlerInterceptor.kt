package ru.climatlab.service.data.backend

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import ru.climatlab.service.data.api.ServerException
import ru.climatlab.service.data.model.ErrorResponse

/**
 * @author ilya
 */
class ErrorHandlerInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        val contentType = response.body()?.contentType()
        val responseString = response.body()?.string() ?: ""

        var errorResponse: ErrorResponse? = null
        if (responseString.contains("error_code")) {
            errorResponse = Gson().fromJson(responseString, ErrorResponse::class.java)
        }

        if (errorResponse != null) {
            throw ServerException(errorResponse.code, errorResponse.message)
        }

        return response.newBuilder().body(ResponseBody.create(contentType, responseString)).build()
    }
}