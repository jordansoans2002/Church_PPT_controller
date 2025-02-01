package com.example.church_ppt_controller.network

import com.example.church_ppt_controller.models.Requests.SlideChangeRequest
import com.example.church_ppt_controller.models.Responses.SlideChangeResponse
import com.example.church_ppt_controller.models.Responses.SlideshowsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST


interface ControllerApiService {
    @GET("/api/Slideshow/get-slideshows")
    suspend fun getPresentations(): SlideshowsResponse

    @POST("/api/Slideshow/change-slide")
    suspend fun changeSlide(@Body request: SlideChangeRequest): SlideChangeResponse
}

