package ru.climatlab.service.data.backend

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import ru.climatlab.service.data.model.RequestModel
import ru.climatlab.service.data.model.RequestReport
import ru.climatlab.service.data.model.TokenResponse
import java.util.*

/**
 * Created by tridetch on 07.04.2019. CliamtLabService
 */
interface ClimatLabApiService {
    @POST("/api/login.php")
    fun login(@Query("login") login: String, @Query("password") password: String): Single<TokenResponse>

    @GET("/api/requests.php")
    fun getRequests(): Single<List<RequestModel>>

    @POST("/api/request.php")
    fun sendRequestReport(@Query("updateRequest") updateRequest: String = "1", @Body requestReport: RequestReport): Completable

    @POST("/api/request.php")
    fun acceptRequest(@Query("requestId") requestId: String, @Query("acceptRequest") acceptRequest: Int = 1, @Query("date_update") date: Date = Date()): Completable

    @POST("/api/request.php")
    fun cancelRequest(@Query("requestId") requestId: String, @Query("comment") comment: String): Completable

}