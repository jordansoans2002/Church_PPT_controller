package com.example.church_ppt_controller

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.preference.PreferenceFragmentCompat
import com.example.church_ppt_controller.ui.theme.Church_PPT_controllerTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, GestureSettingsFragment())
                .commit()
        }

        val startRemote = findViewById<Button>(R.id.startRemoteButton);
        startRemote.setOnClickListener{
            val intent = Intent(this,ChangeSlideActivity::class.java)
            startActivity(intent)
        }

    }

    class GestureSettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.slide_change_preferences, rootKey)
        }
    }
}