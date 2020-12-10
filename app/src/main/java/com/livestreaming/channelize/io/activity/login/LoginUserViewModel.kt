package com.livestreaming.channelize.io.activity.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.channelize.apisdk.network.response.LoginResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource

class LoginUserViewModel(private val iLoginRepositoryCallBack: ILoginRepositoryCallBack) :
    ViewModel() {

    var mutableLoginResponseLiveData = MutableLiveData<Resource<LoginResponse>>()

    fun onUserLogin(email: String, password: String): LiveData<Resource<LoginResponse>> {
        mutableLoginResponseLiveData.value = Resource.loading(null)
        mutableLoginResponseLiveData = iLoginRepositoryCallBack.onUserLogin(email, password)
        return mutableLoginResponseLiveData
    }

}