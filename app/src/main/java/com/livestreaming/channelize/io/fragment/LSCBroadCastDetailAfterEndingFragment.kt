package com.livestreaming.channelize.io.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.livestreaming.channelize.io.BaseApplication
import com.livestreaming.channelize.io.Injector
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.activity.lscSettingUp.LSCBroadCastSettingUpAndLiveActivity
import com.livestreaming.channelize.io.activity.lscSettingUp.LSCBroadCastViewModel
import com.livestreaming.channelize.io.activity.lscSettingUp.LSCBroadcastAndLiveViewModelFact
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import javax.inject.Inject

class LSCBroadCastDetailAfterEndingFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var lscBroadcastAndLiveViewModelFact: LSCBroadcastAndLiveViewModelFact
    private lateinit var cancelLiveBroadCastView: ConstraintLayout
    private lateinit var lscDetailsView: ConstraintLayout
    private lateinit var viewModel: LSCBroadCastViewModel
    private lateinit var endNowTextView: TextView
    private lateinit var cancel: TextView
    private lateinit var closeTextView: TextView
    private var broadCastId: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lsc_broadcast_detail, container, false)
        (BaseApplication.getInstance() as Injector).createAppComponent().inject(this)
        initViews(view)
        initViewModel()
        broadCastId = arguments?.getString("broadCastId")
        return view
    }

    private fun initViews(view: View) {
        cancelLiveBroadCastView = view.findViewById(R.id.cancelLiveBroadCastView)
        lscDetailsView = view.findViewById(R.id.lscDetailsView)
        cancel = view.findViewById(R.id.cancel)
        endNowTextView = view.findViewById(R.id.endNowTextView)
        closeTextView = view.findViewById(R.id.closeTextView)
        closeTextView.setOnClickListener(this)
        cancel.setOnClickListener(this)
        endNowTextView.setOnClickListener(this)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            lscBroadcastAndLiveViewModelFact
        ).get(LSCBroadCastViewModel::class.java)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.endNowTextView -> {
                onStopBroadCast()
                (activity as LSCBroadCastSettingUpAndLiveActivity).leaveLSCBroadcast()
                cancelLiveBroadCastView.visibility = View.GONE
                lscDetailsView.visibility = View.VISIBLE
                endNowTextView.visibility = View.GONE
                cancel.visibility = View.GONE
                closeTextView.visibility = View.VISIBLE
            }
            R.id.closeTextView -> {
                lscDetailsView.visibility = View.GONE
                (activity as LSCBroadCastSettingUpAndLiveActivity).finish()
            }
            R.id.cancel -> {
                cancelLiveBroadCastView.visibility = View.GONE
                (activity as LSCBroadCastSettingUpAndLiveActivity).removeFragment(this)
            }

        }
    }

    private fun onStopBroadCast() {
        broadCastId?.let {
            viewModel.onStopLSCBroadCast(it)
        }
        getAllDetailsOfBroadCast()
    }

    private fun getAllDetailsOfBroadCast() {
        broadCastId?.let {
            viewModel.getAllDetailsOfBroadCast(it).observe(this, Observer { res ->
                when (res.status) {
                    Resource.Status.SUCCESS -> {
                        res.data?.let {
                            Log.d("Tag", it.toString())
                        }

                    }
                    Resource.Status.LOADING -> {


                    }
                    Resource.Status.ERROR -> {
                        Log.d("Tag", res.message.toString())
                    }

                }

            })
        }
    }

}