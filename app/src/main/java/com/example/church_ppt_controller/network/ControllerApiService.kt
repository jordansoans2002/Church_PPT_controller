package com.example.church_ppt_controller.network

import com.example.church_ppt_controller.models.requests.SlideChangeRequest
import com.example.church_ppt_controller.models.responses.SlideChangeResponse
import com.example.church_ppt_controller.models.responses.SlideshowsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ControllerApiService {
    @GET("/api/Slideshow/get-slideshows")
    suspend fun getPresentations(): SlideshowsResponse

    @POST("/api/Slideshow/change-slide")
    suspend fun changeSlide(@Body request: SlideChangeRequest): SlideChangeResponse
}

