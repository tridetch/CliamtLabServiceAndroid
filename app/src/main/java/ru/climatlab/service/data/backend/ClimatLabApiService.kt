package ru.climatlab.service.data.backend

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import ru.climatlab.service.data.model.*

/**
 * Created by tridetch on 07.04.2019. CliamtLabService
 */
interface ClimatLabApiService {
    @POST("/api/login.php")
    fun login(@Query("login") login: String, @Query("password") password: String): Single<CurrentUserResponse>

    @GET("/api/requests.php")
    fun getRequests(): Single<List<RequestResponseModel>>

    @POST("/api/request.php")
    fun sendRequestReport(@Query("updateRequest") updateRequest: Int = 1, @Body requestReport: RequestReport): Completable

    @POST("/api/request.php")
    fun acceptRequest(@Query("acceptRequest") acceptRequest: Int = 1, @Body acceptRequestBody: AcceptRequestBody): Completable

    @POST("/api/request.php")
    fun cancelRequest(@Query("cancelRequest") cancelRequest: Int = 1, @Body cancelRequestBody: CancelRequestBody): Completable

    @POST("/api/clients.php")
    fun getClients(): Single<List<ClientResponseModel>>

    @POST("/api/profile.php")
    fun postNotificationToken(@Body tokenRequestBody: TokenRequestBody): Completable

    @POST("/api/geo.php")
    fun sendUserLocation(@Body userLocation: MapCoordinates): Completable

    @POST("/api/upload_file.php")
    fun sendPhoto(@Body photo: SelectedFile): Completable

}