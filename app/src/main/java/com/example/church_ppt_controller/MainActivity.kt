package com.example.church_ppt_controller

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.church_ppt_controller.ui.screens.ControllerViewModel
import com.example.church_ppt_controller.ui.theme.Church_PPT_controllerTheme
import com.example.church_ppt_controller.utils.ControllerPreferenceRepository

class MainActivity : AppCompatActivity() {

    private val controllerPreferenceRepository by lazy {
        ControllerPreferenceRepository(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            Church_PPT_controllerTheme {
                ChurchPptControllerApp(controllerPreferenceRepository = controllerPreferenceRepository)
            }
        }
    }
}


