package com.dugsiile.dugsiile.bindingAdapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import coil.load
import com.dugsiile.dugsiile.R
import com.dugsiile.dugsiile.models.FeeData
import com.dugsiile.dugsiile.util.Constants.Companion.BASE_URL
import com.google.android.material.imageview.ShapeableImageView

class StudentsRowBinding {
    companion object {
        @BindingAdapter("loadImageFromUrl")
        @JvmStatic
        fun loadImageFromUrl(imageView: ImageView, imageUrl: String) {
            imageView.load("$BASE_URL/$imageUrl") {
                crossfade(600)
                error(R.drawable.ic_baseline_person_gray)
            }
        }

        @BindingAdapter("applyTickColor")
        @JvmStatic
        fun applyTickColor(imageView: ImageView, fees: List<FeeData>) {
            if (fees.isEmpty()) {
              imageView.setColorFilter(
                        ContextCompat.getColor(
                            imageView.context,
                            R.color.primary_500
                        )
                    )

            }
        }
    }
}
