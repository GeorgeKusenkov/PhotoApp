package com.example.attractionsapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attractionsapp.data.PhotoDao
import com.example.attractionsapp.entity.NewPhoto
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val photoDao: PhotoDao) : ViewModel() {
    val allPhotos = this.photoDao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    fun onAddButton(date: String, url: String) {
        viewModelScope.launch {
            photoDao.insert(
                NewPhoto(
                    date = date,
                    url = url
                )
            )
        }
    }
}