package ru.climatlab.service.data.backend

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import ru.climatlab.service.data.model.RequestModel
import ru.climatlab.service.data.model.RequestReport
import ru.climatlab.service.data.model.TokenResponse

/**
 * Created by tridetch on 07.04.2019. CliamtLabService
 */
interface ClimatLabApiService {
    @POST("/api/login.php")
    fun login(@Query("login") login: String, @Query("password") password: String): Single<TokenResponse>

    @GET("/api/requests.php")
    fun getRequests(): Single<List<RequestModel>>

    @POST("/api/request.php")
    fun sendRequestReport(@Query("updateRequest") updateRequest: String = "1", @Body requestReport: RequestReport)
}