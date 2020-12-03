package com.livestreaming.channelize.io.activity.lscSettingUpAndLive

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.channelize.apisdk.Channelize
import com.channelize.apisdk.ChannelizeConfig
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
import com.livestreaming.channelize.io.model.productdetailModel.ProductDetailResponse
import com.livestreaming.channelize.io.model.realTimeMessageResponse.MessageResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.BeautyOptions
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class LSCBroadCastSettingUpAndLiveActivity : BaseActivity(), View.OnClickListener,
    ChannelizeConversationEventHandler {

    private lateinit var timerContainer: RelativeLayout
    private lateinit var remainingTimeCounter: TextView
    private lateinit var totalTimeLeft: TextView
    private lateinit var broadCasterContainer: RelativeLayout
    private lateinit var mLocalVideo: VideoCanvas
    private lateinit var mRtcEngine: RtcEngine
    private var networkQuality = -1
    private lateinit var beautification: ImageView
    private lateinit var cancelLiveBroadcast: ImageView
    private lateinit var liveTextView: TextView
    private lateinit var liveViewCount: TextView
    private lateinit var productListView: ImageView
    private lateinit var writeSomethingEditText: EditText
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var lscBroadCastViewModel: LSCLiveBroadCastViewModel
    private lateinit var post: ImageView
    private lateinit var bottomContainer: ConstraintLayout
    private var startTime: String? = null
    private var endTime: String? = null
    private var productsList: ArrayList<ProductDetailResponse>? = null
    private var broadCastId: String? = null
    private var isLive = false
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var flipCamera: ImageView
    private var productsIds: ArrayList<String>? = null
    private var conversationId: String? = null
    private var eventTitle: String? = null
    private var commentListData = ArrayList<MessageCommentData>()

    private val lscCommentListAdapter: LSCCommentListAdapter by lazy {
        LSCCommentListAdapter()
    }

    private val startBroadcastRequiredResponse: StartBroadcastRequiredResponse by lazy {
        StartBroadcastRequiredResponse()
    }


    @Inject
    lateinit var lscBroadcastAndLiveViewModelFact: LSCBroadcastAndLiveViewModelFact
    private val beautificationValues: BeautificationCustomizationValuesClassHolder by lazy {
        BeautificationCustomizationValuesClassHolder()
    }

    private val mEventHandler: IRtcEngineEventHandler by lazy {
        object : IRtcEngineEventHandler() {

            override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                runOnUiThread {
                    Log.i("onJoinChannelSuccess", "onJoinChannelSuccess")
                    Log.i("Agora_ID", uid.toString())
                    startBroadcastRequiredResponse.rtcUserId = uid
                    Channelize.getInstance().broadCastId = broadCastId
                    Channelize.getInstance().conversationId = conversationId
                    Channelize.getInstance().updateConnectStatus(true)
                    runBlocking {
                        Channelize.getInstance().addSubscriber(
                            "live_broadcasts/$broadCastId/start_watching"
                        )
                        Channelize.getInstance().addSubscriber(
                            "live_broadcasts/$broadCastId/reaction_added"
                        )
                        Channelize.getInstance().addSubscriber(
                            "live_broadcasts/$broadCastId/stop_watching"
                        )
                        Channelize.getInstance().addSubscriber(
                            "conversations/$conversationId/message_created"
                        )
                    }
                    lscRemainingTime()
                }
            }

            override fun onFirstLocalVideoFrame(width: Int, height: Int, elapsed: Int) {
                runOnUiThread {
                    Log.i("onFirstLocalVideoFrame", "success")
                    val recordingParams = RecordingParams(width = width, height = height)
                    startBroadcastRequiredResponse.recordingParams = recordingParams
                    hitStartBroadCastApi()
                    hitStartConversationApi()
                    beautification.visibility = View.VISIBLE
                    liveTextView.visibility = View.VISIBLE
                    cancelLiveBroadcast.visibility = View.VISIBLE
                    liveViewCount.visibility = View.VISIBLE
                    productListView.visibility = View.VISIBLE
                    writeSomethingEditText.visibility = View.VISIBLE
                    bottomContainer.visibility = View.VISIBLE
                    commentRecyclerView.visibility = View.VISIBLE
                    flipCamera.visibility = View.VISIBLE
                    timerContainer.visibility = View.VISIBLE
                    isLive = true
                    Log.d("onFirstLocalVideoFrame", "success")
                }
            }

            override fun onLastmileQuality(quality: Int) {
                Log.d("TAG", quality.toString())
                networkState(quality)
            }

            override fun onConnectionLost() {

            }

            override fun onNetworkQuality(uid: Int, txQuality: Int, rxQuality: Int) {
                Log.d("TAG", rxQuality.toString())

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lsc_broadcast_setting_up_and_live)
        (BaseApplication.getInstance() as Injector).createAppComponent().inject(this)
        val channelizeConfig = if (BuildConfig.DEBUG) {
            ChannelizeConfig.Builder(this).setAPIKey(SharedPrefUtils.getPublicApiKey(this))
                .setLoggingEnabled(true)
                .build()
        } else {
            ChannelizeConfig.Builder(this).setAPIKey(SharedPrefUtils.getPublicApiKey(this))
                .setLoggingEnabled(false)
                .build()
        }
        Channelize.initialize(channelizeConfig)
        Channelize.addConversationEventHandler(this)
        initViews
        broadCastId = intent?.getStringExtra("broadCastId")
        startTime = intent?.getStringExtra("startTime")
        endTime = intent?.getStringExtra("endTime")
        productsIds = intent?.getStringArrayListExtra("productsIds")
        conversationId = intent?.getStringExtra("conversationId")
        eventTitle = intent.getStringExtra("eventName")
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            showPermissionFragment()
        } else {
            settingUpFragment()
        }

        initCommentRecyclerView()
        setupViewModel()
        getProductsList()
        clickListener
    }

    private val initViews: Unit
        get() {
            broadCasterContainer = findViewById(R.id.broadCasterContainer)
            beautification = findViewById(R.id.beautification)
            liveTextView = findViewById(R.id.liveTextView)
            liveViewCount = findViewById(R.id.liveViewCount)
            cancelLiveBroadcast = findViewById(R.id.cancelLiveBroadcast)
            productListView = findViewById(R.id.productListView)
            writeSomethingEditText = findViewById(R.id.writeSomethingEditText)
            commentRecyclerView = findViewById(R.id.commentRecyclerView)
            bottomContainer = findViewById(R.id.bottomContainer)
            post = findViewById(R.id.post)
            remainingTimeCounter = findViewById(R.id.remainingTimeCounter)
            totalTimeLeft = findViewById(R.id.totalTimeLeft)
            timerContainer = findViewById(R.id.timerContainer)
            flipCamera = findViewById(R.id.flipCamera)
        }

    private fun initCommentRecyclerView() {
        commentRecyclerView.layoutManager = LinearLayoutManager(this)
        commentRecyclerView.adapter = lscCommentListAdapter
        lscCommentListAdapter.setListData(null)
        lscCommentListAdapter.notifyDataSetChanged()
    }

    private fun showPermissionFragment() {
        val fragment = LSCPermissionFragment()
        val fm = supportFragmentManager
        val transaction =
            fm.beginTransaction().add(R.id.content_frame, fragment, "LSCPermissionFragment")
                .addToBackStack(null)
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
        lscBroadCastViewModel = ViewModelProvider(
            this,
            lscBroadcastAndLiveViewModelFact
        ).get(LSCLiveBroadCastViewModel::class.java)
    }

    fun settingUpFragment() {
        val fragment = supportFragmentManager.findFragmentByTag("LSCPermissionFragment")
        if (fragment != null && fragment is LSCPermissionFragment) {
            removeFragment(fragment)
        }
        val settingFragment = LSCSettingUpFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.content_frame, settingFragment, "LSCSettingUpFragment")
            .commit()
    }

    fun removeFragment(fragment: Fragment?) {
        fragment?.let {
            supportFragmentManager.beginTransaction().remove(it).commit()
        }
    }

    private val clickListener: Unit
        get() {
            beautification.setOnClickListener(this)
            productListView.setOnClickListener(this)
            post.setOnClickListener(this)
            cancelLiveBroadcast.setOnClickListener(this)
            flipCamera.setOnClickListener(this)
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
                delay(2000)
                progress.dismiss()
                val progress2 = progressDialog(
                    this@LSCBroadCastSettingUpAndLiveActivity,
                    onlyText = true,
                    text = "You are now live!"
                )
                progress2.show()
                delay(1000)
                progress2.dismiss()
            }
        } catch (e: Exception) {
            Log.d("JoinChannelEx", e.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        initRTCEngine()
        initLocalVideoConfig()
    }

    // throw RuntimeException("Need to check RTC sdk init fatal")
    private fun initRTCEngine() {
        try {
            Log.d("AppId", SharedPrefUtils.getAppId(this)!!)
            mRtcEngine = RtcEngine.create(
                BaseApplication.getInstance(),
                SharedPrefUtils.getAppId(this),
                mEventHandler
            )
            mRtcEngine.enableLastmileTest()
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
            mRtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)
            changeBeautification(beautificationValues)
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
        broadCasterContainer.addView(view)
        mLocalVideo = VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, 0)
        mRtcEngine.setupLocalVideo(mLocalVideo)
    }

    fun networkState(networkQuality: Int) {
        this.networkQuality = networkQuality
    }

    fun getNetworkQuality(): Int {
        return networkQuality
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.beautification -> {
                val beautificationView =
                    LSCBeautificationBottomSheetDialogFragment()
                beautificationView.show(supportFragmentManager, "beauty_sheet")
            }
            R.id.productListView -> {
                val lscProductsListDialogFragment = LSCProductsListDialogFragment()
                lscProductsListDialogFragment.show(
                    supportFragmentManager,
                    "LSCProductsListDialogFragment"
                )
            }
            R.id.post -> {
                if (writeSomethingEditText.text.toString().trim().isNotBlank()) {
                    sendCommentData(writeSomethingEditText.text.toString())
                    writeSomethingEditText.setText("")
                    try {
                        val imm: InputMethodManager =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                    } catch (e: Exception) {
                        Log.d("Exception", "KEYPAD ERROR")
                    }
                }
            }
            R.id.cancelLiveBroadcast -> {
                if (isLive)
                    showCancelLiveBroadCastFragment()
                else
                    finish()
            }
            R.id.flipCamera -> {
                switchCamera()
            }
        }
    }

    private fun switchCamera() {
        mRtcEngine.switchCamera()
    }

    private fun showCancelLiveBroadCastFragment() {
        val fragment = LSCBroadCastDetailAfterFinishedFragment()
        val bundle = Bundle()
        bundle.putString("broadCastId", broadCastId)
        bundle.putString("conversationId", conversationId)
        bundle.putString("eventTitle", eventTitle)
        fragment.arguments = bundle
        val fm = supportFragmentManager
        val transaction =
            fm.beginTransaction()
                .add(R.id.content_frame, fragment, "LSCBroadCastDetailAfterEndingFragment")
                .addToBackStack(null)
        transaction.commit()
    }

    fun changeBeautification(beautificationValues: BeautificationCustomizationValuesClassHolder) {
        mRtcEngine.setBeautyEffectOptions(
            beautificationValues.isEnabled,
            BeautyOptions(
                beautificationValues.contrastValue,
                beautificationValues.lightingValue,
                beautificationValues.smoothnessValue,
                beautificationValues.rednessValue
            )
        )
    }

    fun getBeautificationObject(): BeautificationCustomizationValuesClassHolder {
        return beautificationValues
    }


    private fun getProductsList() {
        val productsIdsCommaSeparated: String? = productsIds?.let { productsId ->
            var id = ""
            productsId.forEach { it ->
                id = if (id.isBlank())
                    id.plus(it)
                else
                    id.plus(",$it")
            }
            id
        }
        lscBroadCastViewModel.getProductItems(productsIdsCommaSeparated).observe(this, Observer {
            when (it?.status) {
                Resource.Status.SUCCESS -> {
                    it.data?.let { productItemsRes ->
                        if (productItemsRes.statusCode == 200 && productItemsRes.success == "OK") {
                            productItemsRes.data.products?.let { itemList ->
                                productsList =
                                    itemList.toMutableList() as ArrayList<ProductDetailResponse>
                            }
                        }
                    }
                }
                Resource.Status.ERROR -> {
                    productsList = null
                }
                Resource.Status.LOADING -> {
                }
            }
        })
    }


    private fun sendCommentData(message: String) {
        val commentData = MessageCommentData()
        commentData.id = UUID.randomUUID().toString()
        commentData.body = message
        commentData.conversationId = conversationId
        commentData.userName =
            ChannelizePreferences.getCurrentUserName(BaseApplication.getInstance())
        commentData.userImage =
            ChannelizePreferences.getCurrentUserProfileImage(BaseApplication.getInstance())
        commentListData.add(commentData)
        lscCommentListAdapter.setListData(commentListData)
        lscCommentListAdapter.notifyDataSetChanged()
        commentRecyclerView.smoothScrollToPosition(commentListData.size - 1)
        lscBroadCastViewModel.postComment(commentData).observe(this, Observer {

        })
    }

    fun getProductsListData(): ArrayList<ProductDetailResponse>? {
        return productsList
    }


    @SuppressLint("SimpleDateFormat")
    private fun lscRemainingTime() {
        endTime?.let {
            val outputFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            outputFormat.timeZone = TimeZone.getTimeZone("IST")
            val endTimeFormat = outputFormat.parse(it)
            val currentTime = Calendar.getInstance().time
            val differenceInTime: Long = endTimeFormat?.time?.minus(currentTime.time)!!
            val differenceInMinutes = ((differenceInTime
                    / (1000 * 60))
                    % 60)

            val differenceInHours = ((differenceInTime
                    / (1000 * 60 * 60))
                    % 24)

            totalTimeLeft.text = " ${differenceInHours}h ${differenceInMinutes}m"
            countDownTimer = object : CountDownTimer(differenceInTime, 1000) {
                override fun onFinish() {
                    Log.d("Tag", "aaaa")
                    remainingTimeCounter.visibility = View.GONE
                }

                override fun onTick(millisUntilFinished: Long) {
                    val remainingTime = differenceInTime.minus(millisUntilFinished)

                    val differenceInMinutes1 = ((remainingTime
                            / (1000 * 60))
                            % 60)

                    val differenceInHours1 = ((remainingTime
                            / (1000 * 60 * 60))
                            % 24)
                    remainingTimeCounter.text = "${differenceInMinutes1}m /"
                    Log.d("RESULT", "see")
                }

            }.start()
        }
        /*will change this code to obserables*/

/*        lscBroadCastViewModel.getCounterTime(endTime).observe(this, Observer {
            Log.d("counterTime", "///${it}m")
        })
        lscBroadCastViewModel.getTotalTime(endTime).observe(this, Observer {
            Log.d("TotalTime", it)
        })*/
    }


    private fun hitStartBroadCastApi() {
        broadCastId?.let {
            lscBroadCastViewModel.onStartLSCBroadCast(it, startBroadcastRequiredResponse)
                .observe(this,
                    Observer { startBroadcastRes ->
                        when (startBroadcastRes?.status) {
                            Resource.Status.ERROR -> {
                                startBroadcastRes.message?.let {
                                    showToast(this, it)
                                    Log.d("START_BROADCAST_ERROR", it)
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

    private fun hitStartConversationApi() {
        conversationId?.let {
            lscBroadCastViewModel.onStartConversation(it)
        }
    }

    override fun getLiveCount(response: String?) {
        runOnUiThread {
            val res = Gson().fromJson(response, LSCLiveUpdatesResponse::class.java)
            liveViewCount.text = (res.liveBroadcast.watchersCount).toString()
        }

    }

    override fun onMessageCreated(response: String?) {
        runOnUiThread {
            Log.d("onMessageCreated", response.toString())
            val res = Gson().fromJson(response, MessageResponse::class.java)
            val commentData = MessageCommentData()
            commentData.body = res.message.body
            commentData.userName = res.message.owner.displayName
            commentData.id = res.message.id
            commentListData.add(commentData)
            val listOfMessaged = LinkedHashSet(commentListData)
            commentListData.clear()
            commentListData.addAll(listOfMessaged)
            lscCommentListAdapter.setListData(commentListData)
            lscCommentListAdapter.notifyDataSetChanged()
            commentRecyclerView.smoothScrollToPosition(commentListData.size - 1)
        }
    }

    override fun onLSCReactionsAdded(response: String?) {
        runOnUiThread {
            val res = Gson().fromJson(response, LSCLiveReactionsResponse::class.java)
            when (res.reaction.type) {
                LiveBroadcasterConstants.ANGRY -> {
                    onFlyReactions(R.drawable.angry)
                }
                LiveBroadcasterConstants.THANK_YOU -> {
                    onFlyReactions(R.drawable.thankyou)
                }
                LiveBroadcasterConstants.WOW -> {
                    onFlyReactions(R.drawable.wow)
                }
                LiveBroadcasterConstants.SAD -> {
                    onFlyReactions(R.drawable.sad)
                }
                LiveBroadcasterConstants.SMILEY -> {
                    onFlyReactions(R.drawable.smiley)
                }
                LiveBroadcasterConstants.CLAP -> {
                    onFlyReactions(R.drawable.clapping)
                }
                LiveBroadcasterConstants.LIKE -> {
                    onFlyReactions(R.drawable.like)
                }
                LiveBroadcasterConstants.LAUGH -> {
                    onFlyReactions(R.drawable.laugh)
                }
                LiveBroadcasterConstants.HEART -> {
                    onFlyReactions(R.drawable.heart)
                }
                LiveBroadcasterConstants.INSIGHT -> {
                    onFlyReactions(R.drawable.angry)
                }
                LiveBroadcasterConstants.DISLIKE -> {
                    onFlyReactions(R.drawable.dislike)
                }
            }
        }

    }

    private fun onFlyReactions(resId: Int) {
        val animation = ZeroGravityAnimation()
        animation.setCount(1)
        animation.setScalingFactor(0.2f)
        animation.setOriginationDirection(Direction.BOTTOM)
        animation.setDestinationDirection(Direction.TOP)
        animation.setImage(resId)
        animation.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
        }
        )
        val container: ViewGroup = findViewById(R.id.animatorLoader)
        animation.play(this, container)
    }

    override fun onStop() {
        Log.d("OnStop", "called")
        super.onStop()
    }

    override fun onBackPressed() {
        Log.d("onBackPressed", "onBackPressed")
        if (isLive)
            showCancelLiveBroadCastFragment()
        else {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("onDestroy", "onDestroy")
        leaveLSCBroadcast()
        if (::countDownTimer.isInitialized)
            countDownTimer.cancel()
        if (::mRtcEngine.isInitialized)
            mRtcEngine.leaveChannel()
        RtcEngine.destroy()
    }

    fun leaveLSCBroadcast() {
        try {
            Channelize.getInstance()
                .removeSubscriberTopic("live_broadcasts/$broadCastId/start_watching")
            Channelize.getInstance()
                .removeSubscriberTopic("live_broadcasts/$broadCastId/stop_watching")
            Channelize.getInstance()
                .removeSubscriberTopic("live_broadcasts/$broadCastId/reaction_added")
            Channelize.getInstance()
                .removeSubscriberTopic("conversations/$conversationId/message_created")
        } catch (e: Exception) {
            Log.d("Tag", e.toString())
        }
    }
}