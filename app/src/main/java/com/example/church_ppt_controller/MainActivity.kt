package com.example.church_ppt_controller

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.church_ppt_controller.apiCalls.ApiService
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var apiService:ApiService
        fun getRetrofitInstance(baseURL:String): Retrofit{
            return Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()
        }
    }
//    private val sharedPref = this.getPreferences(MODE_PRIVATE)
    private lateinit var switchIds:Array<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_settings)

        val ipAddressBox = findViewById<TextInputEditText>(R.id.ipAddress)

        val startRemote = findViewById<Button>(R.id.startRemoteButton)
        startRemote.setOnClickListener{
            val ipAddress = ipAddressBox.text.toString()
            apiService= getRetrofitInstance("http://$ipAddress:7777").create(ApiService::class.java)

            val intent = Intent(this,ChangeSlideActivity::class.java)
            startActivity(intent)
        }

        switchIds = arrayOf(R.id.buttonSwitch,R.id.swipeSwitch,R.id.tapSwitch)

        val button = findViewById<ConstraintLayout>(R.id.button)
        val buttonSwitch = findViewById<SwitchCompat>(R.id.buttonSwitch)
        buttonSwitch.setOnClickListener{ view ->onSwitchClicked(view as CompoundButton) }

        val swipe = findViewById<ConstraintLayout>(R.id.swipe)
        val swipeSwitch = findViewById<SwitchCompat>(R.id.swipeSwitch)
        swipeSwitch.setOnClickListener{ view ->onSwitchClicked(view as CompoundButton) }

        val tap = findViewById<ConstraintLayout>(R.id.tap)
        val tapSwitch = findViewById<SwitchCompat>(R.id.tapSwitch)
        tapSwitch.setOnClickListener{ view ->onSwitchClicked(view as CompoundButton) }

    }

    private fun onSwitchClicked(switch: CompoundButton){
        if (!switch.isChecked){
            switch.isChecked = true
            Toast.makeText(
                this,
                "At least one gesture must be selected. Select a different option to change",
                Toast.LENGTH_LONG
            ).show()
        } else {
            for (id in switchIds){
                if(id != switch.id) {
                    findViewById<SwitchCompat>(id).isChecked = false
                }
            }
            Toast.makeText(
                this,
                "Only one gesture can be selected",
                Toast.LENGTH_SHORT
            ).show()

            // TODO write to DataStore
        }
    }
}


