package com.livestreaming.channelize.io.activity.lscSettingUpAndLive

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.channelize.apisdk.Channelize
import com.channelize.apisdk.network.mqtt.ChannelizeConnectionHandler
import com.channelize.apisdk.network.mqtt.ChannelizeConversationEventHandler
import com.channelize.apisdk.utils.ChannelizePreferences
import com.google.gson.Gson
import com.livestreaming.channelize.io.*
import com.livestreaming.channelize.io.activity.BaseActivity
import com.livestreaming.channelize.io.adapter.LSCCommentListAdapter
import com.livestreaming.channelize.io.fragment.*
import com.livestreaming.channelize.io.lscLiveReactions.Direction
import com.livestreaming.channelize.io.lscLiveReactions.ZeroGravityAnimation
import com.livestreaming.channelize.io.model.MessageCommentData
import com.livestreaming.channelize.io.model.RecordingParams
import com.livestreaming.channelize.io.model.StartBroadcastRequiredResponse
import com.livestreaming.channelize.io.model.lscLiveUpdatedModel.LSCLiveReactionsResponse
import com.livestreaming.channelize.io.model.lscLiveUpdatedModel.LSCLiveUpdatesResponse
import com.livestreaming.channelize.io.model.realTimeMessageResponse.MessageResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.BeautyOptions
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration
import kotlinx.android.synthetic.main.activity_lsc_broadcast_setting_up_and_live.*
import kotlinx.android.synthetic.main.adapter_event_broadcast_item_layout.*
import kotlinx.android.synthetic.main.bottom_sheet_fragment_product_list_layout.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.ArrayList

const val TIME_ELAPSED = "timeElapsed"
const val VIDEO_FRAME_WIDTH = 720
const val VIDEO_FRAME_HEIGHT = 1280
const val USER_ID = 0
const val DELAY = 2000L
const val ANIMATION_COUNT = 1
const val ANIMATION_SCALING_FACTOR = 0.5f
const val LAST_REMINDER_POPUP = 30000L

