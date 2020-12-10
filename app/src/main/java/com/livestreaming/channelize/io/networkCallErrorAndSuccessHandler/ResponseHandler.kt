
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
        return try {
            val error = (e as HttpException).response()?.errorBody()
            error?.bytes()?.let { byteArray ->
                val errorRes = String(byteArray)
                val jsonObject = JSONObject(errorRes)
                val errorJson = jsonObject.getJSONObject("error")
                val errorMsg = errorJson.getString("message")
                Resource.error(errorMsg, null)
            } ?: kotlin.run {
                Resource.error("Something went wrong", null)
            }
        } catch (e: Exception) {
            Resource.error(null, null)
        }
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