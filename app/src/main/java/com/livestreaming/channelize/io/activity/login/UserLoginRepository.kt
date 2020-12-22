package com.livestreaming.channelize.io.activity.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.channelize.apisdk.Channelize
import com.channelize.apisdk.network.response.LoginResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import javax.inject.Inject

class UserLoginRepository @Inject constructor() {

    fun onUserLogin(email: String, password: String): MutableLiveData<Resource<LoginResponse>> {
        val logInResponse = MutableLiveData<Resource<LoginResponse>>()
        try {
            Channelize.getInstance().loginWithEmailPassword(
                email, password
            )
            { result, error ->
                if (result != null && result.user != null) {
                    logInResponse.postValue(Resource.success(result))
                } else {
                    logInResponse.postValue(Resource.error(error.message, null))
                }
            }
        } catch (e: Exception) {
            Log.d("LogInEx", e.message.toString())
        } catch (t: Throwable) {
            Log.d("LoginThrowable", t.message.toString())
        }
        return logInResponse
    }

}