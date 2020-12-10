
package com.livestreaming.channelize.io.activity.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.channelize.apisdk.network.response.LoginResponse

class LoginUserViewModel(private val iLoginRepositoryCallBack: ILoginRepositoryCallBack) : ViewModel() {

    var mutableLoginResponseLiveData = MutableLiveData<LoginResponse>()

    fun onUserLogin(email: String, password: String): LiveData<LoginResponse> {
        mutableLoginResponseLiveData = iLoginRepositoryCallBack.onUserLogin(email, password)
        return mutableLoginResponseLiveData
    }

}