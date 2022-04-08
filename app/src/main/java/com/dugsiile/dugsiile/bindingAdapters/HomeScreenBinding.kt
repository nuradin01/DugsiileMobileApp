package com.dugsiile.dugsiile.bindingAdapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.dugsiile.dugsiile.models.Student
import com.dugsiile.dugsiile.util.NetworkResult

class HomeScreenBinding {

    companion object {

        @BindingAdapter("readApiResponse")
        @JvmStatic
        fun handleReadDataErrors(
            view: View,
            apiResponse: NetworkResult<Student>?,
        ){
            when (view){
                is ImageView ->{
                    view.isVisible = apiResponse is NetworkResult.Error
                }
                is TextView ->{
                    view.isVisible = apiResponse is NetworkResult.Error
                    view.text = apiResponse?.message.toString()
                }
            }
        }
    }
}