package com.livestreaming.channelize.io.fragment

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.BeautificationCustomizationValuesClassHolder
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.LSCBroadCastSettingUpAndLiveActivity
import kotlinx.android.synthetic.main.bottom_sheet_beautification_customization.*

const val LOW_CONTRAST = 0
const val NORMAL_CONTRAST = 1
const val HIGH_CONTRAST = 2

class LSCBeautificationBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var contrastValue = HIGH_CONTRAST
    private lateinit var beautificationCustomizationValuesClassHolder: BeautificationCustomizationValuesClassHolder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_beautification_customization, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        beautificationCustomizationValuesClassHolder =
            (activity as LSCBroadCastSettingUpAndLiveActivity).getBeautificationObject()
        slrSmoothness.value = beautificationCustomizationValuesClassHolder.smoothnessValue
        slrLightness.value = beautificationCustomizationValuesClassHolder.lightingValue
        slrRedness.value = beautificationCustomizationValuesClassHolder.rednessValue
        swhMuteVideo.isChecked = beautificationCustomizationValuesClassHolder.isEnabled
        setContrastButtonState()
        slrSmoothness.addOnChangeListener { _, value, _ ->
            beautificationCustomizationValuesClassHolder.smoothnessValue = value
            changeBeautification()
        }
        slrLightness.addOnChangeListener { _, value, _ ->
            beautificationCustomizationValuesClassHolder.lightingValue = value
            changeBeautification()
        }
        slrRedness.addOnChangeListener { _, value, _ ->
            beautificationCustomizationValuesClassHolder.rednessValue = value
            changeBeautification()
        }
        swhMuteVideo.setOnCheckedChangeListener { _, isChecked ->
            beautificationCustomizationValuesClassHolder.isEnabled = isChecked
            changeBeautification()
        }
        tvLow.setOnClickListener {
            contrastValue = LOW_CONTRAST
            beautificationCustomizationValuesClassHolder.contrastValue = LOW_CONTRAST
            lowContrastEnabled()
        }
        tvHigh.setOnClickListener {
            contrastValue = HIGH_CONTRAST
            beautificationCustomizationValuesClassHolder.contrastValue = HIGH_CONTRAST
            highContrastEnabled()
        }
        tvNormal.setOnClickListener {
            contrastValue = NORMAL_CONTRAST
            beautificationCustomizationValuesClassHolder.contrastValue = NORMAL_CONTRAST
            normalContrastEnabled()
        }
        ivCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    private fun changeBeautification() {
        (activity as LSCBroadCastSettingUpAndLiveActivity).changeBeautification(
            beautificationCustomizationValuesClassHolder
        )
    }

    private fun setContrastButtonState() {
        when (beautificationCustomizationValuesClassHolder.contrastValue) {
            LOW_CONTRAST -> {
                lowContrastEnabled()
            }
            NORMAL_CONTRAST -> {
                normalContrastEnabled()
            }
            HIGH_CONTRAST -> {
                highContrastEnabled()
            }
        }
    }

    private fun lowContrastEnabled() {
        activity?.let { activity ->
            lowContrastDrawable(ContextCompat.getColor(activity, R.color.white))
            highContrastDrawable(ContextCompat.getColor(activity, R.color.grey_light))
            normalContrastDrawable(ContextCompat.getColor(activity, R.color.grey_light))
        }
    }

    private fun highContrastEnabled() {
        activity?.let { activity ->
            lowContrastDrawable(ContextCompat.getColor(activity, R.color.grey_light))
            highContrastDrawable(ContextCompat.getColor(activity, R.color.white))
            normalContrastDrawable(ContextCompat.getColor(activity, R.color.grey_light))
        }
    }

    private fun normalContrastEnabled() {
        activity?.let { activity ->
            lowContrastDrawable(ContextCompat.getColor(activity, R.color.grey_light))
            highContrastDrawable(ContextCompat.getColor(activity, R.color.grey_light))
            normalContrastDrawable(ContextCompat.getColor(activity, R.color.white))
        }
    }

    private fun lowContrastDrawable(color: Int) {
        val lowButtonBackground = tvLow.background as GradientDrawable
        lowButtonBackground.mutate()
        lowButtonBackground.setColor(color)
        lowButtonBackground.setTint(color)
    }

    private fun highContrastDrawable(color: Int) {
        val highButtonBackground = tvHigh.background as GradientDrawable
        highButtonBackground.mutate()
        highButtonBackground.setColor(color)
        highButtonBackground.setTint(color)
    }

    private fun normalContrastDrawable(color: Int) {
        val normalButtonBackground = tvNormal.background as GradientDrawable
        normalButtonBackground.mutate()
        normalButtonBackground.setColor(color)
        normalButtonBackground.setTint(color)
    }

}
