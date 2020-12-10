
package com.livestreaming.channelize.io.activity.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class LoginUserViewModelFact(private val iLoginRepositoryCallBack: ILoginRepositoryCallBack) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginUserViewModel(iLoginRepositoryCallBack) as T
    }

}