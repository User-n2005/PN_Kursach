package com.example.kursachpr.ui.components

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.kursachpr.R

object FoxToast {
    fun show(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        val toast = Toast(context)
        
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(32, 24, 32, 24)
            background = context.getDrawable(R.drawable.toast_background)
            gravity = Gravity.CENTER_VERTICAL
        }
        
        val imageView = ImageView(context).apply {
            setImageResource(R.drawable.fox_logo)
            val size = (48 * context.resources.displayMetrics.density).toInt()
            layoutParams = LinearLayout.LayoutParams(size, size).apply {
                marginEnd = (16 * context.resources.displayMetrics.density).toInt()
            }
        }
        
        val textView = TextView(context).apply {
            text = message
            setTextColor(context.getColor(android.R.color.white))
            textSize = 14f
            maxWidth = (250 * context.resources.displayMetrics.density).toInt()
        }
        
        layout.addView(imageView)
        layout.addView(textView)
        
        toast.view = layout
        toast.duration = duration
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 100)
        toast.show()
    }
}
