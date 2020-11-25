package com.livestreaming.channelize.io.activity.lscSettingUp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
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
import com.google.gson.Gson
import com.livestreaming.channelize.io.*
import com.livestreaming.channelize.io.activity.BaseActivity
import com.livestreaming.channelize.io.adapter.LSCCommentListAdapter
import com.livestreaming.channelize.io.fragment.LSCBeautificationBottomSheetDialogFragment
import com.livestreaming.channelize.io.fragment.LSCPermissionFragment
import com.livestreaming.channelize.io.fragment.LSCProductsListDialogFragment
import com.livestreaming.channelize.io.fragment.LSCSettingUpFragment
import com.livestreaming.channelize.io.model.MessageCommentData
import com.livestreaming.channelize.io.model.lscLiveUpdatedModel.LSCLiveUpdatesResponse
import com.livestreaming.channelize.io.model.productdetailModel.ProductDetailResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.BeautyOptions
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class LSCBroadCastSettingUpAndLiveActivity : BaseActivity(), View.OnClickListener,
    ChannelizeConversationEventHandler {

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
    private lateinit var lscBroadCastViewModel: LSCBroadCastViewModel
    private lateinit var post: TextView
    private lateinit var bottomContainer: ConstraintLayout
    private var startTime: String? = null
    private var endTime: String? = null
    private var productsList: ArrayList<ProductDetailResponse>? = null
    private var broadCastId: String? = null
    private val commentData: MessageCommentData by lazy {
        MessageCommentData()
    }
    private var commentListData = ArrayList<MessageCommentData>()

    private val lscCommentListAdapter: LSCCommentListAdapter by lazy {
        LSCCommentListAdapter()
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
                    Log.d("onJoinChannelSuccess", "onJoinChannelSuccess")
                    Channelize.getInstance().broadCastId = broadCastId
                    Channelize.getInstance().updateConnectStatus(true)
                    Channelize.getInstance()
                        .addSubscriber("live_broadcasts/$broadCastId/start_watching")
                    Channelize.getInstance()
                        .addSubscriber("live_broadcasts/$broadCastId/stop_watching")
                    lscRemainingTime()
                }
            }

            override fun onFirstLocalVideoFrame(width: Int, height: Int, elapsed: Int) {
                runOnUiThread {
                    beautification.visibility = View.VISIBLE
                    liveTextView.visibility = View.VISIBLE
                    cancelLiveBroadcast.visibility = View.VISIBLE
                    liveViewCount.visibility = View.VISIBLE
                    productListView.visibility = View.VISIBLE
                    writeSomethingEditText.visibility = View.VISIBLE
                    bottomContainer.visibility = View.VISIBLE
                    commentRecyclerView.visibility = View.VISIBLE
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
        setContentView(R.layout.activity_lsc_broadcast_setting_up)
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
        broadCastId = intent?.getStringExtra("broadCastId")
        startTime = intent?.getStringExtra("startTime")
        endTime = intent?.getStringExtra("endTime")
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
        beautification.setOnClickListener(this)
        productListView.setOnClickListener(this)
        post.setOnClickListener(this)
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


    fun settingUpFragment() {
        val fragment = supportFragmentManager.findFragmentByTag("LSCPermissionFragment")
        if (fragment != null && fragment is LSCPermissionFragment) {
            removeFragment(fragment)
        }
        val settingFragment = LSCSettingUpFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.content_frame, settingFragment, "LSCSettingUpFragment").addToBackStack(null)
            .commit()
    }

    private fun removeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().remove(fragment).commit()
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
                mRtcEngine.joinChannel(null, broadCastId, "", 0)
                delay(2000)
                progress.dismiss()
                val progress2 = progressDialog(
                    this@LSCBroadCastSettingUpAndLiveActivity,
                    onlyText = true,
                    text = "You are live now"
                )
                progress2.show()
                delay(1000)
                progress2.dismiss()
            }
        } catch (e: Exception) {

        }
    }


    override fun onStart() {
        super.onStart()
        initRTCEngine()
        initLocalVideoConfig()

    }


    private fun initRTCEngine() {
        try {
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
            initRTCEngine()
            throw RuntimeException("Need to check RTC sdk init fatal")

        }

    }

    private fun setupVideoConfig() {
        mRtcEngine.enableVideo()
        mRtcEngine.setVideoEncoderConfiguration(
            VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
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

    override fun onStop() {
        Log.d("OnStop", "called")
        super.onStop()

    }

    override fun onDestroy() {
        super.onDestroy()
        Channelize.getInstance()
            .removeSubscriberTopic("live_broadcasts/$broadCastId/start_watching")
        Channelize.getInstance().removeSubscriberTopic("live_broadcasts/$broadCastId/stop_watching")
        mRtcEngine.leaveChannel()
        RtcEngine.destroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
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
                }

            }
        }
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
        lscBroadCastViewModel.getProductItems().observe(this, Observer {
            when (it.status) {
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

    private fun setupViewModel() {
        lscBroadCastViewModel = ViewModelProvider(
            this,
            lscBroadcastAndLiveViewModelFact
        ).get(LSCBroadCastViewModel::class.java)
    }


    private fun sendCommentData(message: String) {
        commentData.id = UUID.randomUUID().toString()
        commentData.body = message + UUID.randomUUID().toString()
        commentData.conversationId = ""
        commentListData.add(commentData)
        commentListData.reverse()
        lscCommentListAdapter.setListData(commentListData)
        lscCommentListAdapter.notifyDataSetChanged()
        commentRecyclerView.smoothScrollToPosition(commentListData.size - 1)
        lscBroadCastViewModel.postComment(commentData).observe(this, Observer {

        })
    }

    fun getProductsListData(): ArrayList<ProductDetailResponse>? {
        return productsList
    }


    override fun getLiveCount(response: String?) {
        runOnUiThread {
            val res = Gson().fromJson(response, LSCLiveUpdatesResponse::class.java)
            liveViewCount.text = (res.liveBroadcast.watchersCount).toString()
        }

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

            totalTimeLeft.text = "${differenceInHours}h${differenceInMinutes}m"
            val countDownTimer = object : CountDownTimer(differenceInTime, 1000) {
                override fun onFinish() {
                    Log.d("Tag", "aaaa")
                }

                override fun onTick(millisUntilFinished: Long) {
                    var remainingTime = differenceInTime.minus(millisUntilFinished)


                    val differenceInMinutes1 = ((remainingTime
                            / (1000 * 60))
                            % 60)

                    val differenceInHours1 = ((remainingTime
                            / (1000 * 60 * 60))
                            % 24)


                    remainingTimeCounter.text = "${differenceInMinutes1}m/"


                    Log.d("RESULT", "see")
                }

            }.start()
        }


    }


}