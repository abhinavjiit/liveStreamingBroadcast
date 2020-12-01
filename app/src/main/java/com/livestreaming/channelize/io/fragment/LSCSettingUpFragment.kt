package com.livestreaming.channelize.io.fragment

import android.os.Bundle
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LSCSettingUpFragment : BaseFragment(), View.OnClickListener {

    private lateinit var checkingConnectionContainer: ConstraintLayout
    private lateinit var checkingConnectionTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var connectionResultContainer: ConstraintLayout
    private lateinit var connectionResultTextView: TextView
    private lateinit var netWorkQuality: TextView
    private lateinit var instructions: TextView
    private lateinit var instructionsList: LinearLayout
    private lateinit var dontShowAgainTextView: TextView
    private lateinit var continueButton: Button
    private lateinit var cancelTextView: TextView
    private lateinit var instructionsContainer: ConstraintLayout
    private lateinit var goLiveContainer: ConstraintLayout
    private lateinit var goLiveButton: Button
    private var instructionsChecked = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting_up_video, container, false)
        checkingConnectionContainer = view.findViewById(R.id.checkingConnectionContainer)
        checkingConnectionTextView = view.findViewById(R.id.checkingConnectionTextView)
        progressBar = view.findViewById(R.id.progressBar)
        connectionResultTextView = view.findViewById(R.id.connectionResultTextView)
        connectionResultContainer = view.findViewById(R.id.connectionResultContainer)
        netWorkQuality = view.findViewById(R.id.netWorkQuality)
        instructions = view.findViewById(R.id.instructions)
        instructionsList = view.findViewById(R.id.instructionsList)
        continueButton = view.findViewById(R.id.continueButton)
        dontShowAgainTextView = view.findViewById(R.id.dontShowAgainTextView)
        cancelTextView = view.findViewById(R.id.cancelTextView)
        instructionsContainer = view.findViewById(R.id.instructionsContainer)
        goLiveContainer = view.findViewById(R.id.goLiveContainer)
        goLiveButton = view.findViewById(R.id.goLiveButton)
        cancelTextView.visibility = View.VISIBLE
        checkingConnectionContainer.visibility = View.VISIBLE
        showConnectionCheckingLoader()

        continueButton.setOnClickListener(this)
        goLiveButton.setOnClickListener(this)
        cancelTextView.setOnClickListener(this)
        dontShowAgainTextView.setOnClickListener(this)
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
            activity.let {
                connectionResultContainer.visibility = View.VISIBLE
                when ((it as LSCBroadCastSettingUpAndLiveActivity).getNetworkQuality()) {
                    0 -> {
                        netWorkQuality.text =
                            context?.resources?.getString(R.string.excellent_connection_string)
                        delay(2000)
                        if (SharedPrefUtils.getInstructionsShownFlag(it)) {
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

                        if (SharedPrefUtils.getInstructionsShownFlag(it)) {
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
                        if (SharedPrefUtils.getInstructionsShownFlag(it)) {
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
        }
    }

    private fun showInstructionsList() {
        connectionResultContainer.visibility = View.GONE
        cancelTextView.visibility = View.GONE
        instructionsContainer.visibility = View.VISIBLE


    }

    override fun onClick(v: View?) {
        activity?.let {
            when (v?.id) {
                R.id.continueButton -> {
                    instructionsContainer.visibility = View.GONE
                    goLiveContainer.visibility = View.VISIBLE
                }

                R.id.goLiveButton -> {
                    (it as LSCBroadCastSettingUpAndLiveActivity).joinChannel(this)
                }
                R.id.cancelTextView -> {

                    it.finish()
                }
                R.id.dontShowAgainTextView -> {
                    if (!instructionsChecked) {
                        val selectedCheckBox =
                            ContextCompat.getDrawable(it, R.drawable.ic_check_box)
                        dontShowAgainTextView.setCompoundDrawablesWithIntrinsicBounds(
                            selectedCheckBox,
                            null,
                            null,
                            null
                        )
                        SharedPrefUtils.setInstructionsShownFlag(it, true)
                        instructionsChecked = true
                    } else {
                        val selectedCheckBox =
                            ContextCompat.getDrawable(it, R.drawable.ic_uncheck_box)
                        dontShowAgainTextView.setCompoundDrawablesWithIntrinsicBounds(
                            selectedCheckBox,
                            null,
                            null,
                            null
                        )
                        SharedPrefUtils.setInstructionsShownFlag(it, false)
                        instructionsChecked = false
                    }
                }
            }
        }
    }

}