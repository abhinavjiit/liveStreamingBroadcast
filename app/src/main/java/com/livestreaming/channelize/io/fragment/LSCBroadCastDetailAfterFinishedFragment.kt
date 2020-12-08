package com.livestreaming.channelize.io.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.livestreaming.channelize.io.BaseApplication
import com.livestreaming.channelize.io.Injector
import com.livestreaming.channelize.io.LiveBroadcasterConstants
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.LSCBroadCastSettingUpAndLiveActivity
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.LSCBroadcastAndLiveViewModelFact
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.LSCLiveBroadCastViewModel
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.TIME_ELAPSED
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import kotlinx.android.synthetic.main.fragment_lsc_broadcast_detail.*
import javax.inject.Inject

class LSCBroadCastDetailAfterFinishedFragment : BaseFragment() {

    @Inject
    lateinit var lscBroadcastAndLiveViewModelFact: LSCBroadcastAndLiveViewModelFact
    private lateinit var viewModel: LSCLiveBroadCastViewModel
    private var broadCastId: String? = null
    private var conversationId: String? = null
    private var eventTitle: String? = null
    private var comingFrom: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lsc_broadcast_detail, container, false)
        (BaseApplication.getInstance() as Injector).createAppComponent().inject(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        broadCastId = arguments?.getString(LiveBroadcasterConstants.BROADCAST_ID)
        conversationId = arguments?.getString(LiveBroadcasterConstants.CONVERSATION_ID)
        comingFrom = arguments?.getString(LiveBroadcasterConstants.COMING_FROM)
        eventTitle = arguments?.getString(LiveBroadcasterConstants.EVENT_NAME)
        if (TIME_ELAPSED == comingFrom) {
            onStopBroadCast()
            (activity as LSCBroadCastSettingUpAndLiveActivity).onUnsubscribeTopics()
            cancelLiveBroadCastView.visibility = View.GONE
            headerTextView.text = eventTitle
            lscDetailsView.visibility = View.VISIBLE
            endNowTextView.visibility = View.GONE
            cancel.visibility = View.GONE
            closeTextView.visibility = View.VISIBLE
        } else {
            cancelLiveBroadCastView.visibility = View.VISIBLE
        }
        closeTextView.setOnClickListener {
            lscDetailsView.visibility = View.GONE
            val intent = Intent()
            intent.putExtra(LiveBroadcasterConstants.BROADCAST_ID, broadCastId)
            activity?.setResult(-1, intent)
            (activity as LSCBroadCastSettingUpAndLiveActivity).finish()
        }
        cancel.setOnClickListener {
            cancelLiveBroadCastView.visibility = View.GONE
            (activity as LSCBroadCastSettingUpAndLiveActivity).removeFragment(this)
        }
        endNowTextView.setOnClickListener {
            onStopBroadCast()
            (activity as LSCBroadCastSettingUpAndLiveActivity).onUnsubscribeTopics()
            cancelLiveBroadCastView.visibility = View.GONE
            headerTextView.text = eventTitle
            lscDetailsView.visibility = View.VISIBLE
            endNowTextView.visibility = View.GONE
            cancel.visibility = View.GONE
            closeTextView.visibility = View.VISIBLE
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            lscBroadcastAndLiveViewModelFact
        ).get(LSCLiveBroadCastViewModel::class.java)
    }

    private fun onStopBroadCast() {
        broadCastId?.let { broadcastId ->
            viewModel.onStopLSCBroadCast(broadcastId = broadcastId)
        }
        conversationId?.let { conversationId ->
            viewModel.onStopConversation(conversationId = conversationId)
        }
        getAllDetailsOfBroadCast()
    }

    @SuppressLint("SetTextI18n")
    private fun getAllDetailsOfBroadCast() {
        safeLet(broadCastId, conversationId) { broadCastId, conversationId ->
            viewModel.getAllDetailsOfBroadCast(
                broadcastId = broadCastId,
                conversationId = conversationId
            ).observe(viewLifecycleOwner, Observer { res ->
                when (res.status) {
                    Resource.Status.SUCCESS -> {
                        progressBar.visibility = View.GONE
                        loadingTextView.visibility = View.GONE
                        res.data?.let { lscBroadcastliveResponse ->
                            countContainer.visibility = View.VISIBLE
                            Log.d("ViewReactionMsgCount", lscBroadcastliveResponse.toString())
                            viewsCountTextView.text =
                                context?.resources?.getString(R.string.view_count) + lscBroadcastliveResponse.viewersCount
                            commentsCountTextView.text =
                                context?.resources?.getString(R.string.comment_count) + lscBroadcastliveResponse.messageCount
                            reactionsCountTextView.text =
                                context?.resources?.getString(R.string.reaction_count) + (lscBroadcastliveResponse.reactionsCount.angry +
                                        lscBroadcastliveResponse.reactionsCount.clap +
                                        lscBroadcastliveResponse.reactionsCount.dislike +
                                        lscBroadcastliveResponse.reactionsCount.heart +
                                        lscBroadcastliveResponse.reactionsCount.insightfull +
                                        lscBroadcastliveResponse.reactionsCount.laugh +
                                        lscBroadcastliveResponse.reactionsCount.like +
                                        lscBroadcastliveResponse.reactionsCount.sad +
                                        lscBroadcastliveResponse.reactionsCount.smiley +
                                        lscBroadcastliveResponse.reactionsCount.thankyou +
                                        lscBroadcastliveResponse.reactionsCount.wow)
                        }
                    }
                    Resource.Status.LOADING -> {
                        progressBar.visibility = View.VISIBLE
                        loadingTextView.visibility = View.VISIBLE
                    }
                    Resource.Status.ERROR -> {
                        Log.d("ViewReactionMsgCountEx", res.message.toString())
                        progressBar.visibility = View.GONE
                        loadingTextView.visibility = View.GONE
                    }
                }
            })
        }
    }

    private inline fun <T1 : Any, T2 : Any, R : Any> safeLet(
        p1: T1?,
        p2: T2?,
        block: (T1, T2) -> R?
    ): R? {
        return if (p1 != null && p2 != null) block(p1, p2) else null
    }

}