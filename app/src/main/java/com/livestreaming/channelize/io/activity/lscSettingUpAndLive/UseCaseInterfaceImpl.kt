package com.livestreaming.channelize.io.activity.lscSettingUpAndLive

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.text.SimpleDateFormat
import java.util.*

class UseCaseInterfaceImpl() : UseCaseInterface {
    private val counterTime: PublishSubject<String> by lazy {
        PublishSubject.create()
    }

    private val counterTimeLiveValue = MutableLiveData<String>()

    override fun getTotalTime(endTime: String?): String {
        val diff = lscRemainingTime(endTime)
        return totalTime(diff)
    }

    override fun getCountdownTime(endTime: String?): LiveData<String> {
        val diff = lscRemainingTime(endTime)
        return counterTime(diff)
    }


    @SuppressLint("SimpleDateFormat")
    private fun lscRemainingTime(endTime: String?): Long? {
        endTime?.let {
            val outputFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            outputFormat.timeZone = TimeZone.getTimeZone("IST")
            val endTimeFormat = outputFormat.parse(it)
            val currentTime = Calendar.getInstance().time
            return endTimeFormat?.time?.minus(currentTime.time)!!
//            val differenceInMinutes = ((differenceInTime
//                    / (1000 * 60))
//                    % 60)
//
//            val differenceInHours = ((differenceInTime
//                    / (1000 * 60 * 60))
//                    % 24)

            //   totalTimeLeft.text = "${differenceInHours}h ${differenceInMinutes}m"
//            countDownTimer = object : CountDownTimer(differenceInTime, 1000) {
//                override fun onFinish() {
//                    Log.d("Tag", "aaaa")
//                }
//
//                override fun onTick(millisUntilFinished: Long) {
//                    var remainingTime = differenceInTime.minus(millisUntilFinished)
//
//                    val differenceInMinutes1 = ((remainingTime
//                            / (1000 * 60))
//                            % 60)
//
//                    val differenceInHours1 = ((remainingTime
//                            / (1000 * 60 * 60))
//                            % 24)
//                    //  remainingTimeCounter.text = "${differenceInMinutes1}m /"
//                    Log.d("RESULT", "see")
//                }
//
//            }.start()
        } ?: run {
            return null
        }

    }

    private fun totalTime(diff: Long?): String {
        val differenceInMinutes = ((diff?.div((1000 * 60)))?.rem(60))

        val differenceInHours = ((diff?.div((1000 * 60 * 60)))?.rem(24))

        return "${differenceInHours}h ${differenceInMinutes}m"
    }


    private fun counterTime(diff: Long?): LiveData<String> {
        diff?.let {
            val countDownTimer = object : CountDownTimer(it, 1000) {
                override fun onFinish() {
                    Log.d("Tag", "aaaa")
                }

                override fun onTick(millisUntilFinished: Long) {
                    var remainingTime = it.minus(millisUntilFinished)

                    val differenceInMinutes1 = ((remainingTime
                            / (1000 * 60))
                            % 60)

                    val differenceInHours1 = ((remainingTime
                            / (1000 * 60 * 60))
                            % 24)

                    counterTime.onNext(differenceInMinutes1.toString())
                    //  remainingTimeCounter.text = "${differenceInMinutes1}m /"
                    Log.d("RESULT", "see")
                }

            }.start()
        }

        counterTime.subscribe(object : Observer<String> {
                override fun onNext(t: String?) {
                    counterTimeLiveValue.value = t
                }
                override fun onSubscribe(d: Disposable?) {
                }

                override fun onError(e: Throwable?) {
                }

                override fun onComplete() {
                }
            })

        return counterTimeLiveValue
    }
}