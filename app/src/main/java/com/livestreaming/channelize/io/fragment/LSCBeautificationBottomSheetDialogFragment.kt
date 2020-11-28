package com.livestreaming.channelize.io.fragment

import android.content.DialogInterface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.Slider
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.BeautificationCustomizationValuesClassHolder
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.LSCBroadCastSettingUpAndLiveActivity

class LSCBeautificationBottomSheetDialogFragment : BottomSheetDialogFragment(),
    View.OnClickListener {

    private lateinit var low: TextView
    private lateinit var normal: TextView
    private lateinit var high: TextView
    private lateinit var lightnessSlider: Slider
    private lateinit var smoothnessSlider: Slider
    private lateinit var rednessSlider: Slider
    private lateinit var cancelBeautificationBottomSheet: ImageView
    private lateinit var muteVideoSwitch: SwitchCompat
    private var contrastValue = 2
    private lateinit var beautificationCustomizationValuesClassHolder: BeautificationCustomizationValuesClassHolder


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.bottom_sheet_beautification_customization, container, false)
        low = view.findViewById(R.id.low)
        normal = view.findViewById(R.id.normal)
        lightnessSlider = view.findViewById(R.id.lightnessSlider)
        smoothnessSlider = view.findViewById(R.id.smoothnessSlider)
        rednessSlider = view.findViewById(R.id.rednessSlider)
        cancelBeautificationBottomSheet = view.findViewById(R.id.cancelBeautificationBottomSheet)
        muteVideoSwitch = view.findViewById(R.id.muteVideoSwitch)
        high = view.findViewById(R.id.high)
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

        low.setOnClickListener(this)
        high.setOnClickListener(this)
        normal.setOnClickListener(this)
        cancelBeautificationBottomSheet.setOnClickListener { dismiss() }
        return view

    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }


    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.low -> {
                contrastValue = 0
                beautificationCustomizationValuesClassHolder.contrastValue = 0
                lowContrastEnabled()
            }
            R.id.high -> {
                contrastValue = 2
                beautificationCustomizationValuesClassHolder.contrastValue = 2
                highContrastEnabled()
            }
            R.id.normal -> {
                contrastValue = 1
                beautificationCustomizationValuesClassHolder.contrastValue = 1
                normalContrastEnabled()
            }

        }
        changeBeautification()
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
        val lowButtonBackground: GradientDrawable = low.background as GradientDrawable
        lowButtonBackground.setColor(ContextCompat.getColor(activity!!, R.color.white))
        lowButtonBackground.setTint(ContextCompat.getColor(activity!!, R.color.white))

        val highButtonBackground: GradientDrawable = high.background as GradientDrawable
        highButtonBackground.setColor(
            ContextCompat.getColor(
                activity!!,
                R.color.grey_light
            )
        )
        highButtonBackground.setTint(ContextCompat.getColor(activity!!, R.color.grey_light))

        val normalButtonBackground: GradientDrawable =
            normal.background as GradientDrawable
        normalButtonBackground.setColor(
            ContextCompat.getColor(
                activity!!,
                R.color.grey_light
            )
        )
        normalButtonBackground.setTint(ContextCompat.getColor(activity!!, R.color.grey_light))

    }


    private fun highContrastEnabled() {
        val lowButtonBackground: GradientDrawable =
            low.background as GradientDrawable
        lowButtonBackground.setColor(ContextCompat.getColor(activity!!, R.color.grey_light))
        lowButtonBackground.setTint(ContextCompat.getColor(activity!!, R.color.grey_light))

        val highButtonBackground: GradientDrawable = high.background as GradientDrawable
        highButtonBackground.setColor(
            ContextCompat.getColor(activity!!, R.color.white)

        )
        highButtonBackground.setTint(ContextCompat.getColor(activity!!, R.color.white))

        val normalButtonBackground: GradientDrawable = normal.background as GradientDrawable
        normalButtonBackground.setColor(
            ContextCompat.getColor(activity!!, R.color.grey_light)
        )
        normalButtonBackground.setTint(ContextCompat.getColor(activity!!, R.color.grey_light))
    }


    private fun normalContrastEnabled() {
        val lowButtonBackground: GradientDrawable =
            low.background as GradientDrawable
        lowButtonBackground.setColor(ContextCompat.getColor(activity!!, R.color.grey_light))
        lowButtonBackground.setTint(ContextCompat.getColor(activity!!, R.color.grey_light))
        val highButtonBackground: GradientDrawable =
            high.background as GradientDrawable
        highButtonBackground.setColor(
            ContextCompat.getColor(
                activity!!,
                R.color.grey_light
            )
        )
        highButtonBackground.setTint(ContextCompat.getColor(activity!!, R.color.grey_light))
        val normalButtonBackground: GradientDrawable =
            normal.background as GradientDrawable
        normalButtonBackground.setColor(
            ContextCompat.getColor(
                activity!!,
                R.color.white
            )
        )
        normalButtonBackground.setTint(ContextCompat.getColor(activity!!, R.color.white))
    }


}