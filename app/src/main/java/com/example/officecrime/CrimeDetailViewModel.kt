package com.example.officecrime

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.io.File
import java.util.UUID

class CrimeDetailViewModel(): ViewModel() {
    private val crimeRepository = CrimeRepository.get()
    private val crimeIdLiveData = MutableLiveData<UUID>()

    var crimeLiveData: LiveData<Crime?> =
        Transformations.switchMap(crimeIdLiveData){crimeId ->
            crimeRepository.getCrime(crimeId)
        } // данные в том виде, в котором они храняться в БД

    fun loadCrime(crimeId:UUID){
        crimeIdLiveData.value = crimeId
    }

    fun saveCrime(crime:Crime){
        crimeRepository.updateCrime(crime)
    } // сохранение ввода данных в БД

    fun getPhotoFile(crime: Crime):File{
        return crimeRepository.getPhotoFile(crime)
    }
}