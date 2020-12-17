package com.livestreaming.channelize.io.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.SharedPrefUtils
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.LSCBroadCastSettingUpAndLiveActivity
import kotlinx.android.synthetic.main.fragment_setting_up_video.*
import kotlinx.android.synthetic.main.fragment_setting_up_video.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LSCSettingUpLiveStreamingFragment : BaseFragment() {

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
                    val selectedCheckBox =
                        ContextCompat.getDrawable(context, R.drawable.ic_check_box)
                    view.tvNotShowAgain.setCompoundDrawablesWithIntrinsicBounds(
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
                    view.tvNotShowAgain.setCompoundDrawablesWithIntrinsicBounds(
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
            clCheckingConnectionContainer.visibility = View.GONE
            showNetworkResult()
        }
    }

    private suspend fun showNetworkResult() {
        try {
            activity?.let { activity ->
                clCheckingConnectionContainer.visibility = View.VISIBLE
                when ((activity as LSCBroadCastSettingUpAndLiveActivity).getNetworkQuality()) {
                    0 -> {
                        tvNetworkQuality.text =
                            context?.resources?.getString(R.string.excellent_connection_string)
                        delay(2000)
                        if (SharedPrefUtils.getInstructionsShownFlag(activity)) {
                            clCheckingConnectionContainer.visibility = View.GONE
                            tvCancel.visibility = View.GONE
                            clGoLiveContainer.visibility = View.VISIBLE
                        } else {
                            showInstructionsList()
                        }
                    }
                    1 -> {
                        tvNetworkQuality.text =
                            context?.resources?.getString(R.string.very_good_connection_string)
                        delay(2000)

                        if (SharedPrefUtils.getInstructionsShownFlag(activity)) {
                            clConnectionResultContainer.visibility = View.GONE
                            tvCancel.visibility = View.GONE
                            clGoLiveContainer.visibility = View.VISIBLE
                        } else {
                            showInstructionsList()
                        }
                    }
                    2 -> {
                        tvNetworkQuality.text =
                            context?.resources?.getString(R.string.good_connection_string)
                        delay(2000)
                        if (SharedPrefUtils.getInstructionsShownFlag(activity)) {
                            clConnectionResultContainer.visibility = View.GONE
                            tvCancel.visibility = View.GONE
                            clGoLiveContainer.visibility = View.VISIBLE
                        } else {
                            showInstructionsList()
                        }
                    }
                    else -> {
                        tvNetworkQuality.text =
                            context?.resources?.getString(R.string.poor_connection_string)
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