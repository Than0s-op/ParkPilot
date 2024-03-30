package com.application.parkpilot.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.parkpilot.module.firebase.database.StationLocation as FS_StationLocation
import com.application.parkpilot.StationLocation as DS_StationLocation
import kotlinx.coroutines.launch

class SpotListViewModel: ViewModel() {
    val liveDataStationLocation = MutableLiveData<ArrayList<DS_StationLocation>?>()
    fun loadStation(){
        viewModelScope.launch{
            liveDataStationLocation.value = FS_StationLocation().locationGet()
        }
    }
}