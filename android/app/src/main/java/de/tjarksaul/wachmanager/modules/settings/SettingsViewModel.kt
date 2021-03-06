package de.tjarksaul.wachmanager.modules.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.tjarksaul.wachmanager.dtos.Station

class SettingsViewModel : ViewModel() {
    fun updateData(data: MutableList<Station>) {
        _stations.value = data
    }

    private val _stations = MutableLiveData<MutableList<Station>>().apply {
        value = emptyList<Station>().toMutableList()
    }
    val stations: LiveData<MutableList<Station>> = _stations
}