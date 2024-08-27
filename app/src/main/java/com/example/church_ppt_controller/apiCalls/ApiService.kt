package com.example.church_ppt_controller.apiCalls;


import com.example.church_ppt_controller.models.Slide
import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("/")
    fun testApiCall():Call<ResponseBody>

    @GET("/current_slide")
    fun getCurrentSlide(@Body body: Map<String, String>): Response<Slide>

    @POST("/change_slide")
    fun changeSlide(@Body body: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>
}
