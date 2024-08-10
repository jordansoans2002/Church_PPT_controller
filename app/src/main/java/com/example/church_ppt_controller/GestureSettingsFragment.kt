package com.example.church_ppt_controller

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class GestureSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.slide_change_preferences, rootKey)
    }
}