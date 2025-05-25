package com.example.church_ppt_controller.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.church_ppt_controller.ui.screens.ControllerViewModel.*
import kotlinx.coroutines.flow.collectLatest


@Composable
fun SettingsScreen(
    onNavigateToControlSlideshow: () -> Unit,
    viewModel: ControllerViewModel,
    modifier: Modifier = Modifier,
) {
    val ipAddress = viewModel.ipAddress.collectAsState()
    Column(modifier = modifier) {
        OutlinedTextField(
            value = ipAddress.value,
            label = { Text("Enter IP address") },
            singleLine = true,
            onValueChange = { viewModel.updateIpAddress(it) },
            modifier = Modifier
                .fillMaxWidth()
        )

        Button(
            modifier = Modifier
                .padding(0.dp,4.dp)
                .fillMaxWidth(),
            content = { Text("Start Controller") },
            onClick = { viewModel.getPresentationList() },
        )


        var controllerOptionsDialog by remember { mutableStateOf(false) }
        TitleDivider("Controller Options")
        val pptControlOption = viewModel.pptControl.collectAsState()
        SettingWithDescription(
            title = "Choose presentation control",
            description = pptControlOption.value.description,
            onClick = { controllerOptionsDialog = true },
            Modifier.fillMaxWidth()
        )

        if(controllerOptionsDialog) {
            OptionsPopup(
                title = "Choose presentation control",
                onDismiss = { controllerOptionsDialog = false },
                content = { paddingValues ->
                    LazyColumn {
                        items(PptControlOptions.entries) { option ->
                            HorizontalDivider()
                            Selector(
                                text = option.description,
                                checked = (pptControlOption.value == option),
                                multiSelect = false,
                                onClick = {
                                    controllerOptionsDialog = false
                                    viewModel.changePptsControlled(option)
                                },
                                Modifier.padding(paddingValues)
                            )
                        }
                    }
                }
            )
        }



        val selectedGesture = viewModel.gestureSelected.collectAsState()
        TitleDivider("Gesture Settings")

        val buttonOrientation = viewModel.buttonSetting.collectAsState()
        var buttonOptionsDialog by remember { mutableStateOf(false) }
        SwitchDescription(
            title = GestureOptions.BUTTON.description,
            description = buttonOrientation.value.name,
            onClick = { buttonOptionsDialog = true},
            onCheckChanged = { checked -> viewModel.changeSelectedGesture(GestureOptions.BUTTON,checked) },
            checked = selectedGesture.value == GestureOptions.BUTTON
        )

        if(buttonOptionsDialog) {
            OptionsPopup(
                title = "Choose button layout",
                onDismiss = { buttonOptionsDialog = false },
                content = { paddingValues ->
                    LazyColumn {
                        items(Orientation.entries) { option ->
                            HorizontalDivider()
                            Selector(
                                text = option.name,
                                checked = buttonOrientation.value == option,
                                multiSelect = false,
                                onClick = {
                                    viewModel.changeButtonSetting(option)
                                    buttonOptionsDialog = false
                                },
                                Modifier.padding(paddingValues)
                            )
                        }
                    }
                }
            )
        }

        val swipeDirection = viewModel.swipeDirection.collectAsState()
        var swipeDirectionDialog by remember { mutableStateOf(false) }
        SwitchDescription(
            title = GestureOptions.SWIPE.description,
            description = swipeDirection.value.name,
            onClick = { swipeDirectionDialog = true},
            onCheckChanged = { checked -> viewModel.changeSelectedGesture(GestureOptions.SWIPE,checked) },
            checked = selectedGesture.value == GestureOptions.SWIPE
        )
        if(swipeDirectionDialog) {
            OptionsPopup(
                title = "Choose swipe direction",
                onDismiss = { swipeDirectionDialog = false },
                content = { paddingValues ->
                    LazyColumn {
                        items(Orientation.entries) { option ->
                            HorizontalDivider()
                            Selector(
                                text = option.name,
                                checked = swipeDirection.value == option,
                                multiSelect = false,
                                onClick = {
                                    viewModel.changeSwipeDirection(option)
                                    swipeDirectionDialog = false
                                },
                                Modifier.padding(paddingValues)
                            )
                        }
                    }
                }
            )
        }

        val tapSetting = viewModel.tapSetting.collectAsState()
        var tapSettingDialog by remember { mutableStateOf(false) }
        SwitchDescription(
            title = GestureOptions.TAP.description,
            description = tapSetting.value.description,
            onClick = { tapSettingDialog = true},
            onCheckChanged = { checked -> viewModel.changeSelectedGesture(GestureOptions.TAP,checked) },
            checked = selectedGesture.value == GestureOptions.TAP
        )
        if(tapSettingDialog) {
            OptionsPopup(
                title = "Choose tap settings",
                onDismiss = { tapSettingDialog = false },
                content = { paddingValues ->
                    LazyColumn {
                        items(TapOptions.entries) { option ->
                            HorizontalDivider()
                            Selector(
                                text = option.description,
                                checked = tapSetting.value == option,
                                multiSelect = false,
                                onClick = {
                                    viewModel.changeTapSetting(option)
                                    tapSettingDialog = false
                                },
                                Modifier.padding(paddingValues)
                            )
                        }
                    }
                }
            )
        }

        val showActiveSlideshows = viewModel.showActiveSlideshows.collectAsState()
        val activeSlideshows = viewModel.activeSlideshows.collectAsState()
        val controlledSlideshows = viewModel.controlledSlideshows.collectAsState()
        if(showActiveSlideshows.value){
            OptionsPopup(
                title = "Select Presentation",
                onDismiss = { viewModel.dismissActiveSlideshowDialog()},
                onApprove = {
                    if(!(pptControlOption.value == PptControlOptions.SINGLE && controlledSlideshows.value.size != 1))
                         onNavigateToControlSlideshow()
                    viewModel.dismissActiveSlideshowDialog()
                },
                content = { paddingValues ->
                    LazyColumn {
                        items(activeSlideshows.value) { slideshow ->
                            HorizontalDivider()
                            Selector(
                                text = slideshow.fileName,
                                checked = (controlledSlideshows.value.contains(slideshow)),
                                multiSelect = pptControlOption.value != PptControlOptions.SINGLE,
                                onClick = {
                                    viewModel.updateControlledSlideshows(slideshow)
                                    Log.i("Control slides",controlledSlideshows.value.toString())
                                },
                                Modifier.padding(paddingValues)
                            )
                        }
                    }
                }
            )
        }
    }

    val context = LocalContext.current
    LaunchedEffect(true) {
        viewModel.error.collectLatest {
            if(it.isNotEmpty()) {
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
fun TitleDivider(text: String){
    Spacer(Modifier.padding(top = 12.dp))
    Text(
        text = text,
        fontSize = 14.sp,
    )
    HorizontalDivider()
    Spacer(Modifier.padding(bottom = 4.dp))
}

@Composable
fun SettingWithDescription(
    title:String,
    description:String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier){
    Column(
        verticalArrangement = Arrangement.spacedBy((-4).dp),
        modifier = modifier
            .clickable{ onClick() }
    ){
        Text(
            text = title,
            fontSize = 16.sp
        )

        Text(
            modifier = Modifier
                .padding(start = 4.dp),
            text = description,
            fontSize = 12.sp,
        )
    }
}

@Composable
fun SwitchDescription(
    title:String,
    description: String,
    onClick: () -> Unit,
    onCheckChanged: (Boolean) -> Unit,
    checked: Boolean,
    modifier: Modifier = Modifier)
{
    Row{
        SettingWithDescription(
            title,
            description,
            onClick,
            modifier.weight(1f)
        )

        Switch(
            checked = checked,
            onCheckedChange = {onCheckChanged(it)},
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(horizontal = 12.dp)
        )
    }
}

@Composable
fun Selector(
    text: String,
    checked: Boolean,
    multiSelect: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Row(modifier = modifier
        .fillMaxWidth()
        .selectable(
            selected = checked,
            onClick = { onClick() },
            role = Role.RadioButton)
    ) {
        Text(
            text = text,
            Modifier
                .weight(1f)
                .padding(2.dp)
        )
        if(multiSelect){
            Checkbox(
                checked = checked,
                onCheckedChange = null,
                Modifier.align(Alignment.CenterVertically)
            )
        }
        else {
            RadioButton(
                selected = checked,
                onClick = null,
                Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun OptionsPopup(
    title: String,
    content: @Composable ((paddingValues: PaddingValues) -> Unit),
    onApprove: (() -> Unit)? = null,
    onDismiss: () -> Unit
){
    Dialog(onDismissRequest = { onDismiss() }){
        Card(
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
        ){
            Text(
                text = title,
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(5.dp,16.dp,0.dp,4.dp)
            )
            HorizontalDivider(color = Color.Blue)

            content(PaddingValues(start = 8.dp, end = 12.dp, top = 8.dp, bottom = 8.dp))

            Row {
                Text(
                    text = "Cancel",
                    color = Color.Blue,
                    modifier = Modifier
                        .clickable { onDismiss() }
                        .padding(top = 8.dp, start = 16.dp, bottom = 12.dp)
                )
                Spacer(Modifier.weight(1f))
                if(onApprove != null) {
                    Text(
                        text = "Ok",
                        color = Color.Blue,
                        modifier = Modifier
                            .clickable { onApprove() }
                            .padding(top = 8.dp, end = 16.dp, bottom = 12.dp)
                    )
                }
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun SettingsScreenPreview(){
//    Church_PPT_controllerTheme {
//        SettingsScreen(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(8.dp)
//        )
//    }
//}