package com.example.attractionsapp.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.attractionsapp.data.App

class MainViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val photoDao = (context.applicationContext as App).db.photoDao()
        return MainViewModel(photoDao) as T
    }
}