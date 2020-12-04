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

class LSCBeautificationBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var contrastValue = 2
    private lateinit var beautificationCustomizationValuesClassHolder: BeautificationCustomizationValuesClassHolder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.bottom_sheet_beautification_customization, container, false)
        beautificationCustomizationValuesClassHolder =
            (activity as LSCBroadCastSettingUpAndLiveActivity).getBeautificationObject()
        smoothnessSlider.value = beautificationCustomizationValuesClassHolder.smoothnessValue
        lightnessSlider.value = beautificationCustomizationValuesClassHolder.lightingValue
        rednessSlider.value = beautificationCustomizationValuesClassHolder.rednessValue
        muteVideoSwitch.isChecked = beautificationCustomizationValuesClassHolder.isEnabled
        setContrastButtonState()
        smoothnessSlider.addOnChangeListener { _, value, _ ->
            beautificationCustomizationValuesClassHolder.smoothnessValue = value
            changeBeautification()
        }
        lightnessSlider.addOnChangeListener { _, value, _ ->
            beautificationCustomizationValuesClassHolder.lightingValue = value
            changeBeautification()
        }
        rednessSlider.addOnChangeListener { _, value, _ ->
            beautificationCustomizationValuesClassHolder.rednessValue = value
            changeBeautification()
        }
        muteVideoSwitch.setOnCheckedChangeListener { _, isChecked ->
            beautificationCustomizationValuesClassHolder.isEnabled = isChecked
            changeBeautification()
        }
        low.setOnClickListener {
            contrastValue = 0
            beautificationCustomizationValuesClassHolder.contrastValue = 0
            lowContrastEnabled()
        }
        high.setOnClickListener {
            contrastValue = 2
            beautificationCustomizationValuesClassHolder.contrastValue = 2
            highContrastEnabled()
        }
        normal.setOnClickListener {
            contrastValue = 1
            beautificationCustomizationValuesClassHolder.contrastValue = 1
            normalContrastEnabled()
        }
        cancelBeautificationBottomSheet.setOnClickListener { dismiss() }
        return view
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
            0 -> {
                lowContrastEnabled()
            }
            1 -> {
                normalContrastEnabled()
            }
            2 -> {
                highContrastEnabled()
            }
        }
    }

    private fun lowContrastEnabled() {
        activity?.let { activity ->
            val lowButtonBackground: GradientDrawable = low.background as GradientDrawable
            lowButtonBackground.setColor(ContextCompat.getColor(activity, R.color.white))
            lowButtonBackground.setTint(ContextCompat.getColor(activity, R.color.white))
            val highButtonBackground: GradientDrawable = high.background as GradientDrawable
            highButtonBackground.setColor(
                ContextCompat.getColor(
                    activity,
                    R.color.grey_light
                )
            )
            highButtonBackground.setTint(ContextCompat.getColor(activity, R.color.grey_light))
            val normalButtonBackground: GradientDrawable =
                normal.background as GradientDrawable
            normalButtonBackground.setColor(
                ContextCompat.getColor(
                    activity,
                    R.color.grey_light
                )
            )
            normalButtonBackground.setTint(ContextCompat.getColor(activity, R.color.grey_light))
        }
    }

    private fun highContrastEnabled() {
        activity?.let { activity ->
            val lowButtonBackground: GradientDrawable =
                low.background as GradientDrawable
            lowButtonBackground.setColor(ContextCompat.getColor(activity, R.color.grey_light))
            lowButtonBackground.setTint(ContextCompat.getColor(activity, R.color.grey_light))

            val highButtonBackground: GradientDrawable = high.background as GradientDrawable
            highButtonBackground.setColor(
                ContextCompat.getColor(activity, R.color.white)
            )
            highButtonBackground.setTint(ContextCompat.getColor(activity, R.color.white))
            val normalButtonBackground: GradientDrawable = normal.background as GradientDrawable
            normalButtonBackground.setColor(
                ContextCompat.getColor(activity, R.color.grey_light)
            )
            normalButtonBackground.setTint(ContextCompat.getColor(activity, R.color.grey_light))
        }
    }


    private fun normalContrastEnabled() {
        activity?.let { activity ->
            val lowButtonBackground: GradientDrawable =
                low.background as GradientDrawable
            lowButtonBackground.setColor(ContextCompat.getColor(activity, R.color.grey_light))
            lowButtonBackground.setTint(ContextCompat.getColor(activity, R.color.grey_light))
            val highButtonBackground: GradientDrawable =
                high.background as GradientDrawable
            highButtonBackground.setColor(
                ContextCompat.getColor(
                    activity,
                    R.color.grey_light
                )
            )
            highButtonBackground.setTint(ContextCompat.getColor(activity, R.color.grey_light))
            val normalButtonBackground: GradientDrawable =
                normal.background as GradientDrawable
            normalButtonBackground.setColor(
                ContextCompat.getColor(
                    activity,
                    R.color.white
                )
            )
            normalButtonBackground.setTint(ContextCompat.getColor(activity, R.color.white))
        }
    }

}