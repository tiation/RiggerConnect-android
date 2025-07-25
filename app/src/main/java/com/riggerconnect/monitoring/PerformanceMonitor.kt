package com.riggerconnect.monitoring

import android.app.Application
import android.content.Context
import android.os.SystemClock
import android.util.Log
import com.google.firebase.performance.FirebasePerformance
import com.google.firebase.performance.metrics.Trace
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * Performance monitoring system for RiggerConnect Android
 * 
 * Tracks app performance metrics, network calls, and user experience
 * Built by Jack Jonas (WA rigger) & Tia (dev, ChaseWhiteRabbit NGO)
 * Contact: jackjonas95@gmail.com, tiatheone@protonmail.com
 */
class PerformanceMonitor private constructor() {
    
    private val activeTraces = ConcurrentHashMap<String, Trace>()
    private val performanceMetrics = ConcurrentHashMap<String, Long>()
    private lateinit var firebasePerformance: FirebasePerformance
    
    companion object {
        @Volatile
        private var INSTANCE: PerformanceMonitor? = null
        
        fun getInstance(): PerformanceMonitor {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PerformanceMonitor().also { INSTANCE = it }
            }
        }
        
        private const val TAG = "PerformanceMonitor"
        
        // Performance trace names
        const val TRACE_APP_START = "app_start"
        const val TRACE_LOGIN = "user_login"
        const val TRACE_JOB_SEARCH = "job_search"
        const val TRACE_PROFILE_LOAD = "profile_load"
        const val TRACE_CERTIFICATION_UPLOAD = "certification_upload"
        const val TRACE_EQUIPMENT_LISTING = "equipment_listing"
        const val TRACE_SAFETY_REPORT = "safety_report"
        const val TRACE_NETWORK_REQUEST = "network_request"
        const val TRACE_DATABASE_OPERATION = "database_operation"
        const val TRACE_IMAGE_PROCESSING = "image_processing"
    }
    
    fun initialize(context: Context) {
        try {
            firebasePerformance = FirebasePerformance.getInstance()
            
            // Configure performance monitoring
            firebasePerformance.isPerformanceCollectionEnabled = true
            
            Log.d(TAG, "Performance monitoring initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize performance monitoring", e)
        }
    }
    
    /**
     * Start a performance trace
     */
    fun startTrace(traceName: String): String {
        return try {
            val traceId = "${traceName}_${System.currentTimeMillis()}"
            val trace = firebasePerformance.newTrace(traceName)
            trace.start()
            activeTraces[traceId] = trace
            performanceMetrics[traceId] = SystemClock.elapsedRealtime()
            
            Log.d(TAG, "Started trace: $traceName (ID: $traceId)")
            traceId
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start trace: $traceName", e)
            ""
        }
    }
    
    /**
     * Stop a performance trace
     */
    fun stopTrace(traceId: String, additionalMetrics: Map<String, String>? = null) {
        try {
            val trace = activeTraces.remove(traceId)
            val startTime = performanceMetrics.remove(traceId)
            
            if (trace != null && startTime != null) {
                // Add custom metrics
                additionalMetrics?.forEach { (key, value) ->
                    trace.putAttribute(key, value)
                }
                
                // Add duration metric
                val duration = SystemClock.elapsedRealtime() - startTime
                trace.putMetric("duration_ms", duration)
                
                trace.stop()
                Log.d(TAG, "Stopped trace: $traceId (Duration: ${duration}ms)")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop trace: $traceId", e)
        }
    }
    
    /**
     * Monitor network request performance
     */
    fun monitorNetworkRequest(
        url: String,
        method: String,
        requestSizeBytes: Long? = null,
        responseSizeBytes: Long? = null,
        responseCode: Int? = null
    ): String {
        return try {
            val traceId = startTrace(TRACE_NETWORK_REQUEST)
            val trace = activeTraces[traceId]
            
            trace?.let {
                it.putAttribute("http_url", url)
                it.putAttribute("http_method", method)
                requestSizeBytes?.let { size -> it.putMetric("request_payload_bytes", size) }
                responseSizeBytes?.let { size -> it.putMetric("response_payload_bytes", size) }
                responseCode?.let { code -> it.putMetric("http_response_code", code.toLong()) }
            }
            
            traceId
        } catch (e: Exception) {
            Log.e(TAG, "Failed to monitor network request", e)
            ""
        }
    }
    
    /**
     * Monitor rigger-specific operations
     */
    fun monitorRiggerOperation(
        operationType: RiggerOperationType,
        additionalAttributes: Map<String, String>? = null
    ): String {
        return try {
            val traceName = "rigger_${operationType.name.lowercase()}"
            val traceId = startTrace(traceName)
            val trace = activeTraces[traceId]
            
            trace?.let {
                it.putAttribute("operation_type", operationType.name.lowercase())
                it.putAttribute("feature_category", "rigger_operations")
                additionalAttributes?.forEach { (key, value) ->
                    it.putAttribute(key, value)
                }
            }
            
            traceId
        } catch (e: Exception) {
            Log.e(TAG, "Failed to monitor rigger operation: $operationType", e)
            ""
        }
    }
    
    /**
     * Record custom performance metric
     */
    fun recordMetric(metricName: String, value: Long, attributes: Map<String, String>? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val trace = firebasePerformance.newTrace(metricName)
                trace.start()
                
                attributes?.forEach { (key, attr) ->
                    trace.putAttribute(key, attr)
                }
                
                trace.putMetric(metricName, value)
                trace.stop()
                
                Log.d(TAG, "Recorded metric: $metricName = $value")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to record metric: $metricName", e)
            }
        }
    }
    
    /**
     * Monitor app startup performance
     */
    fun monitorAppStartup(application: Application) {
        try {
            val startupTrace = firebasePerformance.newTrace(TRACE_APP_START)
            startupTrace.start()
            
            // Monitor different startup phases
            application.registerActivityLifecycleCallbacks(
                AppStartupLifecycleCallbacks(startupTrace)
            )
            
            Log.d(TAG, "App startup monitoring enabled")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to monitor app startup", e)
        }
    }
    
    /**
     * Generate performance report for analytics
     */
    fun generatePerformanceReport(): Map<String, Any> {
        return mapOf(
            "active_traces" to activeTraces.size,
            "total_metrics_collected" to performanceMetrics.size,
            "monitoring_enabled" to firebasePerformance.isPerformanceCollectionEnabled,
            "timestamp" to System.currentTimeMillis(),
            "device_info" to mapOf(
                "model" to android.os.Build.MODEL,
                "android_version" to android.os.Build.VERSION.RELEASE,
                "api_level" to android.os.Build.VERSION.SDK_INT
            )
        )
    }
}

