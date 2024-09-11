package com.appliances.recycle.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.appliances.recycle.repository.RegisterRepository
import com.appliances.recycle.viewModel.RegisterViewModel

class RegisterViewModelFactory (private val registerRepository: RegisterRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(registerRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}