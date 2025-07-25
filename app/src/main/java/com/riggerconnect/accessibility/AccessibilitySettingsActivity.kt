package com.riggerconnect.accessibility

import android.os.Bundle
import android.widget.SeekBar
import android.widget.Switch
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class AccessibilitySettingsActivity : AppCompatActivity() {
    private lateinit var accessibilityManager: AccessibilityManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accessibility_settings)
        
        accessibilityManager = AccessibilityManager(this)
        
        setupHighContrastSwitch()
        setupFontSizeControl()
        setupVoiceControl()
        setupGestureAlternatives()
        setupHelpButton()
    }
    
    private fun setupHighContrastSwitch() {
        val highContrastSwitch = findViewById<Switch>(R.id.switchHighContrast)
        highContrastSwitch.isChecked = accessibilityManager.isHighContrastEnabled()
        
        highContrastSwitch.setOnCheckedChangeListener { _, isChecked ->
            accessibilityManager.setHighContrastMode(isChecked)
            // Recreate activity to apply theme changes
            ActivityCompat.recreate(this)
        }
    }
    
    private fun setupFontSizeControl() {
        val fontSizeSeekBar = findViewById<SeekBar>(R.id.seekBarFontSize)
        val currentScale = accessibilityManager.getFontScale() * 100
        fontSizeSeekBar.progress = currentScale.toInt()
        
        fontSizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val scale = progress / 100f
                    accessibilityManager.setFontScale(scale)
                    // Update text sizes in real-time
                    updateTextSizes()
                }
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }
    
    private fun setupVoiceControl() {
        val voiceControlSwitch = findViewById<Switch>(R.id.switchVoiceControl)
        voiceControlSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Request necessary permissions for voice control
                requestVoicePermissions()
            }
        }
    }
    
    private fun setupGestureAlternatives() {
        val gestureSwitch = findViewById<Switch>(R.id.switchGestureAlternatives)
        gestureSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Enable/disable gesture alternatives throughout the app
            updateGestureAlternatives(isChecked)
        }
    }
    
    private fun setupHelpButton() {
        val helpButton = findViewById<Button>(R.id.btnAccessibilityHelp)
        helpButton.setOnClickListener {
            // Open accessibility help documentation
            startActivity(Intent(this, AccessibilityHelpActivity::class.java))
        }
    }
    
    private fun updateTextSizes() {
        // Recursively update text sizes in the current view hierarchy
        val rootView = window.decorView.findViewById<android.view.View>(android.R.id.content)
        updateTextSizesRecursively(rootView)
    }
    
    private fun updateTextSizesRecursively(view: android.view.View) {
        if (view is android.widget.TextView) {
            accessibilityManager.applyFontScale(view)
        } else if (view is android.view.ViewGroup) {
            for (i in 0 until view.childCount) {
                updateTextSizesRecursively(view.getChildAt(i))
            }
        }
    }
    
    private fun requestVoicePermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(android.Manifest.permission.RECORD_AUDIO),
                VOICE_PERMISSION_REQUEST_CODE
            )
        }
    }
    
    private fun updateGestureAlternatives(enabled: Boolean) {
        // Update app-wide gesture handling
        // This would be implemented in your navigation/gesture handling system
    }
    
    companion object {
        private const val VOICE_PERMISSION_REQUEST_CODE = 1001
    }
}
