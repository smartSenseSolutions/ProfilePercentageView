package com.ss.profilepercentageviewdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import androidx.databinding.DataBindingUtil
import com.ss.profilepercentageviewdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        activityMainBinding.sliderImgBorderWidth.value = activityMainBinding.ppvProfile.borderWidth.toInt().toFloat()

        activityMainBinding.sliderImgPadding.value = activityMainBinding.ppvProfile.imagePadding.toInt().toFloat()

        activityMainBinding.sliderProgress.addOnChangeListener { slider, value, fromUser ->
            activityMainBinding.ppvProfile.setValue(value.toInt())
        }

        activityMainBinding.sliderArcWidth.addOnChangeListener { slider, value, fromUser ->
            activityMainBinding.ppvProfile.setArcWidth(value)
        }

        activityMainBinding.sliderImgBorderWidth.addOnChangeListener { slider, value, fromUser ->
            activityMainBinding.ppvProfile.borderWidth = value
        }

        activityMainBinding.sliderImgPadding.addOnChangeListener { slider, value, fromUser ->
            activityMainBinding.ppvProfile.imagePadding = value
        }

        activityMainBinding.chkDash.setOnCheckedChangeListener { buttonView, isChecked ->
            activityMainBinding.ppvProfile.setDashEnable(isChecked)
        }

        activityMainBinding.chkRounded.setOnCheckedChangeListener { buttonView, isChecked ->
            activityMainBinding.ppvProfile.setRounded(isChecked)
        }
    }
}