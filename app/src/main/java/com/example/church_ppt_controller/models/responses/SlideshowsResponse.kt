package com.example.church_ppt_controller.models.responses

import com.example.church_ppt_controller.models.Presentation

data class SlideshowsResponse(
    val presentations: List<Presentation>
)
