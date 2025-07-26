package com.riggerconnect.ui.feedback

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.riggerconnect.feedback.RiggerFeedbackType
import com.riggerconnect.feedback.UserFeedbackManager

/**
 * Main feedback dialog component
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (category: String, message: String, userEmail: String) -> Unit
) {
    if (isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Send Feedback",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    FeedbackForm(
                        onSubmit = onSubmit,
                        onCancel = onDismiss
                    )
                }
            }
        }
    }
}

/**
 * Feedback form component
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedbackForm(
    onSubmit: (category: String, message: String, userEmail: String) -> Unit,
    onCancel: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf(UserFeedbackManager.CATEGORY_GENERAL_FEEDBACK) }
    var message by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var showCategoryDialog by remember { mutableStateOf(false) }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Category selection
        OutlinedButton(
            onClick = { showCategoryDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = getCategoryIcon(selectedCategory),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(formatCategoryName(selectedCategory))
        }
        
        // Message input
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Your message") },
            placeholder = { Text("Describe your feedback, issue, or suggestion...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5
        )
        
        // Email input (optional)
        OutlinedTextField(
            value = userEmail,
            onValueChange = { userEmail = it },
            label = { Text("Email (optional)") },
            placeholder = { Text("your.email@example.com") },
            modifier = Modifier.fillMaxWidth()
        )
        
        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (message.isNotBlank()) {
                        onSubmit(selectedCategory, message, userEmail)
                    }
                },
                enabled = message.isNotBlank()
            ) {
                Text("Send")
            }
        }
    }
    
    // Category selection dialog
    if (showCategoryDialog) {
        CategorySelectionDialog(
            selectedCategory = selectedCategory,
            onCategorySelected = { 
                selectedCategory = it
                showCategoryDialog = false
            },
            onDismiss = { showCategoryDialog = false }
        )
    }
}

/**
 * Category selection dialog
 */
@Composable
private fun CategorySelectionDialog(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Select Category",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(getFeedbackCategories()) { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .selectable(
                                    selected = selectedCategory == category.id,
                                    onClick = { onCategorySelected(category.id) }
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedCategory == category.id,
                                onClick = { onCategorySelected(category.id) }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                imageVector = category.icon,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = category.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

/**
 * Rigger-specific feedback component
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiggerFeedbackCard(
    feedbackType: RiggerFeedbackType,
    onSubmitFeedback: (RiggerFeedbackType, Map<String, String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var details by remember { mutableStateOf(mapOf<String, String>()) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getRiggerFeedbackIcon(feedbackType),
                        contentDescription = null,
                        tint = getRiggerFeedbackColor(feedbackType),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = formatRiggerFeedbackType(feedbackType),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle details"
                    )
                }
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                
                RiggerFeedbackDetailsForm(
                    feedbackType = feedbackType,
                    onDetailsChanged = { details = it },
                    onSubmit = { onSubmitFeedback(feedbackType, details) }
                )
            }
        }
    }
}

/**
 * Details form for rigger feedback
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RiggerFeedbackDetailsForm(
    feedbackType: RiggerFeedbackType,
    onDetailsChanged: (Map<String, String>) -> Unit,
    onSubmit: () -> Unit
) {
    val context = LocalContext.current
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var equipmentInvolved by remember { mutableStateOf("") }
    var additionalInfo by remember { mutableStateOf("") }
    
    LaunchedEffect(description, location, equipmentInvolved, additionalInfo) {
        onDetailsChanged(mapOf(
            "description" to description,
            "location" to location,
            "equipment_involved" to equipmentInvolved,
            "additional_info" to additionalInfo
        ))
    }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            placeholder = { Text("Describe the issue or concern...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )
        
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location/Job Site") },
            placeholder = { Text("Where did this occur?") },
            modifier = Modifier.fillMaxWidth()
        )
        
        if (feedbackType == RiggerFeedbackType.EQUIPMENT_MALFUNCTION || 
            feedbackType == RiggerFeedbackType.SAFETY_VIOLATION) {
            OutlinedTextField(
                value = equipmentInvolved,
                onValueChange = { equipmentInvolved = it },
                label = { Text("Equipment Involved") },
                placeholder = { Text("Crane, rigging hardware, etc.") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        OutlinedTextField(
            value = additionalInfo,
            onValueChange = { additionalInfo = it },
            label = { Text("Additional Information") },
            placeholder = { Text("Any other relevant details...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 3
        )
        
        Button(
            onClick = onSubmit,
            modifier = Modifier.align(Alignment.End),
            enabled = description.isNotBlank()
        ) {
            Text("Submit Report")
        }
    }
}

/**
 * Quick feedback actions component
 */
@Composable
fun QuickFeedbackActions(
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Quick Feedback",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onCategorySelected(UserFeedbackManager.CATEGORY_BUG_REPORT) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Bug", textAlign = TextAlign.Center)
                }
                
                OutlinedButton(
                    onClick = { onCategorySelected(UserFeedbackManager.CATEGORY_FEATURE_REQUEST) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.LightbulbOutline,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Idea", textAlign = TextAlign.Center)
                }
                
                OutlinedButton(
                    onClick = { onCategorySelected(UserFeedbackManager.CATEGORY_SAFETY_CONCERN) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Safety", textAlign = TextAlign.Center)
                }
            }
        }
    }
}

