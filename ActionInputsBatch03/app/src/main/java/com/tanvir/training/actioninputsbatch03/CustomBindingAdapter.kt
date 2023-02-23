package com.tanvir.training.actioninputsbatch03

import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("app:setFavorite")
fun setFavorite(imageView: ImageView, favorite: Boolean) {
    when(favorite) {
        true -> imageView.setImageResource(R.drawable.ic_baseline_favorite_24_red)
        false -> imageView.setImageResource(R.drawable.ic_baseline_favorite_24_grey)
    }
}