package com.riggerconnect.feedback

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.riggerconnect.monitoring.CrashReportingManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * User feedback collection and management system for RiggerConnect
 * 
 * Enables multiple feedback channels including in-app feedback, email,
 * and integration with crash reporting for comprehensive user support
 * 
 * Built by Jack Jonas (WA rigger) & Tia (dev, ChaseWhiteRabbit NGO)
 * Contact: jackjonas95@gmail.com, tiatheone@protonmail.com
 */
class UserFeedbackManager private constructor() {
    
    private lateinit var context: Context
    private val crashReportingManager = CrashReportingManager.getInstance()
    
    companion object {
        @Volatile
        private var INSTANCE: UserFeedbackManager? = null
        
        fun getInstance(): UserFeedbackManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserFeedbackManager().also { INSTANCE = it }
            }
        }
        
        private const val TAG = "UserFeedbackManager"
        
        // Feedback contact information
        private const val SUPPORT_EMAIL = "support@riggerconnect.com"
        private const val DEVELOPMENT_EMAIL = "tiatheone@protonmail.com"
        private const val BUSINESS_EMAIL = "jackjonas95@gmail.com"
        
        // Feedback categories
        const val CATEGORY_BUG_REPORT = "bug_report"
        const val CATEGORY_FEATURE_REQUEST = "feature_request"
        const val CATEGORY_SAFETY_CONCERN = "safety_concern"
        const val CATEGORY_JOB_ISSUE = "job_issue"
        const val CATEGORY_CERTIFICATION_HELP = "certification_help"
        const val CATEGORY_PAYMENT_ISSUE = "payment_issue"
        const val CATEGORY_GENERAL_FEEDBACK = "general_feedback"
        const val CATEGORY_BUSINESS_INQUIRY = "business_inquiry"
    }
    
    fun initialize(context: Context) {
        this.context = context.applicationContext
        Log.d(TAG, "User feedback manager initialized")
    }
    
    /**
     * Submit feedback through multiple channels
     */
    fun submitFeedback(
        category: String,
        message: String,
        userEmail: String? = null,
        userName: String? = null,
        includeSystemInfo: Boolean = true,
        attachScreenshot: Boolean = false
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val feedbackData = createFeedbackData(
                    category, message, userEmail, userName, includeSystemInfo
                )
                
                // Log feedback event for analytics
                crashReportingManager.logEvent("user_feedback_submitted", mapOf(
                    "feedback_category" to category,
                    "has_user_email" to (userEmail != null).toString(),
                    "include_system_info" to includeSystemInfo.toString(),
                    "message_length" to message.length.toString()
                ))
                
                // Route feedback based on category
                when (category) {
                    CATEGORY_SAFETY_CONCERN -> routeSafetyFeedback(feedbackData)
                    CATEGORY_BUSINESS_INQUIRY -> routeBusinessFeedback(feedbackData)
                    CATEGORY_BUG_REPORT -> routeTechnicalFeedback(feedbackData)
                    else -> routeGeneralFeedback(feedbackData)
                }
                
                Log.d(TAG, "Feedback submitted successfully: $category")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to submit feedback", e)
                crashReportingManager.logException(e, "Feedback submission failed")
            }
        }
    }
    
    /**
     * Open email client for direct feedback
     */
    fun openEmailFeedback(
        category: String,
        prefilledMessage: String = "",
        recipient: String = SUPPORT_EMAIL
    ) {
        try {
            val subject = generateEmailSubject(category)
            val body = generateEmailBody(category, prefilledMessage)
            
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            if (emailIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(emailIntent)
                
                // Log email feedback opened
                crashReportingManager.logEvent("email_feedback_opened", mapOf(
                    "category" to category,
                    "recipient" to recipient
                ))
            } else {
                Log.w(TAG, "No email client available")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open email feedback", e)
            crashReportingManager.logException(e, "Email feedback failed")
        }
    }
    
    /**
     * Submit rigger-specific feedback for industry concerns
     */
    fun submitRiggerFeedback(
        feedbackType: RiggerFeedbackType,
        details: Map<String, String>,
        priority: FeedbackPriority = FeedbackPriority.MEDIUM
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val feedbackData = JSONObject().apply {
                    put("feedback_type", feedbackType.name)
                    put("priority", priority.name)
                    put("timestamp", System.currentTimeMillis())
                    put("details", JSONObject(details))
                    put("app_version", getAppVersion())
                    put("user_context", getUserContext())
                }
                
                // Route based on feedback type and priority
                when (feedbackType) {
                    RiggerFeedbackType.SAFETY_VIOLATION -> routeUrgentSafetyFeedback(feedbackData)
                    RiggerFeedbackType.EQUIPMENT_MALFUNCTION -> routeEquipmentFeedback(feedbackData)
                    RiggerFeedbackType.TRAINING_ISSUE -> routeTrainingFeedback(feedbackData)
                    RiggerFeedbackType.JOB_SITE_CONCERN -> routeJobSiteFeedback(feedbackData)
                    RiggerFeedbackType.REGULATORY_COMPLIANCE -> routeComplianceFeedback(feedbackData)
                    else -> routeGeneralRiggerFeedback(feedbackData)
                }
                
                Log.d(TAG, "Rigger feedback submitted: $feedbackType")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to submit rigger feedback", e)
                crashReportingManager.logException(e, "Rigger feedback submission failed")
            }
        }
    }
    
    /**
     * Create in-app feedback dialog
     */
    fun showFeedbackDialog(context: Context, category: String? = null) {
        // This would typically show a custom dialog or fragment
        // For now, we'll route to email feedback
        val selectedCategory = category ?: CATEGORY_GENERAL_FEEDBACK
        openEmailFeedback(selectedCategory)
    }
    
    private fun createFeedbackData(
        category: String,
        message: String,
        userEmail: String?,
        userName: String?,
        includeSystemInfo: Boolean
    ): JSONObject {
        return JSONObject().apply {
            put("category", category)
            put("message", message)
            put("user_email", userEmail ?: "anonymous")
            put("user_name", userName ?: "anonymous")
            put("timestamp", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
            put("app_version", getAppVersion())
            
            if (includeSystemInfo) {
                put("system_info", getSystemInfo())
            }
        }
    }
    
    private fun routeSafetyFeedback(feedbackData: JSONObject) {
        // Route safety concerns to both technical and business teams
        val recipients = arrayOf(SUPPORT_EMAIL, BUSINESS_EMAIL)
        sendEmailFeedback(recipients, "URGENT: Safety Concern", feedbackData.toString())
    }
    
    private fun routeBusinessFeedback(feedbackData: JSONObject) {
        sendEmailFeedback(arrayOf(BUSINESS_EMAIL), "Business Inquiry", feedbackData.toString())
    }
    
    private fun routeTechnicalFeedback(feedbackData: JSONObject) {
        sendEmailFeedback(arrayOf(DEVELOPMENT_EMAIL), "Technical Issue", feedbackData.toString())
    }
    
    private fun routeGeneralFeedback(feedbackData: JSONObject) {
        sendEmailFeedback(arrayOf(SUPPORT_EMAIL), "General Feedback", feedbackData.toString())
    }
    
    private fun routeUrgentSafetyFeedback(feedbackData: JSONObject) {
        // Immediate routing for safety violations
        val recipients = arrayOf(SUPPORT_EMAIL, BUSINESS_EMAIL, DEVELOPMENT_EMAIL)
        sendEmailFeedback(recipients, "CRITICAL: Safety Violation Report", feedbackData.toString())
    }
    
    private fun routeEquipmentFeedback(feedbackData: JSONObject) {
        sendEmailFeedback(arrayOf(SUPPORT_EMAIL), "Equipment Issue Report", feedbackData.toString())
    }
    
    private fun routeTrainingFeedback(feedbackData: JSONObject) {
        sendEmailFeedback(arrayOf(BUSINESS_EMAIL), "Training Program Feedback", feedbackData.toString())
    }
    
    private fun routeJobSiteFeedback(feedbackData: JSONObject) {
        sendEmailFeedback(arrayOf(SUPPORT_EMAIL), "Job Site Concern", feedbackData.toString())
    }
    
    private fun routeComplianceFeedback(feedbackData: JSONObject) {
        sendEmailFeedback(arrayOf(BUSINESS_EMAIL), "Regulatory Compliance Issue", feedbackData.toString())
    }
    
    private fun routeGeneralRiggerFeedback(feedbackData: JSONObject) {
        sendEmailFeedback(arrayOf(SUPPORT_EMAIL), "Rigger Platform Feedback", feedbackData.toString())
    }
    
    private fun sendEmailFeedback(recipients: Array<String>, subject: String, body: String) {
        try {
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_EMAIL, recipients)
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            context.startActivity(Intent.createChooser(emailIntent, "Send Feedback").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send email feedback", e)
        }
    }
    
    private fun generateEmailSubject(category: String): String {
        return "RiggerConnect Feedback - ${category.replace("_", " ").capitalize(Locale.getDefault())}"
    }
    
    private fun generateEmailBody(category: String, prefilledMessage: String): String {
        return buildString {
            appendLine("RiggerConnect Android App Feedback")
            appendLine("Category: ${category.replace("_", " ")}")
            appendLine("App Version: ${getAppVersion()}")
            appendLine("Device: ${android.os.Build.MODEL}")
            appendLine("Android Version: ${android.os.Build.VERSION.RELEASE}")
            appendLine("Timestamp: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}")
            appendLine()
            appendLine("Message:")
            appendLine(prefilledMessage)
            appendLine()
            appendLine("Additional details:")
            appendLine("(Please describe your feedback, issue, or suggestion here)")
        }
    }
    
    private fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }
    
    private fun getSystemInfo(): JSONObject {
        return JSONObject().apply {
            put("device_model", android.os.Build.MODEL)
            put("device_manufacturer", android.os.Build.MANUFACTURER)
            put("android_version", android.os.Build.VERSION.RELEASE)
            put("api_level", android.os.Build.VERSION.SDK_INT)
            put("app_version", getAppVersion())
        }
    }
    
    private fun getUserContext(): JSONObject {
        return JSONObject().apply {
            put("timestamp", System.currentTimeMillis())
            put("locale", Locale.getDefault().toString())
            put("timezone", TimeZone.getDefault().id)
        }
    }
}

/**
 * Enum for rigger-specific feedback types
 */
enum class RiggerFeedbackType {
    SAFETY_VIOLATION,
    EQUIPMENT_MALFUNCTION,
    TRAINING_ISSUE,
    JOB_SITE_CONCERN,
    CERTIFICATION_PROBLEM,
    PAYMENT_DISPUTE,
    REGULATORY_COMPLIANCE,
    PLATFORM_SUGGESTION,
    NETWORK_ISSUE,
    DATA_ACCURACY,
    USER_EXPERIENCE,
    PERFORMANCE_ISSUE
}

/**
 * Feedback priority levels
 */
enum class FeedbackPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
