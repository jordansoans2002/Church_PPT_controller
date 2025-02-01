package com.example.church_ppt_controller.models.Requests

data class SlideChangeRequest(
    val presentationIds: List<String>,
    val slideChange: Int,
    val options:Map<String,Boolean> = mapOf(
        Pair("currentSlidePreview", false),
        Pair("nextSlidePreview", false),
        Pair("previousSlidePreview", false)
    )
)
