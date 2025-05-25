package com.example.church_ppt_controller.ui.screens

import android.util.Log
import androidx.compose.foundation.gestures.Orientation
import androidx.datastore.core.IOException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.church_ppt_controller.models.Presentation
import com.example.church_ppt_controller.models.requests.SlideChangeRequest
import com.example.church_ppt_controller.models.responses.SlideChangeResponse
import com.example.church_ppt_controller.network.ControllerApiService
import com.example.church_ppt_controller.network.RetrofitClient
import com.example.church_ppt_controller.utils.ControllerPreferenceRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

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
    companion object {
        const val TAG = "ControllerViewModel"
        private const val PORT = 8080
    }

    private lateinit var controllerApiService: ControllerApiService
    private val _ipAddress = MutableStateFlow("192.168.1.")
    val ipAddress = _ipAddress.asStateFlow()

    private val _showActiveSlideshows = MutableStateFlow(false)
    val showActiveSlideshows = _showActiveSlideshows.asStateFlow()

    private val _activeSlideshows = MutableStateFlow(emptyList<Presentation>())
    val activeSlideshows = _activeSlideshows.asStateFlow()

    private val _controlledSlideshows = MutableStateFlow(emptyList<Presentation>())
    val controlledSlideshows = _controlledSlideshows.asStateFlow()

    private val _changeSlideshows = MutableStateFlow(emptyList<Presentation>())
    val changeSlideshows = _changeSlideshows.asStateFlow()

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

    private val _buttonSetting = MutableStateFlow(Orientation.Vertical)
    val buttonSetting: StateFlow<Orientation> = _buttonSetting.asStateFlow()

    private val _swipeDirection = MutableStateFlow(Orientation.Vertical)
    val swipeDirection: StateFlow<Orientation> = _swipeDirection.asStateFlow()

    enum class TapOptions(val description: String){
        SINGLE_TAP("Single tap for next, double tap for previous slide"),
        DOUBLE_TAP("Double tap for next, single tap for previous slide"),
    }
    private val _tapSetting = MutableStateFlow(TapOptions.SINGLE_TAP)
    val tapSetting: StateFlow<TapOptions> = _tapSetting

    private  val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    init {
        viewModelScope.launch {
            controllerPreferenceRepository.controllerPreferencesFlow.collect { preferences ->
                _ipAddress.value = preferences.ipAddress
                _pptControl.value = PptControlOptions.valueOf(preferences.pptControlSetting)
                _gestureSelected.value = GestureOptions.valueOf(preferences.gestureSelected)
                _buttonSetting.value = Orientation.valueOf(preferences.buttonSetting)
                _swipeDirection.value = Orientation.valueOf(preferences.swipeSetting)
                _tapSetting.value = TapOptions.valueOf(preferences.tapSetting)
            }
        }
    }

    fun dismissActiveSlideshowDialog(){
        _showActiveSlideshows.value = false
    }

    fun updateControlledSlideshows(presentation: Presentation) {
        val alreadyExists = controlledSlideshows.value.any { it.presentationId == presentation.presentationId }
        _controlledSlideshows.update { currentList ->
            if(alreadyExists) {
                if(pptControl.value != PptControlOptions.SINGLE && controlledSlideshows.value.size > 1)
                    currentList.filter { it.presentationId != presentation.presentationId }
                else
                    currentList
            } else {
                if(pptControl.value == PptControlOptions.SINGLE)
                    listOf(presentation)
                else
                    currentList + presentation
            }
        }

        if(controlledSlideshows.value.size == 1)
            _changeSlideshows.value = controlledSlideshows.value
        else
            _changeSlideshows.value = emptyList()
    }
    
    fun updateChangeSlideshows(presentation: Presentation) {
        val alreadyExists = changeSlideshows.value.any { it.presentationId == presentation.presentationId }
        _changeSlideshows.update { currentList ->
            if(alreadyExists) {
                if(pptControl.value == PptControlOptions.SIMULTANEOUS)
                    currentList.filter { it.presentationId != presentation.presentationId }
                else
                    currentList
            } else {
                if(pptControl.value == PptControlOptions.MULTIPLE)
                    listOf(presentation)
                else
                    currentList + presentation
            }
        }
    }

    fun getPresentationList(){
        _showActiveSlideshows.value = false
        viewModelScope.launch {
            try {
                RetrofitClient.updateBaseUrl("http://${_ipAddress.value}:$PORT")
                controllerApiService = RetrofitClient.getControllerApiService()
                val slideshows = controllerApiService.getPresentations()
                _activeSlideshows.value = slideshows.presentations

                if (_activeSlideshows.value.isEmpty()) {
                    Log.i("Get presentation", "No active presentations")
                    _error.emit("No active presentations.")
                } else {
                    _showActiveSlideshows.value = true
                    if (_activeSlideshows.value.size == 1) {
                        _controlledSlideshows.value = _activeSlideshows.value
                    }
                }
            }
            catch (_ : IllegalArgumentException){
                _error.emit("Invalid URL format, please recheck the address")
            } catch (e : IOException) {
                // TODO create toast, could not connect to server
                Log.e("Get presentations","IOException"+ e.message.toString()) //"Could not connect to server")
                _error.emit("Could not connect to server. Please ensure network and ip address is correct")
            } catch (e : HttpException){
                Log.i("Get presentation", e.code().toString()) //"no active powerpoint instances"
                _error.emit("No PowerPoint instances are active.")
            }
            controllerPreferenceRepository.updateIpAddress(ipAddress.value)
        }
    }

    fun changeSlides(slideChange: Int){
        viewModelScope.launch {
            try {
                val presentationIds: List<String> = changeSlideshows.value.map { it.presentationId }
                val slideChangeRequest = SlideChangeRequest(presentationIds,slideChange)
                val slideChangeResponse: SlideChangeResponse = controllerApiService.changeSlide(slideChangeRequest)

                val newPresentationMap = slideChangeResponse.presentations.map { it.presentationInfo }.associateBy { it.presentationId }
                _controlledSlideshows.value = controlledSlideshows.value.map { it ->
                    newPresentationMap[it.presentationId] ?: it
                }
//                Log.d("Slide change response",(slideChangeResponse).toString())
//                Log.i("Slide change response",(slideChangeResponse.presentations.map { it.presentationInfo }).toString())
            } catch (error: HttpException) {
                //TODO vibrate if change fails
                val errorJsonString = error.response()?.errorBody()?.string()
                val gson = Gson()
                val slideChangeResponse = gson.fromJson(errorJsonString,SlideChangeResponse::class.java)
                Log.i("Change slide 405",slideChangeResponse.toString())
                if(!slideChangeResponse.success){
                    if(slideChangeResponse.presentations[0].statusCode == 404){
                        // go back if single mode
                        _error.emit("Slideshow was ended.")
                    } else {
                        _error.emit("Slide does not exist")
                    }
                }
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
        if(option == PptControlOptions.SINGLE && controlledSlideshows.value.size > 1)
            _controlledSlideshows.value = emptyList()
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

    fun changeButtonSetting(option: Orientation) {
        viewModelScope.launch {
            controllerPreferenceRepository.updateButtonSetting(option)
        }
    }
    fun changeSwipeDirection(option: Orientation) {
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