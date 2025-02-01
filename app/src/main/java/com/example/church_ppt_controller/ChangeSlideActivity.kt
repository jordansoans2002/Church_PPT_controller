package com.example.church_ppt_controller

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class ChangeSlideActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.fragment_change_slide)

        val previousSlide = findViewById<ImageView>(R.id.previousSlide)
        previousSlide.setOnClickListener{changeSlide(-1)}
        val nextSlide = findViewById<ImageView>(R.id.nextSlide)
        nextSlide.setOnClickListener{changeSlide(1)}
    }

    private fun changeSlide(n:Int){
        val body = mapOf(
            "count" to n,
            "option" to "all"
        )
//        apiService.changeSlide(body).enqueue(object: Callback<ResponseBody> {
//            override fun onResponse(
//                call: Call<ResponseBody>,
//                response: Response<ResponseBody>
//            ) {
//                println(response.body()!!.string())
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                println(t.toString())
//            }
//
//        })
    }
}