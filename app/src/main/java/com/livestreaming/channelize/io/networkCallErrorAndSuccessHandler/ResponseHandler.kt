package com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler

import org.json.JSONObject
import retrofit2.HttpException
import java.net.SocketTimeoutException

open class ResponseHandler {
    fun <T : Any?> handleSuccess(data: T): Resource<T> {
        return Resource.success(data)
    }

    fun <T : Any> handleException(e: Exception): Resource<T> {
        return when (e) {
            is HttpException -> Resource.error(getErrorMessage(e.code()), null)
            is SocketTimeoutException -> Resource.error(getErrorMessage(-1), null)
            else -> Resource.error(getErrorMessage(Int.MAX_VALUE), null)
        }
    }

    fun <T : Any> handleThrowable(e: Throwable): Resource<T> {
        val error = (e as HttpException).response()?.errorBody()
        val errorRes = String(error?.bytes()!!)
        val jsonObject = JSONObject(errorRes)
        val errorJson = jsonObject.getJSONObject("error")
        val errorMsg = errorJson.getString("message")
        return Resource.error(errorMsg, null)
    }


    private fun getErrorMessage(code: Int): String {
        return when (code) {
            -1 -> "Timeout"
            401 -> "Unauthorised"
            404 -> "Not found"
            500 -> "Internal server error"
            else -> "Something went wrong"
        }
    }
}