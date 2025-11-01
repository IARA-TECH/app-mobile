//package com.mobile.app_iara.ui.abacus.register
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.mobile.app_iara.data.repository.AbacusRepository
//import kotlinx.coroutines.launch
//
//class RegisterAbacusViewModel (private val repository: AbacusRepository) : ViewModel() {
//
//    fun registerAbacus(
//        abacus: Abacus,
//        onSuccess: (Abacus) -> Unit,
//        onFailure: (Throwable) -> Unit
//    ) {
//        viewModelScope.launch {
//            val result = repository.registerAbacus(abacus)
//
//            result.onSuccess { registeredAbacus ->
//                onSuccess(registeredAbacus)
//            }.onFailure { error ->
//                onFailure(error)
//            }
//        }
//    }
//}