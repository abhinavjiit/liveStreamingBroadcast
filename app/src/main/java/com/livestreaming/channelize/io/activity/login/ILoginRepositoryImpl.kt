package com.livestreaming.channelize.io.activity.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.channelize.apisdk.Channelize
import com.channelize.apisdk.network.response.LoginResponse

class ILoginRepositoryImpl : ILoginRepositoryCallBack {

    private var logInSuccess = MutableLiveData<LoginResponse>()

    override fun onUserLogin(email: String, password: String): MutableLiveData<LoginResponse> {
        try {
            Channelize.getInstance().loginWithEmailPassword(
                email, password
            )
            { result, _ ->
                if (result != null && result.user != null) {
                    logInSuccess.postValue(result)
                } else {
                    logInSuccess.postValue(null)
                }
            }
        } catch (e: Exception) {
            logInSuccess.postValue(null)
            Log.d("LogInEx", e.message.toString())
        } catch (t: Throwable) {
            logInSuccess.postValue(null)
            Log.d("LoginThrowable", t.message.toString())
        }
        return logInSuccess
    }

}