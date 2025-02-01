package com.example.church_ppt_controller.models

import com.example.church_ppt_controller.models.Responses.OperationResult
import kotlinx.serialization.Serializable

@Serializable
data class Presentation(
    val presentationId: String,
    val fileName: String,
    val filePath: String,
    val windowTitle: String,
    val isRunning: Boolean,
    val currentSlide: Int,
    val totalSlides: Int,
)