/**
 * Enum for rigger-specific operations to monitor
 */
enum class RiggerOperationType {
    JOB_SEARCH,
    JOB_APPLICATION_SUBMIT,
    PROFILE_UPDATE,
    CERTIFICATION_UPLOAD,
    EQUIPMENT_ADD,
    SAFETY_REPORT_SUBMIT,
    TRAINING_MODULE_COMPLETE,
    PAYMENT_PROCESS,
    LOCATION_UPDATE,
    NETWORK_SYNC,
    DATA_BACKUP,
    IMAGE_UPLOAD
}

/**
 * Lifecycle callbacks for monitoring app startup
 */
private class AppStartupLifecycleCallbacks(
    private val startupTrace: Trace
) : Application.ActivityLifecycleCallbacks {
    
    private var isFirstActivityCreated = false
    
    override fun onActivityCreated(activity: android.app.Activity, savedInstanceState: android.os.Bundle?) {
        if (!isFirstActivityCreated) {
            startupTrace.putAttribute("first_activity", activity.javaClass.simpleName)
            isFirstActivityCreated = true
        }
    }
    
    override fun onActivityStarted(activity: android.app.Activity) {}
    
    override fun onActivityResumed(activity: android.app.Activity) {
        if (!isFirstActivityCreated) {
            startupTrace.stop()
            activity.application.unregisterActivityLifecycleCallbacks(this)
        }
    }
    
    override fun onActivityPaused(activity: android.app.Activity) {}
    override fun onActivityStopped(activity: android.app.Activity) {}
    override fun onActivitySaveInstanceState(activity: android.app.Activity, outState: android.os.Bundle) {}
    override fun onActivityDestroyed(activity: android.app.Activity) {}
}
