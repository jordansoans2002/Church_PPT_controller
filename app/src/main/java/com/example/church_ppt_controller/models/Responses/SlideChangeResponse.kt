package com.example.church_ppt_controller.models.Responses

import android.service.autofill.Presentations
import com.example.church_ppt_controller.models.Presentation
import kotlinx.serialization.Serializable

@Serializable
data class SlideChangeResponse (
    val success: Boolean,
    val presentations: List<OperationResult>
)

@Serializable
data class OperationResult(
    val statusCode: Int,
    val message: String = "",
    val presentationInfo: Presentation
)