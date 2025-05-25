package com.example.church_ppt_controller

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.church_ppt_controller.ui.screens.ChangeSlideScreen
import com.example.church_ppt_controller.ui.screens.ControllerViewModel
import com.example.church_ppt_controller.ui.screens.ControllerViewModelFactory
import com.example.church_ppt_controller.ui.screens.SettingsScreen
import com.example.church_ppt_controller.utils.ControllerPreferenceRepository


enum class ChurchPptControllerScreen {
    Settings,
    ChangeSlide
}

@Composable
fun ChurchPptControllerApp(
    navController: NavHostController = rememberNavController(),
    controllerPreferenceRepository: ControllerPreferenceRepository
){
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = ChurchPptControllerScreen.valueOf(
        backStackEntry?.destination?.route ?: ChurchPptControllerScreen.Settings.name
    )
    val controllerViewModel: ControllerViewModel = viewModel(
        factory = ControllerViewModelFactory(controllerPreferenceRepository)
    )


    Scaffold { innerPadding ->
       NavHost(
           navController = navController,
           startDestination = ChurchPptControllerScreen.Settings.name,
           modifier = Modifier
               .fillMaxSize()
               .padding(innerPadding)
       ) {
           composable(route = ChurchPptControllerScreen.Settings.name){
               SettingsScreen(
                   onNavigateToControlSlideshow = { navController.navigate(ChurchPptControllerScreen.ChangeSlide.name)},
                   viewModel = controllerViewModel,
                   modifier = Modifier
                   .fillMaxSize()
                   .padding(8.dp),
               )
           }

           composable(route = ChurchPptControllerScreen.ChangeSlide.name){
               ChangeSlideScreen(
                   viewModel = controllerViewModel,
                   modifier = Modifier
                       .fillMaxSize()
                       .padding(2.dp),
               )
           }
       }
    }

}