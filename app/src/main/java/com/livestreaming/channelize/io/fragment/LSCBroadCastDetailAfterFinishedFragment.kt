package com.livestreaming.channelize.io.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.livestreaming.channelize.io.BaseApplication
import com.livestreaming.channelize.io.Injector
import com.livestreaming.channelize.io.LiveBroadcasterConstants
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.LSCLiveBroadCastViewModel
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.TIME_ELAPSED
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import kotlinx.android.synthetic.main.fragment_lsc_broadcast_detail.*

class LSCBroadCastDetailAfterFinishedFragment : Fragment() {

    private lateinit var lscLiveBroadCastViewModel: LSCLiveBroadCastViewModel
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
        if (comingFrom == TIME_ELAPSED) {
            onStopBroadCast()
            lscLiveBroadCastViewModel.onUnsubscribeTopics.value = true
            clCancelLiveBroadCastView.visibility = View.GONE
            tvHeader.text = eventTitle
            clLscDetailsContainer.visibility = View.VISIBLE
            tvEndNow.visibility = View.GONE
            tvCancel.visibility = View.GONE
            tvClose.visibility = View.VISIBLE
        } else {
            clCancelLiveBroadCastView.visibility = View.VISIBLE
        }
        tvClose.setOnClickListener {
            clLscDetailsContainer.visibility = View.GONE
            val intent = Intent()
            intent.putExtra(LiveBroadcasterConstants.BROADCAST_ID, broadCastId)
            activity?.setResult(AppCompatActivity.RESULT_OK, intent)
            lscLiveBroadCastViewModel.onFinishBroadcast.value = true
        }
        tvCancel.setOnClickListener {
            clCancelLiveBroadCastView.visibility = View.GONE
            lscLiveBroadCastViewModel.onRemoveFragment(this)
        }
        tvEndNow.setOnClickListener {
            onStopBroadCast()
            lscLiveBroadCastViewModel.onUnsubscribeTopics.value = true
            clCancelLiveBroadCastView.visibility = View.GONE
            tvHeader.text = eventTitle
            clLscDetailsContainer.visibility = View.VISIBLE
            tvEndNow.visibility = View.GONE
            tvCancel.visibility = View.GONE
            tvClose.visibility = View.VISIBLE
        }
    }

    private fun initViewModel() {
        lscLiveBroadCastViewModel = ViewModelProvider(
            requireActivity(),
        ).get(LSCLiveBroadCastViewModel::class.java)
    }

    private fun onStopBroadCast() {
        broadCastId?.let { broadcastId ->
            lscLiveBroadCastViewModel.onStopLSCBroadCast(broadcastId = broadcastId)
        }
        conversationId?.let { conversationId ->
            lscLiveBroadCastViewModel.onStopConversation(conversationId = conversationId)
        }
        getAllDetailsOfBroadCast()
    }

    @SuppressLint("SetTextI18n")
    private fun getAllDetailsOfBroadCast() {
        safeLet(broadCastId, conversationId) { broadCastId, conversationId ->
            lscLiveBroadCastViewModel.getAllDetailsOfBroadCast(
                broadcastId = broadCastId,
                conversationId = conversationId
            ).observe(viewLifecycleOwner, Observer { res ->
                when (res.status) {
                    Resource.Status.SUCCESS -> {
                        progressBar.visibility = View.GONE
                        tvLoading.visibility = View.GONE
                        res.data?.let { lscBroadcastLiveResponse ->
                            llCountContainer.visibility = View.VISIBLE
                            Log.d("ViewReactionMsgCount", lscBroadcastLiveResponse.toString())
                            tvViewsCount.text =
                                context?.resources?.getString(R.string.view_count) + lscBroadcastLiveResponse.viewersCount
                            tvCommentsCount.text =
                                context?.resources?.getString(R.string.comment_count) + lscBroadcastLiveResponse.messageCount
                            tvReactionsCount.text =
                                context?.resources?.getString(R.string.reaction_count) + (lscBroadcastLiveResponse.reactionsCount.angry +
                                        lscBroadcastLiveResponse.reactionsCount.clap +
                                        lscBroadcastLiveResponse.reactionsCount.dislike +
                                        lscBroadcastLiveResponse.reactionsCount.heart +
                                        lscBroadcastLiveResponse.reactionsCount.insightfull +
                                        lscBroadcastLiveResponse.reactionsCount.laugh +
                                        lscBroadcastLiveResponse.reactionsCount.like +
                                        lscBroadcastLiveResponse.reactionsCount.sad +
                                        lscBroadcastLiveResponse.reactionsCount.smiley +
                                        lscBroadcastLiveResponse.reactionsCount.thankyou +
                                        lscBroadcastLiveResponse.reactionsCount.wow)
                        }
                    }
                    Resource.Status.ERROR -> {
                        Log.d("ViewReactionMsgCountEx", res.message.toString())
                        progressBar.visibility = View.GONE
                        tvLoading.visibility = View.GONE
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