// Helper functions
private fun getCategoryIcon(category: String): ImageVector {
    return when (category) {
        UserFeedbackManager.CATEGORY_BUG_REPORT -> Icons.Default.BugReport
        UserFeedbackManager.CATEGORY_FEATURE_REQUEST -> Icons.Default.LightbulbOutline
        UserFeedbackManager.CATEGORY_SAFETY_CONCERN -> Icons.Default.Warning
        UserFeedbackManager.CATEGORY_JOB_ISSUE -> Icons.Default.Work
        UserFeedbackManager.CATEGORY_CERTIFICATION_HELP -> Icons.Default.School
        UserFeedbackManager.CATEGORY_PAYMENT_ISSUE -> Icons.Default.Payment
        UserFeedbackManager.CATEGORY_BUSINESS_INQUIRY -> Icons.Default.Business
        else -> Icons.Default.Feedback
    }
}

private fun formatCategoryName(category: String): String {
    return category.replace("_", " ").split(" ").joinToString(" ") { 
        it.replaceFirstChar { char -> char.uppercaseChar() }
    }
}

private fun getRiggerFeedbackIcon(type: RiggerFeedbackType): ImageVector {
    return when (type) {
        RiggerFeedbackType.SAFETY_VIOLATION -> Icons.Default.Warning
        RiggerFeedbackType.EQUIPMENT_MALFUNCTION -> Icons.Default.Build
        RiggerFeedbackType.TRAINING_ISSUE -> Icons.Default.School
        RiggerFeedbackType.JOB_SITE_CONCERN -> Icons.Default.LocationOn
        RiggerFeedbackType.CERTIFICATION_PROBLEM -> Icons.Default.VerifiedUser
        RiggerFeedbackType.PAYMENT_DISPUTE -> Icons.Default.Payment
        RiggerFeedbackType.REGULATORY_COMPLIANCE -> Icons.Default.Gavel
        RiggerFeedbackType.PLATFORM_SUGGESTION -> Icons.Default.LightbulbOutline
        RiggerFeedbackType.NETWORK_ISSUE -> Icons.Default.NetworkCheck
        RiggerFeedbackType.DATA_ACCURACY -> Icons.Default.DataUsage
        RiggerFeedbackType.USER_EXPERIENCE -> Icons.Default.Face
        RiggerFeedbackType.PERFORMANCE_ISSUE -> Icons.Default.Speed
    }
}

@Composable
private fun getRiggerFeedbackColor(type: RiggerFeedbackType): androidx.compose.ui.graphics.Color {
    return when (type) {
        RiggerFeedbackType.SAFETY_VIOLATION -> MaterialTheme.colorScheme.error
        RiggerFeedbackType.EQUIPMENT_MALFUNCTION -> MaterialTheme.colorScheme.error
        RiggerFeedbackType.REGULATORY_COMPLIANCE -> MaterialTheme.colorScheme.error
        RiggerFeedbackType.PAYMENT_DISPUTE -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }
}

private fun formatRiggerFeedbackType(type: RiggerFeedbackType): String {
    return type.name.replace("_", " ").split(" ").joinToString(" ") { 
        it.lowercase().replaceFirstChar { char -> char.uppercaseChar() }
    }
}

private data class FeedbackCategory(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector
)

private fun getFeedbackCategories(): List<FeedbackCategory> {
    return listOf(
        FeedbackCategory(
            UserFeedbackManager.CATEGORY_GENERAL_FEEDBACK,
            "General Feedback",
            "General comments or suggestions",
            Icons.Default.Feedback
        ),
        FeedbackCategory(
            UserFeedbackManager.CATEGORY_BUG_REPORT,
            "Bug Report",
            "Report app crashes or functionality issues",
            Icons.Default.BugReport
        ),
        FeedbackCategory(
            UserFeedbackManager.CATEGORY_FEATURE_REQUEST,
            "Feature Request",
            "Suggest new features or improvements",
            Icons.Default.LightbulbOutline
        ),
        FeedbackCategory(
            UserFeedbackManager.CATEGORY_SAFETY_CONCERN,
            "Safety Concern",
            "Report safety-related issues or violations",
            Icons.Default.Warning
        ),
        FeedbackCategory(
            UserFeedbackManager.CATEGORY_JOB_ISSUE,
            "Job Issue",
            "Problems with specific jobs or assignments",
            Icons.Default.Work
        ),
        FeedbackCategory(
            UserFeedbackManager.CATEGORY_CERTIFICATION_HELP,
            "Certification Help",
            "Questions about certifications or training",
            Icons.Default.School
        ),
        FeedbackCategory(
            UserFeedbackManager.CATEGORY_PAYMENT_ISSUE,
            "Payment Issue",
            "Payment disputes or billing questions",
            Icons.Default.Payment
        ),
        FeedbackCategory(
            UserFeedbackManager.CATEGORY_BUSINESS_INQUIRY,
            "Business Inquiry",
            "Partnership or business-related questions",
            Icons.Default.Business
        )
    )
}
