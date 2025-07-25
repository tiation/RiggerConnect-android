package com.riggerconnect.monitoring

import android.content.Context
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.performance.FirebasePerformance
import com.google.firebase.performance.metrics.Trace

/**
 * Centralized monitoring and crash reporting manager for RiggerConnect
 * 
 * Integrates Firebase Crashlytics, Analytics, and Performance monitoring
 * Built by Jack Jonas (WA rigger) & Tia (dev, ChaseWhiteRabbit NGO)
 * to serve riggers and fund social impact through a smart, practical SaaS
 */
class CrashReportingManager private constructor() {
    
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var crashlytics: FirebaseCrashlytics
    private lateinit var performance: FirebasePerformance
    
    companion object {
        @Volatile
        private var INSTANCE: CrashReportingManager? = null
        
        fun getInstance(): CrashReportingManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CrashReportingManager().also { INSTANCE = it }
            }
        }
        
        private const val TAG = "CrashReportingManager"
    }
    
    fun initialize(context: Context) {
        try {
            firebaseAnalytics = Firebase.analytics
            crashlytics = FirebaseCrashlytics.getInstance()
            performance = FirebasePerformance.getInstance()
            
            // Enable crash reporting collection
            crashlytics.setCrashlyticsCollectionEnabled(true)
            
            // Set custom keys for better crash analysis
            crashlytics.setCustomKey("app_version", getAppVersion(context))
            crashlytics.setCustomKey("device_model", android.os.Build.MODEL)
            crashlytics.setCustomKey("android_version", android.os.Build.VERSION.RELEASE)
            
            Log.d(TAG, "Crash reporting and monitoring initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize crash reporting", e)
        }
    }
    
    /**
     * Log non-fatal exceptions for monitoring
     */
    fun logException(exception: Throwable, message: String? = null) {
        try {
            message?.let { crashlytics.log(it) }
            crashlytics.recordException(exception)
            Log.w(TAG, "Non-fatal exception logged: ${exception.message}", exception)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to log exception", e)
        }
    }
    
    /**
     * Set user identifier for crash reports
     */
    fun setUserId(userId: String) {
        try {
            crashlytics.setUserId(userId)
            firebaseAnalytics.setUserId(userId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set user ID", e)
        }
    }
    
    /**
     * Set custom user properties
     */
    fun setUserProperty(key: String, value: String) {
        try {
            crashlytics.setCustomKey(key, value)
            firebaseAnalytics.setUserProperty(key, value)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set user property", e)
        }
    }
    
    /**
     * Log custom events for analytics
     */
    fun logEvent(eventName: String, parameters: Map<String, Any>? = null) {
        try {
            val bundle = android.os.Bundle()
            parameters?.forEach { (key, value) ->
                when (value) {
                    is String -> bundle.putString(key, value)
                    is Int -> bundle.putInt(key, value)
                    is Long -> bundle.putLong(key, value)
                    is Double -> bundle.putDouble(key, value)
                    is Boolean -> bundle.putBoolean(key, value)
                    else -> bundle.putString(key, value.toString())
                }
            }
            firebaseAnalytics.logEvent(eventName, bundle)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to log event: $eventName", e)
        }
    }
    
    /**
     * Start performance trace
     */
    fun startTrace(traceName: String): Trace? {
        return try {
            val trace = performance.newTrace(traceName)
            trace.start()
            trace
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start trace: $traceName", e)
            null
        }
    }
    
    /**
     * Stop performance trace
     */
    fun stopTrace(trace: Trace?) {
        try {
            trace?.stop()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop trace", e)
        }
    }
    
    /**
     * Log business-specific events for rigger operations
     */
    fun logRiggerEvent(eventType: RiggerEventType, parameters: Map<String, Any>? = null) {
        val eventParams = mutableMapOf<String, Any>()
        eventParams["event_category"] = "rigger_operations"
        eventParams["event_type"] = eventType.name.lowercase()
        parameters?.let { eventParams.putAll(it) }
        
        logEvent("rigger_${eventType.name.lowercase()}", eventParams)
    }
    
    private fun getAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }
}

/**
 * Enum for rigger-specific event types
 */
enum class RiggerEventType {
    JOB_SEARCH,
    JOB_APPLICATION,
    PROFILE_UPDATE,
    CERTIFICATION_UPLOAD,
    EQUIPMENT_LISTING,
    SAFETY_REPORT,
    TRAINING_COMPLETION,
    NETWORK_CONNECTION,
    PAYMENT_PROCESSED,
    LOCATION_UPDATE
}
