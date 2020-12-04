package com.livestreaming.channelize.io.activity.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.channelize.apisdk.network.response.LoginResponse

class LoginUserViewModel(private val iLoginRepository: ILoginRepository) : ViewModel() {

    var mutableLoginResponseLiveData = MutableLiveData<LoginResponse>()
    fun onUserLogin(email: String, password: String): LiveData<LoginResponse> {
        mutableLoginResponseLiveData = iLoginRepository.onUserLogin(email, password)
        return mutableLoginResponseLiveData
    }

}