package com.livestreaming.channelize.io.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.SharedPrefUtils
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.LSCBroadCastSettingUpAndLiveActivity
import kotlinx.android.synthetic.main.fragment_setting_up_video.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LSCSettingUpFragment : BaseFragment() {

    private var instructionsChecked = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting_up_video, container, false)
        showConnectionCheckingLoader()
        continueButton.setOnClickListener {
            instructionsContainer.visibility = View.GONE
            goLiveContainer.visibility = View.VISIBLE
        }
        goLiveButton.setOnClickListener {
            activity?.let { context ->
                (context as LSCBroadCastSettingUpAndLiveActivity).joinChannel(
                    this
                )
            }
        }
        cancelTextView.setOnClickListener {
            activity?.finish()
        }
        dontShowAgainTextView.setOnClickListener {
            activity?.let { context ->
                if (!instructionsChecked) {
                    val selectedCheckBox =
                        ContextCompat.getDrawable(context, R.drawable.ic_check_box)
                    dontShowAgainTextView.setCompoundDrawablesWithIntrinsicBounds(
                        selectedCheckBox,
                        null,
                        null,
                        null
                    )
                    SharedPrefUtils.setInstructionsShownFlag(context, true)
                    instructionsChecked = true
                } else {
                    val selectedCheckBox =
                        ContextCompat.getDrawable(context, R.drawable.ic_uncheck_box)
                    dontShowAgainTextView.setCompoundDrawablesWithIntrinsicBounds(
                        selectedCheckBox,
                        null,
                        null,
                        null
                    )
                    SharedPrefUtils.setInstructionsShownFlag(context, false)
                    instructionsChecked = false
                }
            }
        }
        return view
    }

    private fun showConnectionCheckingLoader() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            checkingConnectionContainer.visibility = View.GONE
            showNetworkResult()
        }
    }

    private suspend fun showNetworkResult() {
        try {
            activity.let {activity->
                connectionResultContainer.visibility = View.VISIBLE
                when ((activity as LSCBroadCastSettingUpAndLiveActivity).getNetworkQuality()) {
                    0 -> {
                        netWorkQuality.text =
                            context?.resources?.getString(R.string.excellent_connection_string)
                        delay(2000)
                        if (SharedPrefUtils.getInstructionsShownFlag(activity)) {
                            connectionResultContainer.visibility = View.GONE
                            cancelTextView.visibility = View.GONE
                            goLiveContainer.visibility = View.VISIBLE
                        } else {
                            showInstructionsList()
                        }
                    }
                    1 -> {
                        netWorkQuality.text =
                            context?.resources?.getString(R.string.very_good_connection_string)
                        delay(2000)

                        if (SharedPrefUtils.getInstructionsShownFlag(activity)) {
                            connectionResultContainer.visibility = View.GONE
                            cancelTextView.visibility = View.GONE
                            goLiveContainer.visibility = View.VISIBLE
                        } else {
                            showInstructionsList()
                        }
                    }
                    2 -> {
                        netWorkQuality.text =
                            context?.resources?.getString(R.string.good_connection_string)
                        delay(2000)
                        if (SharedPrefUtils.getInstructionsShownFlag(activity)) {
                            connectionResultContainer.visibility = View.GONE
                            cancelTextView.visibility = View.GONE
                            goLiveContainer.visibility = View.VISIBLE
                        } else {
                            showInstructionsList()
                        }
                    }
                    else -> {
                        netWorkQuality.text =
                            context?.resources?.getString(R.string.poor_connection_string)
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("NetworkQualityEx", e.toString())
        }
    }

    private fun showInstructionsList() {
        connectionResultContainer.visibility = View.GONE
        cancelTextView.visibility = View.GONE
        instructionsContainer.visibility = View.VISIBLE
    }

}