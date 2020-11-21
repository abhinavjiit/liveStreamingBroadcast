package com.livestreaming.channelize.io.activity.lscSettingUp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.livestreaming.channelize.io.BaseApplication
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.activity.BaseActivity
import com.livestreaming.channelize.io.fragment.LSCPermissionFragment
import com.livestreaming.channelize.io.fragment.LSCSettingUpFragment
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

class LSCBroadCastSettingUpActivity : BaseActivity(), View.OnClickListener {

    private lateinit var broadCasterContainer: RelativeLayout
    private lateinit var mLocalVideo: VideoCanvas
    private lateinit var mRtcEngine: RtcEngine
    private var networkQuality = -1
    private lateinit var beautification: ImageView
    private val beautificationValues: BeautificationCustomizationValuesClassHolder by lazy {
        BeautificationCustomizationValuesClassHolder()
    }
    private val mEventHandler: IRtcEngineEventHandler by lazy {
        object : IRtcEngineEventHandler() {

            override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                Log.d("onJoinChannelSuccess", "onJoinChannelSuccess")

            }

            override fun onFirstLocalVideoFrame(width: Int, height: Int, elapsed: Int) {
             runOnUiThread {
                 beautification.visibility = View.VISIBLE
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
        broadCasterContainer = findViewById(R.id.broadCasterContainer)
        beautification = findViewById(R.id.beautification)
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

        beautification.setOnClickListener(this)

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
        removeFragment(fragment)
        val progress = progressDialog(this)
        progress.show()
        CoroutineScope(Dispatchers.Main).launch {
            mRtcEngine.joinChannel(null, "abhinav", "", 0)
            delay(2000)
            progress.dismiss()
            val progress2 = progressDialog(
                this@LSCBroadCastSettingUpActivity,
                onlyText = true,
                text = "You are live now"
            )
            progress2.show()
            delay(1000)
            progress2.dismiss()
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
                "2336dbc90d61465ebce940642b537ba4",
                mEventHandler
            )
            mRtcEngine.enableLastmileTest()
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
            mRtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)

            changeBeautification(beautificationValues)
        } catch (e: Exception) {
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
        mRtcEngine.leaveChannel()
        RtcEngine.destroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.beautification) {
            val beautificationView = LSCBeautificationBottomSheetDialogFragment()
            beautificationView.show(supportFragmentManager, "beauty_sheet")
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

}