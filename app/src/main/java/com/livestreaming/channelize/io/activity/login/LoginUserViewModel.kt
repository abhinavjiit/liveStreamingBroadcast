package com.livestreaming.channelize.io.activity.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.channelize.apisdk.network.response.LoginResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource

class LoginUserViewModel(private val userLoginRepository: UserLoginRepository) :
    ViewModel() {

    var mutableLoginResponseLiveData: MutableLiveData<Resource<LoginResponse>>? = null

    fun onUserLogin(email: String, password: String): LiveData<Resource<LoginResponse>>? {
        mutableLoginResponseLiveData = userLoginRepository.onUserLogin(email, password)
        return mutableLoginResponseLiveData
    }

}