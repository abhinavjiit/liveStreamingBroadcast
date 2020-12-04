package com.livestreaming.channelize.io.activity.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class LoginUserViewModelFact(private val iLoginRepository: ILoginRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginUserViewModel(iLoginRepository = iLoginRepository) as T
    }

}