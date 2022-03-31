package com.dugsiile.dugsiile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dugsiile.dugsiile.data.DataStoreRepository
import com.dugsiile.dugsiile.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
    private val dataStoreRepository: DataStoreRepository
) : AndroidViewModel(application) {

    fun signout() =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.signout()
        }
}