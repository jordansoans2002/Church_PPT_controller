package com.example.church_ppt_controller.models

import android.media.Image
import com.google.gson.annotations.SerializedName

data class Slide (
    @SerializedName("slideImage")
    val image:Image,

    @SerializedName("lang1Lyrics")
    val lang1Lyrics:String,

    @SerializedName("lang2Lyrics")
    val lang2Lyrics:String
)
