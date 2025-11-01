//package com.mobile.app_iara.ui.abacus.register
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.mobile.app_iara.data.repository.AbacusRepository
//
//class RegisterAbacusViewModelFactory(
//    private val repository: AbacusRepository
//) : ViewModelProvider.Factory {
//
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(RegisterAbacusViewModel::class.java)) {
//            return RegisterAbacusViewModel(repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}