package com.livestreaming.channelize.io.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.livestreaming.channelize.io.LiveBroadcasterConstants
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.SharedPrefUtils
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.DELAY
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.LSCBroadCastSettingUpAndLiveActivity
import kotlinx.android.synthetic.main.fragment_setting_up_video.*
import kotlinx.android.synthetic.main.fragment_setting_up_video.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LSCSettingUpLiveStreamingFragment : Fragment() {

    private var instructionsChecked = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting_up_video, container, false)
        showConnectionCheckingLoader()
        view.btnContinue.setOnClickListener {
            clInstructionsContainer.visibility = View.GONE
            clGoLiveContainer.visibility = View.VISIBLE
        }
        view.btnGoLiveButton.setOnClickListener {
            activity?.let { context ->
                (context as LSCBroadCastSettingUpAndLiveActivity).joinChannel(
                    this
                )
            }
        }
        view.tvCancel.setOnClickListener {
            activity?.finish()
        }
        view.tvNotShowAgain.setOnClickListener {
            activity?.let { context ->
                if (!instructionsChecked) {
                    val selectedCheckBox = ContextCompat.getDrawable(context, R.drawable.ic_check_box)
                    view.tvNotShowAgain.setCompoundDrawablesWithIntrinsicBounds(
                        selectedCheckBox,
                        null,
                        null,
                        null
                    )
                    SharedPrefUtils.showInstructions(context, true)
                    instructionsChecked = true
                } else {
                    val selectedCheckBox = ContextCompat.getDrawable(context, R.drawable.ic_uncheck_box)
                    view.tvNotShowAgain.setCompoundDrawablesWithIntrinsicBounds(
                        selectedCheckBox,
                        null,
                        null,
                        null
                    )
                    SharedPrefUtils.showInstructions(context, false)
                    instructionsChecked = false
                }
            }
        }
        return view
    }

    private fun showConnectionCheckingLoader() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(DELAY)
            clCheckingConnectionContainer.visibility = View.GONE
            showNetworkResult()
        }
    }

    private suspend fun showNetworkResult() {
        try {
            activity?.let { activity ->
                when ((activity as LSCBroadCastSettingUpAndLiveActivity).getNetworkQuality()) {
                    LiveBroadcasterConstants.EXCELLENT_NETWORK_QUALITY -> {
                        tvNetworkQuality.text = context?.resources?.getString(R.string.excellent_connection_string)
                        delay(DELAY)
                        if (SharedPrefUtils.showInstructions(activity)) {
                            clCheckingConnectionContainer.visibility = View.GONE
                            tvCancel.visibility = View.GONE
                            clGoLiveContainer.visibility = View.VISIBLE
                        } else {
                            showInstructionsList()
                        }
                    }
                    LiveBroadcasterConstants.VERY_GOOD_NETWORK_QUALITY -> {
                        tvNetworkQuality.text = context?.resources?.getString(R.string.very_good_connection_string)
                        delay(DELAY)
                        if (SharedPrefUtils.showInstructions(activity)) {
                            clConnectionResultContainer.visibility = View.GONE
                            tvCancel.visibility = View.GONE
                            clGoLiveContainer.visibility = View.VISIBLE
                        } else {
                            showInstructionsList()
                        }
                    }
                    LiveBroadcasterConstants.GOOD_NETWORK_QUALITY -> {
                        tvNetworkQuality.text = context?.resources?.getString(R.string.good_connection_string)
                        delay(DELAY)
                        if (SharedPrefUtils.showInstructions(activity)) {
                            clConnectionResultContainer.visibility = View.GONE
                            tvCancel.visibility = View.GONE
                            clGoLiveContainer.visibility = View.VISIBLE
                        } else {
                            showInstructionsList()
                        }
                    }
                    else -> {
                        tvNetworkQuality.text = context?.resources?.getString(R.string.poor_connection_string)
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("NetworkQualityEx", e.toString())
        }
    }

    private fun showInstructionsList() {
        clConnectionResultContainer.visibility = View.GONE
        tvCancel.visibility = View.GONE
        clInstructionsContainer.visibility = View.VISIBLE
    }

}
