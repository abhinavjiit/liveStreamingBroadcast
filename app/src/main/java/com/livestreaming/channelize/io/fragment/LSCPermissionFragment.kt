
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
import kotlinx.android.synthetic.main.fragment_permission_lsc.view.*

const val RECORD_REQUEST_CODE = 101
const val CAMERA_REQUEST_CODE = 102
const val CAMERA = "camera"
const val MICROPHONE = "microPhone"

class LSCPermissionFragment : BaseFragment() {

    private var cameraPermission = false
    private var microPhonePermission = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_permission_lsc, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { activity ->
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                setDrawables(tvCamera)
                tvCamera.text = context?.resources?.getString(R.string.camera_enabled_string)
                tvCamera.isEnabled = false
                tvCamera.isClickable = false
            }
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                setDrawables(tvMicrophone)
                tvMicrophone.text = context?.resources?.getString(R.string.microphone_enable_string)
                tvMicrophone.isEnabled = false
                tvMicrophone.isClickable = false
            }
        }
        view.tvMicrophone.setOnClickListener {
            showMicroPhonePermission()
        }
        view.tvCamera.setOnClickListener {
            showCameraPermission()
        }
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
                    showPermissionDialog(
                        msg = activity.resources.getString(R.string.camera_permission_required),
                        title = activity.resources.getString(R.string.permission_required_title_string),
                        permissionType = CAMERA
                    )
                } else {
                    makeRequest(activity, CAMERA)
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
                    showPermissionDialog(
                        msg = activity.resources.getString(R.string.microaPhonr_permission_required_string),
                        title = activity.resources.getString(R.string.permission_required_title_string),
                        permissionType = MICROPHONE
                    )
                } else {
                    makeRequest(activity, MICROPHONE)
                }
            }
        }
    }

    private fun makeRequest(context: FragmentActivity, type: String) {
        when (type) {
            CAMERA -> {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_REQUEST_CODE
                )
            }
            MICROPHONE -> {
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
                            setDrawables(tvCamera)
                            tvCamera.text =
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
                            setDrawables(tvMicrophone)
                            tvMicrophone.text =
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
            gradientDrawable.mutate()
            gradientDrawable.setColor(ContextCompat.getColor(activity, R.color.white))
            container.setTextColor(ContextCompat.getColor(activity, R.color.app_red))
        }
    }

    private fun showPermissionDialog(msg: String, title: String, permissionType: String) {
        activity?.let { activity ->
            val builder = AlertDialog.Builder(activity)
            builder.setMessage(msg)
                .setTitle(title)
            builder.setPositiveButton(
                "OK"
            ) { _, _ ->
                Log.i("TAG", "Clicked")
                makeRequest(activity, permissionType)
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

}


