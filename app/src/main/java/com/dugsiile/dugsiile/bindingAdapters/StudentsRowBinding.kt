package com.dugsiile.dugsiile.bindingAdapters

import android.content.res.Resources
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import coil.load
import com.dugsiile.dugsiile.R
import com.dugsiile.dugsiile.models.FeeData
import com.dugsiile.dugsiile.models.StudentData
import com.dugsiile.dugsiile.ui.HomeFragmentDirections
import com.dugsiile.dugsiile.util.Constants.Companion.BASE_URL
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.transition.MaterialElevationScale
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
                    val studentCardDetailTransitionName = studentRowLayout.context.getString(R.string.student_card_detail_transition_name)
                    val extras = FragmentNavigatorExtras(studentRowLayout to studentCardDetailTransitionName)
                    val action =
                        HomeFragmentDirections.actionHomeFragmentToStudentDetailsFragment(student)
                    studentRowLayout.findNavController().navigate(action, extras)
                } catch (e: Exception) {
                    Log.d("onStudentClickListener", e.toString())
                }
            }
        }


        @BindingAdapter("loadImageFromUrl")
        @JvmStatic
        fun loadImageFromUrl(imageView: ImageView, imageUrl: String) {
            imageView.load(imageUrl) {
                crossfade(600)
                error(R.drawable.ic_person_round)
            }
        }

        @BindingAdapter("showTickIcon")
        @JvmStatic

        fun showTickIcon(view: View, fees: List<FeeData>) {
            if (fees.isEmpty()) {
              when(view) {
                  is ImageView -> view.visibility = View.VISIBLE
                  is TextView -> view.visibility = View.GONE
              }

            } else {
                when(view) {
                    is ImageView -> view.visibility = View.GONE
                    is TextView -> {
                        view.visibility = View.VISIBLE
                        view.text = "$${fees.map { it.amountCharged }.reduce { acc, fee -> acc+fee }}"
                    }
                }
            }
        }


        @BindingAdapter("feeName")
        @JvmStatic
        fun feeName(textView: TextView, fee: FeeData) {
            val chargedAtFormated = SimpleDateFormat("MMMM, yyyy").format(fee.chargedAt)
            textView.text = "$${fee.amountCharged} of $chargedAtFormated"
        }

        @BindingAdapter("studentName")
        @JvmStatic
        fun studentName(textView: TextView, name: String) {
            if (name.length >30){
            textView.text = "${name.substring(0,30)} ..."
            } else {
            textView.text = name

            }
        }


}}


