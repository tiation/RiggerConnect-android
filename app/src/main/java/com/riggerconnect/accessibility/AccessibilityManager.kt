package com.riggerconnect.accessibility

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat

class AccessibilityManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("accessibility_prefs", Context.MODE_PRIVATE)
    
    companion object {
        const val KEY_HIGH_CONTRAST = "high_contrast_mode"
        const val KEY_FONT_SCALE = "font_scale"
        const val DEFAULT_FONT_SCALE = 1.0f
    }
    
    // High Contrast Mode
    fun isHighContrastEnabled(): Boolean {
        return prefs.getBoolean(KEY_HIGH_CONTRAST, false)
    }
    
    fun setHighContrastMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HIGH_CONTRAST, enabled).apply()
        // Trigger theme refresh in the app
    }
    
    // Font Scaling
    fun getFontScale(): Float {
        return prefs.getFloat(KEY_FONT_SCALE, DEFAULT_FONT_SCALE)
    }
    
    fun setFontScale(scale: Float) {
        prefs.edit().putFloat(KEY_FONT_SCALE, scale).apply()
        // Trigger font size refresh in the app
    }
    
    // TalkBack Support Helper
    fun setAccessibilityDescription(view: View, description: String) {
        view.contentDescription = description
        view.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
    }
    
    // Dynamic Font Size Helper
    fun applyFontScale(textView: TextView) {
        val currentSize = textView.textSize
        textView.textSize = currentSize * getFontScale()
    }
    
    // Apply High Contrast Colors
    fun applyHighContrastIfEnabled(view: View) {
        if (isHighContrastEnabled()) {
            when (view) {
                is TextView -> {
                    view.setTextColor(ContextCompat.getColor(context, R.color.high_contrast_text))
                }
                else -> {
                    view.setBackgroundColor(ContextCompat.getColor(context, R.color.high_contrast_background))
                }
            }
        }
    }
    
    // Voice Control Support (Basic Implementation)
    fun setupVoiceControl(view: View, action: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            view.accessibilityDelegate = object : View.AccessibilityDelegate() {
                override fun performAccessibilityAction(host: View, action: Int, args: Bundle?): Boolean {
                    if (action == AccessibilityNodeInfo.ACTION_CLICK) {
                        action()
                        return true
                    }
                    return super.performAccessibilityAction(host, action, args)
                }
            }
        }
    }
    
    // Gesture Alternative Support
    fun setupGestureAlternative(view: View, onTap: () -> Unit, onDoubleTap: () -> Unit) {
        val gestureDetector = GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                onTap()
                return true
            }
            
            override fun onDoubleTap(e: MotionEvent): Boolean {
                onDoubleTap()
                return true
            }
        })
        
        view.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }
}
