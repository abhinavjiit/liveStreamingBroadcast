package com.livestreaming.channelize.io.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.livestreaming.channelize.io.BaseApplication
import com.livestreaming.channelize.io.Injector
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.LSCBroadCastSettingUpAndLiveActivity
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.LSCLiveBroadCastViewModel
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.LSCBroadcastAndLiveViewModelFact
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import javax.inject.Inject

class LSCBroadCastDetailAfterFinishedFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var lscBroadcastAndLiveViewModelFact: LSCBroadcastAndLiveViewModelFact
    private lateinit var cancelLiveBroadCastView: ConstraintLayout
    private lateinit var lscDetailsView: ConstraintLayout
    private lateinit var viewModel: LSCLiveBroadCastViewModel
    private lateinit var endNowTextView: TextView
    private lateinit var cancel: TextView
    private lateinit var closeTextView: TextView
    private lateinit var viewsCountTextView: TextView
    private lateinit var reactionsCountTextView: TextView
    private lateinit var commentsCountTextView: TextView
    private lateinit var countContainer: LinearLayout
    private lateinit var loadingTextView: TextView
    private lateinit var progressBar: ProgressBar
    private var broadCastId: String? = null
    private var conversationId: String? = null
    private var eventTitle: String? = null
    private lateinit var headerTextView: TextView
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
        conversationId = arguments?.getString("conversationId")
        eventTitle = arguments?.getString("eventTitle")
        return view
    }

    private fun initViews(view: View) {
        cancelLiveBroadCastView = view.findViewById(R.id.cancelLiveBroadCastView)
        lscDetailsView = view.findViewById(R.id.lscDetailsView)
        cancel = view.findViewById(R.id.cancel)
        endNowTextView = view.findViewById(R.id.endNowTextView)
        closeTextView = view.findViewById(R.id.closeTextView)
        viewsCountTextView = view.findViewById(R.id.viewsCountTextView)
        reactionsCountTextView = view.findViewById(R.id.reactionsCountTextView)
        commentsCountTextView = view.findViewById(R.id.commentsCountTextView)
        countContainer = view.findViewById(R.id.countContainer)
        loadingTextView = view.findViewById(R.id.loadingTextView)
        progressBar = view.findViewById(R.id.progressBar)
        headerTextView = view.findViewById(R.id.headerTextView)
        closeTextView.setOnClickListener(this)
        cancel.setOnClickListener(this)
        endNowTextView.setOnClickListener(this)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            lscBroadcastAndLiveViewModelFact
        ).get(LSCLiveBroadCastViewModel::class.java)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.endNowTextView -> {
                onStopBroadCast()
                (activity as LSCBroadCastSettingUpAndLiveActivity).leaveLSCBroadcast()
                cancelLiveBroadCastView.visibility = View.GONE
                headerTextView.text = eventTitle
                lscDetailsView.visibility = View.VISIBLE
                endNowTextView.visibility = View.GONE
                cancel.visibility = View.GONE
                closeTextView.visibility = View.VISIBLE
            }
            R.id.closeTextView -> {
                lscDetailsView.visibility = View.GONE
                val intent = Intent()
                intent.putExtra("broadCastId", broadCastId)
                activity?.setResult(-1, intent)
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
            viewModel.onStopLSCBroadCast(broadcastId = it)
        }
        conversationId?.let {
            viewModel.onStopConversation(conversationId = it)
        }
        getAllDetailsOfBroadCast()
    }

    @SuppressLint("SetTextI18n")
    private fun getAllDetailsOfBroadCast() {
        safeLet(broadCastId, conversationId) { broadCastId, conversationId ->
            viewModel.getAllDetailsOfBroadCast(
                broadcastId = broadCastId,
                conversationId = conversationId
            ).observe(this, Observer { res ->
                when (res.status) {
                    Resource.Status.SUCCESS -> {
                        progressBar.visibility = View.GONE
                        loadingTextView.visibility = View.GONE
                        res.data?.let {
                            countContainer.visibility = View.VISIBLE
                            Log.d("ViewReactionMsgCount", it.toString())
                            viewsCountTextView.text =
                                context?.resources?.getString(R.string.view_count) + it.viewersCount
                            commentsCountTextView.text =
                                context?.resources?.getString(R.string.comment_count) + it.messageCount
                            reactionsCountTextView.text =
                                context?.resources?.getString(R.string.reaction_count) + (it.reactionsCount.angry + it.reactionsCount.clap + it.reactionsCount.dislike +
                                        it.reactionsCount.heart +
                                        it.reactionsCount.insightfull +
                                        it.reactionsCount.laugh +
                                        it.reactionsCount.like +
                                        it.reactionsCount.sad +
                                        it.reactionsCount.smiley +
                                        it.reactionsCount.thankyou +
                                        it.reactionsCount.wow)
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