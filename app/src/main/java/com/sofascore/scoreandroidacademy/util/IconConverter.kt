package com.sofascore.scoreandroidacademy.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.core.content.ContextCompat

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

    }

}