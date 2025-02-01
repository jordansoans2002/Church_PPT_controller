package com.example.church_ppt_controller.models

import com.example.church_ppt_controller.ui.screens.ControllerViewModel.*

data class ControllerPreferences(
    val ipAddress: String = "192.168.1.",
    val pptControlSetting: String = PptControlOptions.SINGLE.name,
    val gestureSelected: String = GestureOptions.BUTTON.name,
    val buttonSetting: String = Orientations.VERTICAL.name,
    val swipeSetting: String = Orientations.VERTICAL.name,
    val tapSetting: String = TapOptions.SINGLE_TAP.name,
)
