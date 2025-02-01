@file:JvmName("ChangeSlideScreenKt")

package com.example.church_ppt_controller.ui.screens

import android.content.res.Configuration
import androidx.annotation.FloatRange
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.church_ppt_controller.R
import com.example.church_ppt_controller.models.Presentation


@Composable
fun ChangeSlideScreen(
    viewModel: ControllerViewModel,
    modifier: Modifier = Modifier,
) {
    if(viewModel.gestureSelected.value != ControllerViewModel.GestureOptions.TAP){
        if(LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Row {
                Box(
                    modifier = Modifier
                        .clickable { viewModel.changeSlides(-1) }
                        .weight(1f)
                        .fillMaxHeight()
                        .border(BorderStroke(1.dp,Color.Gray)),
                ){
                    Image(
                        painter = painterResource(R.drawable.arrow_left),
                        contentDescription = "Previous slide button",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .matchParentSize()
                    )
                    Text(
                        text = "Previous slide",
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(0.30f)
                        .align(Alignment.CenterVertically)
                ) {
                   items(viewModel.controlledSlideshows.value) { presentation ->
                       SlideshowProgress(presentation)
                   }
                }

                Box(
                    modifier = Modifier
                        .clickable { viewModel.changeSlides(1) }
                        .weight(1f)
                        .fillMaxHeight()
                        .border(BorderStroke(1.dp,Color.Gray)),
                ){
                    Image(
                        painter = painterResource(R.drawable.arrow_right),
                        contentDescription = "Next slide button",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .matchParentSize()
                    )
                    Text(
                        text = "Next slide",
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                    )
                }
            }
        } else {
            Column {
                Box(
                    modifier =  Modifier
                        .clickable { viewModel.changeSlides(-1) }
                        .weight(1f)
                        .fillMaxWidth()
                        .border(BorderStroke(1.dp,Color.Gray))
                ){
                    Image(
                        painter = painterResource(R.drawable.arrow_up),
                        contentDescription = "Previous slide button",
                        modifier = Modifier
                            .matchParentSize()
                    )
                    Text(
                        text = "Previous slide",
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                ) {
                    items(viewModel.controlledSlideshows.value) { presentation ->
                        SlideshowProgress(presentation)
                    }
                }

                Box(
                    modifier =  Modifier
                        .clickable { viewModel.changeSlides(1) }
                        .weight(1f)
                        .fillMaxWidth()
                        .border(BorderStroke(1.dp,Color.Gray))
                ) {
                    Image(
                        painter = painterResource(R.drawable.arrow_down),
                        contentDescription = "Next slide button",
                        modifier = Modifier
                            .matchParentSize()
                    )
                    Text(
                        text = "Next slide",
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                    )
                }
            }
        }
    } else {

    }
}


@Composable
fun SlideshowProgress(presentation: Presentation){
    Box(Modifier.fillMaxWidth()) {
        Box(Modifier
            .height(35.dp)
            .fillMaxWidth(presentation.currentSlide*1.0f/presentation.totalSlides)
            .background(Color.Green)
        )
        Text(
            text = presentation.fileName,
            modifier = Modifier
                .align(Alignment.Center)
                .border(2.dp,Color.Magenta)
        )

    }
}


//@Preview(showBackground = true)
//@Preview(showSystemUi = true)
//@Composable
//fun HomeScreenPreview(){
//    Church_PPT_controllerTheme {
//        ChangeSlideScreen(
//            modifier = Modifier
////                .fillMaxSize()
//                .padding(8.dp),
//        )
//    }
//}