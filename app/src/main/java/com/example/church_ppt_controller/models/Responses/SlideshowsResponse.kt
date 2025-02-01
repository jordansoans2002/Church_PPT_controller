package com.example.church_ppt_controller.models.Responses

import com.example.church_ppt_controller.models.Presentation

data class SlideshowsResponse(
    val presentations: List<Presentation>
)