class LSCBroadCastSettingUpAndLiveActivity : BaseActivity(), ChannelizeConversationEventHandler,
    ChannelizeConnectionHandler {

    @Inject
    lateinit var lscBroadcastAndLiveViewModelFact: LSCBroadcastAndLiveViewModelFact
    private lateinit var lscBroadCastViewModel: LSCLiveBroadCastViewModel
    private lateinit var countDownTimer: CountDownTimer
    private var commentListData = ArrayList<MessageCommentData>()
    private lateinit var mLocalVideo: VideoCanvas
    private lateinit var mRtcEngine: RtcEngine
    private var startTime: String? = null
    private var endTime: String? = null
    private var broadCastId: String? = null
    private var productsIds: ArrayList<String>? = null
    private var conversationId: String? = null
    private var eventTitle: String? = null
    private var isLive = false
    private var networkQuality = -1

    private val firstReminderPopUp by lazy {
        showAlertDialogBox(msg = getString(R.string.first_reminder_string), title = getString(R.string.alert_string))
    }

    private val lastReminderPopUp by lazy {
        showAlertDialogBox(msg = getString(R.string.last_reminder_string), title = getString(R.string.alert_string))
        CoroutineScope(Dispatchers.Main).launch {
            delay(LAST_REMINDER_POPUP)
            showCancelLiveBroadCastFragment(TIME_ELAPSED)
        }
    }

    private val lscCommentListAdapter: LSCCommentListAdapter by lazy {
        LSCCommentListAdapter(this, commentListData)
    }

    private val startBroadcastRequiredResponse: StartBroadcastRequiredResponse by lazy {
        StartBroadcastRequiredResponse()
    }

    private val beautificationValues: BeautificationCustomizationValuesClassHolder by lazy {
        BeautificationCustomizationValuesClassHolder()
    }

    private val mEventHandler: IRtcEngineEventHandler by lazy {
        object : IRtcEngineEventHandler() {

            override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                runOnUiThread {
                    Log.i("onJoinChannelSuccess", "onJoinChannelSuccess")
                    Log.i("AgoraId", uid.toString())
                    startBroadcastRequiredResponse.rtcUserId = uid
                }
            }

            override fun onFirstLocalVideoFrame(width: Int, height: Int, elapsed: Int) {
                runOnUiThread {
                    Log.i("onFirstLocalVideoFrame", "success")
                    if (!isLive) {
                        val recordingParams = RecordingParams(width = VIDEO_FRAME_WIDTH, height = VIDEO_FRAME_HEIGHT)
                        startBroadcastRequiredResponse.recordingParams = recordingParams
                        onStratingBroadcast()
                        onStartingConversation()
                        setAllRequiredVisibility()
                    }
                    Log.d("onFirstLocalVideoFrame", "success")
                }
            }

            override fun onLastmileQuality(quality: Int) {
                Log.d("TAG", quality.toString())
                networkState(quality)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lsc_broadcast_setting_up_and_live)
        (BaseApplication.getInstance() as Injector).createAppComponent().inject(this)
        setupViewModel()
        Channelize.addConversationEventHandler(this)
        Channelize.addConnectionHandler(this)
        broadCastId = intent?.getStringExtra(LiveBroadcasterConstants.BROADCAST_ID)
        startTime = intent?.getStringExtra(LiveBroadcasterConstants.START_TIME)
        endTime = intent?.getStringExtra(LiveBroadcasterConstants.STOP_TIME)
        productsIds = intent?.getStringArrayListExtra(LiveBroadcasterConstants.EVENT_PRODUCT_IDS)
        conversationId = intent?.getStringExtra(LiveBroadcasterConstants.CONVERSATION_ID)
        eventTitle = intent.getStringExtra(LiveBroadcasterConstants.EVENT_NAME)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            showPermissionFragment()
        } else {
            settingUpFragment()
        }
        initCommentRecyclerView()
        clickListener
    }

    private fun initCommentRecyclerView() {
        commentRecyclerView.layoutManager = LinearLayoutManager(this)
        commentRecyclerView.adapter = lscCommentListAdapter
        lscCommentListAdapter.notifyDataSetChanged()
    }

    private fun showPermissionFragment() {
        val fragment = LSCPermissionFragment()
        val fm = supportFragmentManager
        val transaction =
            fm.beginTransaction().add(R.id.flContentFrame, fragment, "LSCPermissionFragment").addToBackStack(null)
        transaction.commit()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val fragment = supportFragmentManager.findFragmentByTag("LSCPermissionFragment")
        fragment?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setupViewModel() {
        lscBroadCastViewModel =
            ViewModelProvider(this, lscBroadcastAndLiveViewModelFact).get(LSCLiveBroadCastViewModel::class.java)

        lscBroadCastViewModel.onUnsubscribeTopics.observe(this, Observer { onUnsubscribeTopics ->
            if (onUnsubscribeTopics) {
                onUnsubscribeTopics()
            }
        })

        lscBroadCastViewModel.onFinishBroadcast.observe(this, Observer { onFinishBroadcast ->
            if (onFinishBroadcast) {
                finish()
            }
        })

        lscBroadCastViewModel.onRemoveFragment.observe(this, Observer { fragment ->
            removeFragment(fragment)
        })

        lscBroadCastViewModel.onSettingUpFragment.observe(this, Observer { onSettingUpFragment ->
            if (onSettingUpFragment) {
                settingUpFragment()
            }
        })
    }

    private fun settingUpFragment() {
        val fragment = supportFragmentManager.findFragmentByTag("LSCPermissionFragment")
        if (fragment != null && fragment is LSCPermissionFragment) {
            removeFragment(fragment)
        }
        val settingFragment = LSCSettingUpLiveStreamingFragment()
        supportFragmentManager.beginTransaction().add(R.id.flContentFrame, settingFragment, "LSCSettingUpFragment")
            .commit()
    }

    private fun removeFragment(fragment: Fragment?) {
        fragment?.let { removeFrag ->
            supportFragmentManager.beginTransaction().remove(removeFrag).commit()
        }
    }

    private val clickListener: Unit
        get() {
            ivBeautification.setOnClickListener {
                val beautificationView = LSCBeautificationBottomSheetDialogFragment()
                beautificationView.show(supportFragmentManager, "beauty_sheet")
            }
            ivProductListView.setOnClickListener {
                val lscProductsListDialogFragment = LSCProductsListDialogFragment()
                val bundle = Bundle()
                bundle.putStringArrayList(LiveBroadcasterConstants.EVENT_PRODUCT_IDS, productsIds)
                lscProductsListDialogFragment.arguments = bundle
                lscProductsListDialogFragment.show(supportFragmentManager, "LSCProductsListDialogFragment")
            }
            ivPost.setOnClickListener {
                if (etWriteSomething.text.toString().trim().isNotBlank()) {
                    sendCommentData(etWriteSomething.text.toString())
                    etWriteSomething.setText("")
                    try {
                        val imm: InputMethodManager =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                    } catch (e: Exception) {
                        Log.d("Exception", "KEYPAD ERROR")
                    }
                }
            }
            ivCancelLiveBroadcast.setOnClickListener {
                if (isLive) {
                    showCancelLiveBroadCastFragment()
                } else {
                    finish()
                }
            }
            ivFlipCamera.setOnClickListener {
                switchCamera()
            }
        }

    private fun initLocalVideoConfig() {
        setupVideoConfig()
        setLocalVideo()
    }

    fun joinChannel(fragment: Fragment) {
        try {
            removeFragment(fragment)
            val progress = progressDialog(this)
            progress.show()
            CoroutineScope(Dispatchers.Main).launch {
                mRtcEngine.joinChannel(
                    null,
                    broadCastId,
                    "",
                    SharedPrefUtils.getUniqueId(this@LSCBroadCastSettingUpAndLiveActivity).toInt()
                )
                delay(DELAY)
                progress.dismiss()
                val progress2 = progressDialog(
                    this@LSCBroadCastSettingUpAndLiveActivity,
                    onlyText = true,
                    text = getString(R.string.you_are_live_now)
                )
                progress2.show()
                delay(DELAY.div(2))
                progress2.dismiss()
            }
        } catch (e: Exception) {
            Log.d("JoinChannelEx", e.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        if (!::mRtcEngine.isInitialized) {
            initRTCEngine()
            initLocalVideoConfig()
        }
    }

    // throw RuntimeException("Need to check RTC sdk init fatal")
    private fun initRTCEngine() {
        try {
            SharedPrefUtils.getAppId(this)?.let { appId ->
                Log.d("AppId", appId)
                mRtcEngine = RtcEngine.create(BaseApplication.getInstance(), appId, mEventHandler)
                mRtcEngine.enableLastmileTest()
                mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
                mRtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)
                lscBroadCastViewModel.onchangeBeautification.observe(this, Observer {
                    mRtcEngine.setBeautyEffectOptions(
                        beautificationValues.isEnabled,
                        BeautyOptions(
                            beautificationValues.contrastValue,
                            beautificationValues.lightingValue,
                            beautificationValues.smoothnessValue,
                            beautificationValues.rednessValue
                        )
                    )
                })
                lscBroadCastViewModel.beautificationCustomizationValuesClassHolder.value = beautificationValues
            }
        } catch (e: Exception) {
            startAppIdService()
            Log.d("RTC_FATAL_EXCEPTION", e.toString())
        }
    }

    private fun setupVideoConfig() {
        mRtcEngine.enableVideo()
        mRtcEngine.setVideoEncoderConfiguration(
            VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_1280x720,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
            )
        )
    }

    private fun setLocalVideo() {
        val view = RtcEngine.CreateRendererView(BaseApplication.getInstance())
        view.setZOrderMediaOverlay(true)
        cslBroadcastContainer.addView(view)
        mLocalVideo = VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, USER_ID)
        mRtcEngine.setupLocalVideo(mLocalVideo)
    }

    fun networkState(networkQuality: Int) {
        this.networkQuality = networkQuality
    }

    fun getNetworkQuality(): Int {
        return networkQuality
    }

    private fun switchCamera() {
        mRtcEngine.switchCamera()
    }

    private fun showCancelLiveBroadCastFragment(comingFrom: String = "") {
        val fragment = LSCBroadCastDetailAfterFinishedFragment()
        val bundle = Bundle()
        bundle.putString(LiveBroadcasterConstants.BROADCAST_ID, broadCastId)
        bundle.putString(LiveBroadcasterConstants.CONVERSATION_ID, conversationId)
        bundle.putString(LiveBroadcasterConstants.EVENT_NAME, eventTitle)
        bundle.putString(LiveBroadcasterConstants.COMING_FROM, comingFrom)
        fragment.arguments = bundle
        val fm = supportFragmentManager
        val transaction = fm.beginTransaction()
            .add(R.id.flContentFrame, fragment, "LSCBroadCastDetailAfterEndingFragment")
            .addToBackStack(null)
        transaction.commit()
    }

    private fun sendCommentData(message: String) {
        val commentData = MessageCommentData()
        commentData.id = UUID.randomUUID().toString()
        commentData.body = message
        commentData.conversationId = conversationId
        commentData.userName = ChannelizePreferences.getCurrentUserName(BaseApplication.getInstance())
        commentData.userImage = ChannelizePreferences.getCurrentUserProfileImage(BaseApplication.getInstance())
        commentListData.add(commentData)
        lscCommentListAdapter.notifyDataSetChanged()
        commentRecyclerView.smoothScrollToPosition(commentListData.size - 1)
        lscBroadCastViewModel.postComment(commentData).observe(this, Observer {
        })
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun lscRemainingTime() {
        endTime?.let { endingTime ->
            val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            outputFormat.timeZone = TimeZone.getTimeZone("IST")
            val endTimeFormat = outputFormat.parse(endingTime)
            val currentTime = Calendar.getInstance().time
            val differenceInTime: Long? = endTimeFormat?.time?.minus(currentTime.time)
            val differenceInMinutes = ((differenceInTime?.div((1000 * 60)))?.rem(60))
            val differenceInHours = ((differenceInTime?.div((1000 * 60 * 60)))?.rem(24))

            tvTotalTimeLeft.text = " ${differenceInHours}h ${differenceInMinutes}m"

            differenceInTime?.let { timeDiff ->
                countDownTimer = object : CountDownTimer(timeDiff, 1000) {
                    override fun onFinish() {
                        Log.d("TimeCounter", "TimeFinishedCounterLiveActivity")
                        tvRemainingTimeCounter.visibility = View.GONE
                    }

                    override fun onTick(millisUntilFinished: Long) {
                        val elapsedTime = timeDiff.minus(millisUntilFinished)
                        var remainingTime = millisUntilFinished
                        val differenceInMinutes1 = ((elapsedTime
                                / (1000 * 60))
                                % 60)
                        val reminderAlertDialogTime = TimeUnit.MILLISECONDS.toMinutes(remainingTime)
                        remainingTime -= TimeUnit.MINUTES.toMillis(
                            reminderAlertDialogTime
                        )
                        val remainSeconds = TimeUnit.MILLISECONDS.toSeconds(remainingTime)

                        tvRemainingTimeCounter.text = "${differenceInMinutes1}m /"

                        alertDialogBox(reminderAlertDialogTime, remainSeconds)
                        Log.d("RESULT", "see")
                    }
                }.start()
            }
        }
    }

    private fun onStratingBroadcast() {
        broadCastId?.let { broadcastId ->
            lscBroadCastViewModel.onStartLSCBroadCast(broadcastId, startBroadcastRequiredResponse)
                .observe(this,
                    Observer { startBroadcastRes ->
                        when (startBroadcastRes?.status) {
                            Resource.Status.ERROR -> {
                                startBroadcastRes.message?.let { startBroadCastStatus ->
                                    showToast(this, startBroadCastStatus)
                                    Log.d("START_BROADCAST_ERROR", startBroadCastStatus)
                                }
                            }
                            Resource.Status.SUCCESS -> {
                                startBroadcastRes.data?.let {
                                    Log.d("START_BROADCAST", "SUCCESS")
                                }
                            }
                            else -> {
                                Log.d("START_BROADCAST_TAG", "START_BROADCAST")
                            }
                        }
                    })
        }
    }

    private fun onStartingConversation() {
        conversationId?.let { conversationId ->
            lscBroadCastViewModel.onStartConversation(conversationId)
                .observe(this, Observer { startConversationResponse ->
                    if (startConversationResponse != null) {
                        Log.d("StartConversationRes", "Success")
                        onSubscribeTopics()
                        isLive = true
                        lscRemainingTime()
                    } else {
                        showToast(this, resources.getString(R.string.something_went_wrong))
                        Log.d("StartConversationRes", "Failure")
                    }
                })
        }
    }

    override fun onOpenConversationMessageReceived(response: String?) {
        response?.let { messageResponse ->
            Log.d("onMessageCreated", messageResponse)
            val res = Gson().fromJson(messageResponse, MessageResponse::class.java)
            val commentData = MessageCommentData()
            commentData.body = res.message.body
            commentData.userName = res.message.owner.displayName
            commentData.id = res.message.id
            commentListData.add(commentData)
            val listOfMessaged = LinkedHashSet(commentListData)
            commentListData.clear()
            commentListData.addAll(listOfMessaged)
            lscCommentListAdapter.notifyDataSetChanged()
            commentRecyclerView.smoothScrollToPosition(commentListData.size - 1)
        }
    }

    override fun onStartWatching(response: String?) {
        response?.let { liveUserCountResponse ->
            showLiveUserCount(liveUserCountResponse)
        }
    }

    override fun onStopWatching(response: String?) {
        response?.let { liveUserCountResponse ->
            showLiveUserCount(liveUserCountResponse)
        }
    }

    override fun onRealTimeDataUpdate(topic: String?, response: String?) {
        topic?.let { topicUrl ->
            when (topicUrl) {
                "live_broadcasts/$broadCastId/reaction_added" -> {
                    onLSCReactionsAdded(response)
                }
                else -> {
                    Log.d("OnRealTimeUpdateError", topicUrl)
                }
            }
        }
    }

    private fun showLiveUserCount(response: String?) {
        val res = Gson().fromJson(response, LSCLiveUpdatesResponse::class.java)
        tvLiveViewCount.text = (res.liveBroadcast.watchersCount).toString()
    }

    private fun onLSCReactionsAdded(response: String?) {
        val res = Gson().fromJson(response, LSCLiveReactionsResponse::class.java)
        when (res.reaction.type) {
            LiveBroadcasterConstants.ANGRY -> {
                val smileDrawable = ContextCompat.getDrawable(this, R.drawable.ic_angry) as VectorDrawable
                onFlyReactions(smileDrawable)
            }
            LiveBroadcasterConstants.THANK_YOU -> {
                val smileDrawable = ContextCompat.getDrawable(this, R.drawable.ic_thankyou) as VectorDrawable
                onFlyReactions(smileDrawable)
            }
            LiveBroadcasterConstants.WOW -> {
                val smileDrawable = ContextCompat.getDrawable(this, R.drawable.ic_wow) as VectorDrawable
                onFlyReactions(smileDrawable)
            }
            LiveBroadcasterConstants.SAD -> {
                val smileDrawable = ContextCompat.getDrawable(this, R.drawable.ic_sad) as VectorDrawable
                onFlyReactions(smileDrawable)
            }
            LiveBroadcasterConstants.SMILEY -> {
                val smileDrawable = ContextCompat.getDrawable(this, R.drawable.ic_smile) as VectorDrawable
                onFlyReactions(smileDrawable)
            }
            LiveBroadcasterConstants.CLAP -> {
                val smileDrawable = ContextCompat.getDrawable(this, R.drawable.ic_clapping) as VectorDrawable
                onFlyReactions(smileDrawable)
            }
            LiveBroadcasterConstants.LIKE -> {
                val smileDrawable = ContextCompat.getDrawable(this, R.drawable.ic_like) as VectorDrawable
                onFlyReactions(smileDrawable)
            }
            LiveBroadcasterConstants.LAUGH -> {
                val smileDrawable = ContextCompat.getDrawable(this, R.drawable.ic_laugh) as VectorDrawable
                onFlyReactions(smileDrawable)
            }
            LiveBroadcasterConstants.HEART -> {
                val smileDrawable = ContextCompat.getDrawable(this, R.drawable.ic_heart) as VectorDrawable
                onFlyReactions(smileDrawable)
            }
            LiveBroadcasterConstants.INSIGHT -> {
                val smileDrawable = ContextCompat.getDrawable(this, R.drawable.ic_bulb) as VectorDrawable
                onFlyReactions(smileDrawable)
            }
            LiveBroadcasterConstants.DISLIKE -> {
                val smileDrawable = ContextCompat.getDrawable(this, R.drawable.ic_dislike) as VectorDrawable
                onFlyReactions(smileDrawable)
            }
        }
    }

    private fun onFlyReactions(iconDrawable: VectorDrawable) {
        val animation = ZeroGravityAnimation()
        animation.setCount(ANIMATION_COUNT)
        animation.setScalingFactor(ANIMATION_SCALING_FACTOR)
        animation.setOriginationDirection(Direction.BOTTOM)
        animation.setDestinationDirection(Direction.TOP)
        animation.setImage(iconDrawable)
        animation.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
        }
        )
        val container: ViewGroup = findViewById(R.id.flAnimatorLoader)
        animation.play(this, container)
    }

    private fun alertDialogBox(remainingMinuteTime: Long, remainingSeconds: Long) {
        (remainingMinuteTime == 2L || remainingMinuteTime == 0L).also { isTimeReached ->
            when (isTimeReached) {
                true -> {
                    if (remainingMinuteTime == 2L && remainingSeconds == 0L) {
                        firstReminderPopUp
                    } else if (remainingMinuteTime == 0L && remainingSeconds == 0L) {
                        lastReminderPopUp
                    }
                }
                else -> {
                    Log.d("REMINDER_TIME", "FALSE")
                }
            }
        }
    }

    override fun onBackPressed() {
        Log.d("onBackPressed", "onBackPressed")
        if (isLive) {
            showCancelLiveBroadCastFragment()
        } else {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("onDestroy", "onDestroy")
        onUnsubscribeTopics()
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
        if (::mRtcEngine.isInitialized) {
            mRtcEngine.leaveChannel()
        }
        RtcEngine.destroy()
    }

    private fun setAllRequiredVisibility() {
        ivBeautification.visibility = View.VISIBLE
        tvLive.visibility = View.VISIBLE
        ivCancelLiveBroadcast.visibility = View.VISIBLE
        tvLiveViewCount.visibility = View.VISIBLE
        ivProductListView.visibility = View.VISIBLE
        etWriteSomething.visibility = View.VISIBLE
        cslBottomContainer.visibility = View.VISIBLE
        commentRecyclerView.visibility = View.VISIBLE
        ivFlipCamera.visibility = View.VISIBLE
        cslTimerContainer.visibility = View.VISIBLE
    }

    private fun onSubscribeTopics() {
        Channelize.getInstance().addSubscriber("conversations/$conversationId/message_created")
        Channelize.getInstance().addSubscriber("live_broadcasts/$broadCastId/start_watching")
        Channelize.getInstance().addSubscriber("live_broadcasts/$broadCastId/stop_watching")
        Channelize.getInstance().addSubscriber("live_broadcasts/$broadCastId/reaction_added")
    }

    private fun onUnsubscribeTopics() {
        try {
            Channelize.getInstance().unSubscribeTopic("live_broadcasts/$broadCastId/start_watching")
            Channelize.getInstance().unSubscribeTopic("live_broadcasts/$broadCastId/stop_watching")
            Channelize.getInstance().unSubscribeTopic("live_broadcasts/$broadCastId/reaction_added")
            Channelize.getInstance().unSubscribeTopic("conversations/$conversationId/message_created")
        } catch (e: Exception) {
            Log.d("Tag", e.toString())
        }
    }

}

