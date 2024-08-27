package com.example.church_ppt_controller

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.church_ppt_controller.MainActivity.Companion.apiService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeSlideActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_change_slide)

        val previousSlide = findViewById<ImageView>(R.id.previousSlide)
        previousSlide.setOnClickListener{changeSlide(-1)}
        val nextSlide = findViewById<ImageView>(R.id.nextSlide)
        nextSlide.setOnClickListener{changeSlide(1)}
    }

    private fun changeSlide(n:Int){
        val body = mapOf(
            "count" to n
        )
        apiService.changeSlide(body).enqueue(object: Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                println(response)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println(t.toString())
            }

        })
    }
}