package com.livestreaming.channelize.io.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.LSCBroadCastSettingUpAndLiveActivity
import kotlinx.android.synthetic.main.fragment_permission_lsc.*

const val RECORD_REQUEST_CODE = 101
const val CAMERA_REQUEST_CODE = 102

class LSCPermissionFragment : BaseFragment() {

    private var cameraPermission = false
    private var microPhonePermission = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_permission_lsc, container, false)
        activity?.let { activity ->
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                setDrawables(camera)
                camera.text = context?.resources?.getString(R.string.camera_enabled_string)
                camera.isEnabled = false
                camera.isClickable = false
            }
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                setDrawables(microPhone)
                microPhone.text = context?.resources?.getString(R.string.microphone_enable_string)
                microPhone.isEnabled = false
                microPhone.isClickable = false
            }
        }
        microPhone.setOnClickListener {
            showMicroPhonePermission()
        }
        camera.setOnClickListener {
            showCameraPermission()
        }
        return view
    }

    private fun showCameraPermission() {
        activity?.let { activity ->
            val permission = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                Log.i("TAG", "Permission to open Camera denied")
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        Manifest.permission.CAMERA
                    )
                ) {
                    val builder = AlertDialog.Builder(activity)
                    builder.setMessage("Permission to access the camera is required for this app to open camera.")
                        .setTitle("Permission required")

                    builder.setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        Log.i("TAG", "Clicked")
                        makeRequest(activity, "camera")
                    }

                    val dialog = builder.create()
                    dialog.show()
                } else {
                    makeRequest(activity, "camera")
                }
            }
        }
    }

    private fun showMicroPhonePermission() {
        activity?.let { activity ->
            val permission = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.RECORD_AUDIO
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                Log.i("TAG", "Permission to record denied")
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        Manifest.permission.RECORD_AUDIO
                    )
                ) {
                    val builder = AlertDialog.Builder(activity)
                    builder.setMessage("Permission to access the microphone is required for this app to record audio.")
                        .setTitle("Permission required")

                    builder.setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        Log.i("TAG", "Clicked")
                        makeRequest(activity, "microPhone")
                    }
                    val dialog = builder.create()
                    dialog.show()
                } else {
                    makeRequest(activity, "microPhone")
                    makeRequest(activity, "microPhone")
                }
            }
        }
    }

    private fun makeRequest(context: FragmentActivity, type: String) {
        when (type) {
            "camera" -> {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_REQUEST_CODE
                )
            }
            "microPhone" -> {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    RECORD_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        activity?.let {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    cameraPermission =
                        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                            Log.i("TAG", "Permission has been denied by user")
                            false
                        } else {
                            setDrawables(camera)
                            camera.text =
                                context?.resources?.getString(R.string.camera_enabled_string)
                            Log.i("TAG", "Permission has been granted by user")
                            true
                        }
                }
                RECORD_REQUEST_CODE -> {
                    microPhonePermission =
                        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                            Log.i("TAG", "Permission has been denied by user")
                            false
                        } else {
                            Log.i("TAG", "Permission has been granted by user")
                            setDrawables(microPhone)
                            microPhone.text =
                                context?.resources?.getString(R.string.microphone_enable_string)
                            true
                        }
                }
                else -> {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.let { activity ->
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                (activity as LSCBroadCastSettingUpAndLiveActivity).settingUpFragment()
            }
        }
    }

    private fun setDrawables(container: TextView) {
        activity?.let { activity ->
            val gradientDrawable: GradientDrawable =
                container.background as GradientDrawable
            gradientDrawable.setColor(ContextCompat.getColor(activity, R.color.white))
            container.setTextColor(ContextCompat.getColor(activity, R.color.app_red))
        }
    }

}


