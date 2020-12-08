package com.livestreaming.channelize.io.activity.login

import androidx.lifecycle.MutableLiveData
import com.channelize.apisdk.network.response.LoginResponse

interface ILoginRepositoryCallBack {

    fun onUserLogin(email: String, password: String): MutableLiveData<LoginResponse>

}