package com.riggerconnect.feedback

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riggerconnect.ui.feedback.FeedbackDialog
import com.riggerconnect.ui.feedback.QuickFeedbackActions
import com.riggerconnect.ui.feedback.RiggerFeedbackCard

/**
 * Integration component for feedback system
 * 
 * This component demonstrates how to integrate the feedback system into
 * the main app screens and navigation structure.
 * 
 * Built by Jack Jonas (WA rigger) & Tia (dev, ChaseWhiteRabbit NGO)
 * Contact: jackjonas95@gmail.com, tiatheone@protonmail.com
 */

/**
 * Feedback Screen - Main screen for feedback functionality
 */
@Composable
fun FeedbackScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val feedbackManager = UserFeedbackManager.getInstance()
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("") }
    
    // Initialize feedback manager
    LaunchedEffect(Unit) {
        feedbackManager.initialize(context)
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Feedback & Support",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        // Quick feedback actions
        QuickFeedbackActions(
            onCategorySelected = { category ->
                selectedCategory = category
                showFeedbackDialog = true
            }
        )
        
        // Rigger-specific feedback cards
        Text(
            text = "Industry-Specific Feedback",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 8.dp)
        )
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(getTopRiggerFeedbackTypes()) { feedbackType ->
                RiggerFeedbackCard(
                    feedbackType = feedbackType,
                    onSubmitFeedback = { type, details ->
                        feedbackManager.submitRiggerFeedback(
                            feedbackType = type,
                            details = details,
                            priority = determinePriority(type)
                        )
                    }
                )
            }
        }
        
        // Contact information
        ContactInfoCard()
    }
    
    // Feedback dialog
    FeedbackDialog(
        isVisible = showFeedbackDialog,
        onDismiss = { showFeedbackDialog = false },
        onSubmit = { category, message, email ->
            feedbackManager.submitFeedback(
                category = category,
                message = message,
                userEmail = email.takeIf { it.isNotBlank() },
                includeSystemInfo = true
            )
            showFeedbackDialog = false
        }
    )
}

/**
 * Floating Action Button for quick feedback access
 */
@Composable
fun FeedbackFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.secondary
    ) {
        Icon(
            imageVector = Icons.Default.Feedback,
            contentDescription = "Send Feedback"
        )
    }
}

/**
 * Menu item for feedback in navigation drawer or overflow menu
 */
@Composable
fun FeedbackMenuItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Feedback,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Send Feedback")
    }
}

/**
 * Contact information card
 */
@Composable
private fun ContactInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Direct Contact",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "For immediate assistance or urgent safety concerns:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "• Business Inquiries: jackjonas95@gmail.com",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• Technical Support: tiatheone@protonmail.com",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• General Support: support@riggerconnect.com",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/**
 * Example integration in MainActivity or main navigation
 */
class FeedbackIntegrationHelper {
    companion object {
        
        /**
         * Add feedback menu item to overflow menu
         */
        fun addFeedbackToOverflowMenu(
            onFeedbackClick: () -> Unit
        ): @Composable () -> Unit = {
            DropdownMenuItem(
                text = { Text("Send Feedback") },
                onClick = onFeedbackClick,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Feedback,
                        contentDescription = null
                    )
                }
            )
        }
        
        /**
         * Initialize feedback system in Application class or MainActivity
         */
        fun initializeFeedbackSystem(context: android.content.Context) {
            UserFeedbackManager.getInstance().initialize(context)
        }
        
        /**
         * Handle feedback from crash or error scenarios
         */
        fun handleErrorFeedback(
            context: android.content.Context,
            errorMessage: String,
            stackTrace: String? = null
        ) {
            val feedbackManager = UserFeedbackManager.getInstance()
            
            val details = mutableMapOf<String, String>().apply {
                put("error_message", errorMessage)
                if (stackTrace != null) {
                    put("stack_trace", stackTrace)
                }
                put("auto_generated", "true")
                put("timestamp", System.currentTimeMillis().toString())
            }
            
            feedbackManager.submitRiggerFeedback(
                feedbackType = RiggerFeedbackType.PERFORMANCE_ISSUE,
                details = details,
                priority = FeedbackPriority.HIGH
            )
        }
        
        /**
         * Trigger feedback for specific user actions
         */
        fun triggerContextualFeedback(
            context: android.content.Context,
            action: String,
            category: String = UserFeedbackManager.CATEGORY_GENERAL_FEEDBACK
        ) {
            val feedbackManager = UserFeedbackManager.getInstance()
            feedbackManager.openEmailFeedback(
                category = category,
                prefilledMessage = "Feedback related to: $action"
            )
        }
    }
}

// Helper functions
private fun getTopRiggerFeedbackTypes(): List<RiggerFeedbackType> {
    return listOf(
        RiggerFeedbackType.SAFETY_VIOLATION,
        RiggerFeedbackType.EQUIPMENT_MALFUNCTION,
        RiggerFeedbackType.JOB_SITE_CONCERN,
        RiggerFeedbackType.CERTIFICATION_PROBLEM,
        RiggerFeedbackType.TRAINING_ISSUE,
        RiggerFeedbackType.PLATFORM_SUGGESTION
    )
}

private fun determinePriority(feedbackType: RiggerFeedbackType): FeedbackPriority {
    return when (feedbackType) {
        RiggerFeedbackType.SAFETY_VIOLATION -> FeedbackPriority.CRITICAL
        RiggerFeedbackType.EQUIPMENT_MALFUNCTION -> FeedbackPriority.HIGH
        RiggerFeedbackType.REGULATORY_COMPLIANCE -> FeedbackPriority.HIGH
        RiggerFeedbackType.PAYMENT_DISPUTE -> FeedbackPriority.HIGH
        RiggerFeedbackType.JOB_SITE_CONCERN -> FeedbackPriority.MEDIUM
        RiggerFeedbackType.CERTIFICATION_PROBLEM -> FeedbackPriority.MEDIUM
        RiggerFeedbackType.TRAINING_ISSUE -> FeedbackPriority.MEDIUM
        else -> FeedbackPriority.LOW
    }
}
