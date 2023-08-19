package si.uni_lj.fe.tnuv.oleae.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import si.uni_lj.fe.tnuv.oleae.data.ClassificationReceiveManager
import si.uni_lj.fe.tnuv.oleae.data.ConnectionState
import si.uni_lj.fe.tnuv.oleae.util.Resource
import javax.inject.Inject

@HiltViewModel
class ClassificationViewModel @Inject constructor(
    private val classificationReceiveManager: ClassificationReceiveManager
) : ViewModel(){

    var initializingMessage by mutableStateOf<String?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var classification by mutableStateOf<String?>("Nothing Detected")
        private set


    var connectionState by mutableStateOf<ConnectionState>(ConnectionState.Uninitialized)


    private fun subscribeToChanges(){
        viewModelScope.launch {
            classificationReceiveManager.data.collect{ result ->
                when(result){
                    is Resource.Success -> {
                        connectionState = result.data.connectionState
                        classification = result.data.classification
                    }

                    is Resource.Loading -> {
                        initializingMessage = result.message
                        connectionState = ConnectionState.CurrentlyInitializing
                    }

                    is Resource.Error -> {
                        errorMessage = result.errorMessage
                        connectionState = ConnectionState.Uninitialized
                    }
                }
            }
        }
    }

    fun disconnect(){
        classificationReceiveManager.disconnect()
    }

    fun reconnect(){
        classificationReceiveManager.reconnect()
    }

    fun initializeConnection(){
        errorMessage = null
        subscribeToChanges()
        classificationReceiveManager.startReceiving()
    }

    override fun onCleared() {
        super.onCleared()
        classificationReceiveManager.closeConnection()
    }


}