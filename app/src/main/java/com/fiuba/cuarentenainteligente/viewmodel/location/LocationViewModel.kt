package com.fiuba.cuarentenainteligente.viewmodel.location

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.fiuba.cuarentenainteligente.livedata.location.LocationLiveData

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val locationData = LocationLiveData(application)

    fun getLocationData() = locationData
}