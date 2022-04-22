package com.dugsiile.dugsiile.bindingAdapters

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import coil.load
import com.dugsiile.dugsiile.R
import com.dugsiile.dugsiile.models.FeeData
import com.dugsiile.dugsiile.models.StudentData
import com.dugsiile.dugsiile.ui.HomeFragmentDirections
import com.dugsiile.dugsiile.util.Constants.Companion.BASE_URL
import com.google.android.material.imageview.ShapeableImageView
import java.lang.Exception
import java.text.SimpleDateFormat

class StudentsRowBinding {
    companion object {

        @BindingAdapter("onStudentClickListener")
        @JvmStatic
        fun onStudentClickListener(studentRowLayout: ConstraintLayout, student: StudentData) {
            Log.d("onStudentClickListener", "CALLED")
            studentRowLayout.setOnClickListener {
                try {
                    val action =
                        HomeFragmentDirections.actionHomeFragmentToStudentDetailsFragment(student)
                    studentRowLayout.findNavController().navigate(action)
                } catch (e: Exception) {
                    Log.d("onStudentClickListener", e.toString())
                }
            }
        }


        @BindingAdapter("loadImageFromUrl")
        @JvmStatic
        fun loadImageFromUrl(imageView: ImageView, imageUrl: String) {
            imageView.load("$BASE_URL/$imageUrl") {
                crossfade(600)
                error(R.drawable.ic_person_round)
            }
        }

        @BindingAdapter("applyTickColor")
        @JvmStatic
        fun applyTickColor(imageView: ImageView, fees: List<FeeData>) {
            if (fees.isEmpty()) {
                imageView.visibility = View.VISIBLE

            } else {
                imageView.visibility = View.GONE
            }
        }


    @BindingAdapter("feeName")
    @JvmStatic
    fun feeName(textView: TextView, fee: FeeData) {
        val chargedAtFormated = SimpleDateFormat("MMM, yyyy").format(fee.chargedAt)
        textView.text = "$${fee.amountCharged} of $chargedAtFormated"
    }


}}


