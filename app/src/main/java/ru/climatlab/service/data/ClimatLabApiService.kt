package ru.climatlab.service.data

import io.reactivex.Completable
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by tridetch on 07.04.2019. CliamtLabService
 */
interface ClimatLabApiService {
    @POST
    fun login(@Query("login") login: String, @Query("password") password: String): Completable
}