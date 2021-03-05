package com.app.tawktest.apiCalling

import com.app.tawktest.response.UserDetail
import com.app.tawktest.response.UserList
import retrofit2.Call
import retrofit2.http.*

interface APIService {

    @GET("users")
    fun userList(@Query("since") since: Int, @Query("per_page") per_page :Int): Call<UserList?>

    @GET()
    fun userDetails(@Url() userName: String): Call<UserDetail?>

}