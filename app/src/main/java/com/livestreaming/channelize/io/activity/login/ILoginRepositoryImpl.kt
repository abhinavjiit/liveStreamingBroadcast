package com.livestreaming.channelize.io.activity.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.channelize.apisdk.Channelize
import com.channelize.apisdk.network.response.LoginResponse

class ILoginRepositoryImpl : ILoginRepositoryCallBack {

    private var logInResponse = MutableLiveData<LoginResponse>()

    override fun onUserLogin(email: String, password: String): MutableLiveData<LoginResponse> {
        try {
            Channelize.getInstance().loginWithEmailPassword(
                email, password
            )
            { result, _ ->
                if (result != null && result.user != null) {
                    logInResponse.postValue(result)
                } else {
                    logInResponse.postValue(null)
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