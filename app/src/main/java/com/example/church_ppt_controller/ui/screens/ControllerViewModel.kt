package com.example.church_ppt_controller.ui.screens

import android.util.Log
import androidx.datastore.core.IOException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.church_ppt_controller.models.Presentation
import com.example.church_ppt_controller.models.Requests.SlideChangeRequest
import com.example.church_ppt_controller.models.Responses.SlideChangeResponse
import com.example.church_ppt_controller.network.RetrofitClient
import com.example.church_ppt_controller.utils.ControllerPreferenceRepository
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Converter
import retrofit2.HttpException
import retrofit2.converter.gson.GsonConverterFactory

class ControllerViewModelFactory(
    private val repository: ControllerPreferenceRepository
): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ControllerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ControllerViewModel(repository) as T
        }
        throw  IllegalArgumentException("Unknown ViewModel class")
    }
}


class ControllerViewModel(
    private val controllerPreferenceRepository: ControllerPreferenceRepository
) : ViewModel() {
    private val PORT_NUMBER = 5000
    private val _ipAddress = MutableStateFlow("192.168.1.")
    val ipAddress = _ipAddress.asStateFlow()

    private val _showActiveSlideshows = MutableStateFlow(false)
    val showActiveSlideshows = _showActiveSlideshows.asStateFlow()

    private val _activeSlideshows = MutableStateFlow(emptyList<Presentation>())
    val activeSlideshows = _activeSlideshows.asStateFlow()

    private val _controlledSlideshows = MutableStateFlow(emptyList<Presentation>())
    val controlledSlideshows = _controlledSlideshows.asStateFlow()

    enum class PptControlOptions(val description: String) {
        SINGLE("Single Presentation"),
        MULTIPLE("Multiple Presentations"),
        SIMULTANEOUS("Multiple presentations simultaneously")
    }
    private val _pptControl = MutableStateFlow(PptControlOptions.SINGLE)
    val pptControl: StateFlow<PptControlOptions> = _pptControl.asStateFlow()

    enum class GestureOptions(val description: String){
        BUTTON("Press button to change slide"),
        SWIPE("Swipe to change slide"),
        TAP("Tap screen to change slide")
    }
    private val _gestureSelected = MutableStateFlow(GestureOptions.BUTTON)
    val gestureSelected: StateFlow<GestureOptions> = _gestureSelected.asStateFlow()

    enum class Orientations(val description: String){
        VERTICAL("Vertical"),
        HORIZONTAL("Horizontal")
    }

    private val _buttonSetting = MutableStateFlow(Orientations.VERTICAL)
    val buttonSetting: StateFlow<Orientations> = _buttonSetting.asStateFlow()

    private val _swipeDirection = MutableStateFlow(Orientations.VERTICAL)
    val swipeDirection: StateFlow<Orientations> = _swipeDirection.asStateFlow()

    enum class TapOptions(val description: String){
        SINGLE_TAP("Single tap for next, double tap for previous slide"),
        DOUBLE_TAP("Double tap for next, single tap for previous slide"),
    }
    private val _tapSetting = MutableStateFlow(TapOptions.SINGLE_TAP)
    val tapSetting: StateFlow<TapOptions> = _tapSetting


    // cannot use mutable state flow need to use something different
    private  val _error = MutableStateFlow("")
    val error = _error.asStateFlow()

    init {
        viewModelScope.launch {
            controllerPreferenceRepository.controllerPreferencesFlow.collect { preferences ->
                _ipAddress.value = preferences.ipAddress
                _pptControl.value = PptControlOptions.valueOf(preferences.pptControlSetting)
                _gestureSelected.value = GestureOptions.valueOf(preferences.gestureSelected)
                _buttonSetting.value = Orientations.valueOf(preferences.buttonSetting)
                _swipeDirection.value = Orientations.valueOf(preferences.swipeSetting)
                _tapSetting.value = TapOptions.valueOf(preferences.tapSetting)
            }
        }
    }

    fun dismissActiveSlideshowDialog(){
        _showActiveSlideshows.value = false
    }

    fun updateControlledSlideshows(presentation: Presentation) {
        _controlledSlideshows.update { currentList ->
            if(_controlledSlideshows.value.contains(presentation) && pptControl.value!=PptControlOptions.SINGLE){
                if(_controlledSlideshows.value.size != 1)
                    currentList - presentation
                else
                    currentList
            } else {
                if(pptControl.value != PptControlOptions.SINGLE)
                    currentList + presentation
                else
                    listOf(presentation)
            }
        }
    }

    fun getPresentationList(){
        _showActiveSlideshows.value = false
        try {
            RetrofitClient.updateBaseUrl("http://${_ipAddress.value}:$PORT_NUMBER")
        } catch (e : IllegalArgumentException){
            _error.value = "Invalid URL format, please recheck the address"
        }
        viewModelScope.launch {
            try {
                val slideshows = RetrofitClient.getControllerApiService().getPresentations()
                _activeSlideshows.value = slideshows.presentations

                if(_activeSlideshows.value.isEmpty()){
                    Log.i("Get presentation", "No active presentations")
                    _error.value = "No active presentations."
                } else {
                    _showActiveSlideshows.value = true
                    if(_activeSlideshows.value.size == 1){
                        _controlledSlideshows.value = _activeSlideshows.value
                    }
                }
            } catch (e : IOException) {
                // TODO create toast, could not connect to server
                Log.e("Get presentations","IOException"+ e.message.toString()) //"Could not connect to server")
                _error.value = "Could not connect to server. Please ensure network and ip address is correct"
            } catch (e : HttpException){
                Log.i("Get presentation", e.code().toString()) //"no active powerpoint instances"
                _error.value = "No PowerPoint instances are active."
            }
            controllerPreferenceRepository.updateIpAddress(ipAddress.value)
        }
    }

    fun changeSlides(slideChange: Int){
        val presentationIds: List<String> = controlledSlideshows.value.map { it.presentationId }
        val slideChangeRequest = SlideChangeRequest(presentationIds,slideChange)
        viewModelScope.launch {
            try {
                val slideChangeResponse = RetrofitClient.getControllerApiService().changeSlide(slideChangeRequest)
            } catch (error: HttpException) {
                //TODO vibrate if change fails
                val errorJsonString = error.response()?.errorBody()?.string()
                val gson = Gson()
                val slideChangeResponse = gson.fromJson(errorJsonString,SlideChangeResponse::class.java)
                Log.i("Change slide 405",slideChangeResponse.toString())
                //if single presentation mode then close the controller if 405 (no presentation)
            } catch (e: Exception){
                Log.e("Change slide",e.message.toString())
            }
        }
    }


    fun updateIpAddress(ip: String){
        _ipAddress.value = ip
    }

    fun changePptsControlled(option:PptControlOptions){
        viewModelScope.launch {
            controllerPreferenceRepository.updatePptControl(option)
        }
    }

    fun changeSelectedGesture(option: GestureOptions,checked: Boolean){
        viewModelScope.launch {
            if(checked)
                controllerPreferenceRepository.updateGesture(option)
            else
                controllerPreferenceRepository.updateGesture(GestureOptions.BUTTON)
        }
    }

    fun changeButtonSetting(option: Orientations) {
        viewModelScope.launch {
            controllerPreferenceRepository.updateButtonSetting(option)
        }
    }
    fun changeSwipeDirection(option: Orientations) {
        viewModelScope.launch {
            controllerPreferenceRepository.updateSwipeSetting(option)
        }
    }
    fun changeTapSetting(option: TapOptions) {
        viewModelScope.launch {
            controllerPreferenceRepository.updateTapSetting(option)
        }
    }
}