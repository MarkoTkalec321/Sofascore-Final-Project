package com.sofascore.scoreandroidacademy.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue
import android.widget.ImageView
import androidx.core.content.ContextCompat
import coil.load
import java.io.ByteArrayInputStream

class IconConverter {
    companion object {
        fun dpToPx(context: Context, dp: Int): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                context.resources.displayMetrics
            ).toInt()
        }

        fun resizeIcon(context: Context, iconId: Int, width: Int, height: Int): Drawable? {
            val drawable = ContextCompat.getDrawable(context, iconId) ?: return null
            val bitmap = (drawable as BitmapDrawable).bitmap
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
            return BitmapDrawable(context.resources, resizedBitmap)
        }
        fun ImageView.loadImageFromByteArray(imageBytes: ByteArray?) {
            imageBytes?.let {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                if (bitmap != null) {
                    this.setImageBitmap(bitmap)
                } else {
                    Log.e("LoadImage", "Failed to convert byte array to Bitmap.")
                }
            } ?: Log.e("LoadImage", "Image byte array is null.")
        }
    }

}