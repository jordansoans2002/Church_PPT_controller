@file:JvmName("ChangeSlideScreenKt")

package com.example.church_ppt_controller.ui.screens

import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.church_ppt_controller.R
import com.example.church_ppt_controller.models.Presentation
import com.example.church_ppt_controller.ui.screens.ControllerViewModel.PptControlOptions
import kotlinx.coroutines.flow.collectLatest


@Composable
fun ChangeSlideScreen(
    viewModel: ControllerViewModel,
    modifier: Modifier = Modifier,
) {
    val controlledSlideshows = viewModel.controlledSlideshows.collectAsState()
    val changeSlideshows = viewModel.changeSlideshows.collectAsState()
    val gesture = viewModel.gestureSelected.collectAsState()
    val pptControlMode = viewModel.pptControl.collectAsState()

    var borderWidth = 0.dp

    val screenOrientation = LocalConfiguration.current.orientation
    when(gesture.value){
        ControllerViewModel.GestureOptions.BUTTON -> borderWidth = 4.dp
        ControllerViewModel.GestureOptions.SWIPE -> {
            modifier.pointerInput(Unit) {
                when(screenOrientation){
                    Configuration.ORIENTATION_LANDSCAPE -> {
                        detectHorizontalDragGestures { change, dragAmount ->
                            Log.i("Swipe","Change: $change Drag amount: $dragAmount")
                        }
                    }
                    else -> {
                        detectVerticalDragGestures { change, dragAmount ->
                            Log.i("Swipe","Change: $change Drag amount: $dragAmount")
                        }
                    }
                }
            }
        }
        ControllerViewModel.GestureOptions.TAP -> {
            modifier.pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        Log.d("double tap","DOUBLE TAP")
                        if(viewModel.tapSetting.value == ControllerViewModel.TapOptions.SINGLE_TAP)
                            viewModel.changeSlides(-1)
                        else
                            viewModel.changeSlides(1)
                    },
                    onTap = {
                        if(viewModel.tapSetting.value == ControllerViewModel.TapOptions.SINGLE_TAP)
                            viewModel.changeSlides(1)
                        else
                            viewModel.changeSlides(-1)
                    }
                )
            }
        }
    }

    if(gesture.value != ControllerViewModel.GestureOptions.TAP){
        Box(modifier) {
            if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                Column {
                    Row {
                        Box(
                            modifier = Modifier
                                .clickable(enabled = (borderWidth != 0.dp))
                                { viewModel.changeSlides(-1) }
                                .weight(1f)
                                .fillMaxHeight()
                                .border(BorderStroke(borderWidth, Color.Gray)),
                        ) {
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
                            items(controlledSlideshows.value) { slideshow ->
                                HorizontalDivider()
                                Box {
                                    SlideshowProgress(slideshow)
                                    Selector(
                                        text = slideshow.fileName,
                                        checked = changeSlideshows.value.any { it.presentationId == slideshow.presentationId },
                                        multiSelect = pptControlMode.value == PptControlOptions.SIMULTANEOUS,
                                        onClick = {
                                            viewModel.updateChangeSlideshows(slideshow)
                                            Log.i("Change slides", changeSlideshows.value.toString())
                                        }
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clickable(enabled = (borderWidth != 0.dp))
                                { viewModel.changeSlides(1) }
                                .weight(1f)
                                .fillMaxHeight()
                                .border(BorderStroke(borderWidth, Color.Gray)),
                        ) {
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
                }
            } else {
                Column {
                    Box(
                        modifier = Modifier
                            .clickable(enabled = (borderWidth != 0.dp))
                            { viewModel.changeSlides(-1) }
                            .weight(1f)
                            .fillMaxWidth()
                            .border(BorderStroke(borderWidth, Color.Gray))
                    ) {
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
                        items(controlledSlideshows.value) { slideshow ->
                            HorizontalDivider()
                            Box {
                                SlideshowProgress(slideshow)
                                Selector(
                                    text = slideshow.fileName,
                                    checked = changeSlideshows.value.any { it.presentationId == slideshow.presentationId },
                                    multiSelect = pptControlMode.value == PptControlOptions.SIMULTANEOUS,
                                    onClick = {
                                        viewModel.updateChangeSlideshows(slideshow)
                                    }
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clickable(enabled = (borderWidth != 0.dp))
                            { viewModel.changeSlides(1) }
                            .weight(1f)
                            .fillMaxWidth()
                            .border(BorderStroke(borderWidth, Color.Gray))
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
        }
    } else {
        Box {
            LazyColumn(
                modifier = Modifier
                    .align(Alignment.Center),
            ) {
                items(controlledSlideshows.value) { slideshow ->
                    HorizontalDivider()
                    Box {
                        SlideshowProgress(slideshow)
                        Selector(
                            text = slideshow.fileName,
                            checked = changeSlideshows.value.any { it.presentationId == slideshow.presentationId },
                            multiSelect = pptControlMode.value == PptControlOptions.SIMULTANEOUS,
                            onClick = {
                                viewModel.updateChangeSlideshows(slideshow)
                            }
                        )
                    }
                }
            }
        }
    }


    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    LaunchedEffect(true){
        viewModel.error.collectLatest {
            if(it.isNotEmpty()) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                Toast.makeText(
                    context,
                    it,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}


@Composable
fun SlideshowProgress(presentation: Presentation,modifier: Modifier = Modifier){
    val progress = presentation.currentSlide*1.0f/presentation.totalSlides
    var progressColor = Color.Green
    if(presentation.currentSlide==1 || progress >= 1)
        progressColor = Color.Red
    Box(modifier.fillMaxWidth()) {
        Box(Modifier
            .height(25.dp)
            .fillMaxWidth(progress)
            .background(progressColor)
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