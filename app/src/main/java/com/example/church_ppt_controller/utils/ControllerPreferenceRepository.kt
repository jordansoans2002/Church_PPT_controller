package com.example.church_ppt_controller.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.church_ppt_controller.models.ControllerPreferences
import com.example.church_ppt_controller.ui.screens.ControllerViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private const val CONTROLLER_SETTINGS_NAME = "controller_settings"

class ControllerPreferenceRepository(private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(CONTROLLER_SETTINGS_NAME)

    private object PreferenceKeys {
        val IP_ADDRESS = stringPreferencesKey("ip_address")
        val PPT_CONTROL = stringPreferencesKey("ppt_control")
        val GESTURE = stringPreferencesKey("gesture")
        val BUTTON_SETTING = stringPreferencesKey("button_setting")
        val SWIPE_SETTING = stringPreferencesKey("swipe_setting")
        val TAP_SETTING = stringPreferencesKey("tap_setting")
    }

    val controllerPreferencesFlow: Flow<ControllerPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val ipAddress = preferences[PreferenceKeys.IP_ADDRESS] ?: "192.168.1."
            val pptControl = preferences[PreferenceKeys.PPT_CONTROL] ?: ControllerViewModel.PptControlOptions.SINGLE.name
            val gesture = preferences[PreferenceKeys.GESTURE] ?: ControllerViewModel.GestureOptions.BUTTON.name
            val buttonSetting = preferences[PreferenceKeys.BUTTON_SETTING] ?: ControllerViewModel.Orientations.VERTICAL.name
            val swipeSetting = preferences[PreferenceKeys.SWIPE_SETTING] ?: ControllerViewModel.Orientations.VERTICAL.name
            val tapSetting = preferences[PreferenceKeys.TAP_SETTING] ?: ControllerViewModel.TapOptions.SINGLE_TAP.name
            ControllerPreferences(
                ipAddress,
                pptControl,
                gesture,
                buttonSetting,
                swipeSetting,
                tapSetting
            )
        }

    suspend fun updateIpAddress(ipAddress: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.IP_ADDRESS] = ipAddress
        }
    }

    suspend fun updatePptControl(pptControl: ControllerViewModel.PptControlOptions) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.PPT_CONTROL] = pptControl.name
        }
    }

    suspend fun updateGesture(gesture: ControllerViewModel.GestureOptions) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.GESTURE] = gesture.name
        }
    }

    suspend fun updateButtonSetting(buttonSetting: ControllerViewModel.Orientations){
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.BUTTON_SETTING] = buttonSetting.name
        }
    }

    suspend fun updateSwipeSetting(swipeSetting: ControllerViewModel.Orientations) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.SWIPE_SETTING]= swipeSetting.name
        }
    }

    suspend fun updateTapSetting(tapSetting: ControllerViewModel.TapOptions) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.TAP_SETTING] = tapSetting.name
        }
    }
}