# RiggerConnect Feedback System

## Overview

The RiggerConnect feedback system provides a comprehensive solution for collecting and managing user feedback across multiple channels. This system is specifically designed for the rigging industry, supporting both general app feedback and industry-specific concerns including safety violations, equipment issues, and regulatory compliance.

**Built by Jack Jonas (WA rigger) & Tia (dev, ChaseWhiteRabbit NGO)**  
**Contact: jackjonas95@gmail.com, tiatheone@protonmail.com**

## Architecture

### Core Components

1. **UserFeedbackManager** - Singleton manager for all feedback operations
2. **FeedbackComponents** - Compose UI components for feedback collection
3. **FeedbackIntegration** - Integration helpers and screens
4. **RiggerFeedbackType & FeedbackPriority** - Enums for categorization

### System Design

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   UI Layer      │    │  Business Logic  │    │  External APIs  │
│                 │    │                  │    │                 │
│ FeedbackDialog  │───▶│UserFeedbackMgr   │───▶│ Email Intents   │
│ FeedbackScreen  │    │                  │    │ Firebase        │
│ RiggerCards     │    │ Category Routing │    │ Analytics       │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

## Features

### General Feedback Categories

- **Bug Report** - App crashes or functionality issues
- **Feature Request** - New feature suggestions and improvements
- **Safety Concern** - Safety-related issues or violations
- **Job Issue** - Problems with specific jobs or assignments
- **Certification Help** - Questions about certifications or training
- **Payment Issue** - Payment disputes or billing questions
- **General Feedback** - General comments or suggestions
- **Business Inquiry** - Partnership or business-related questions

### Rigger-Specific Feedback Types

- **Safety Violation** - Critical safety incidents (Priority: CRITICAL)
- **Equipment Malfunction** - Equipment failures or issues (Priority: HIGH)
- **Training Issue** - Training program feedback (Priority: MEDIUM)
- **Job Site Concern** - On-site problems or concerns (Priority: MEDIUM)
- **Certification Problem** - Certification-related issues (Priority: MEDIUM)
- **Payment Dispute** - Payment-related disputes (Priority: HIGH)
- **Regulatory Compliance** - Compliance issues (Priority: HIGH)
- **Platform Suggestion** - App improvement suggestions (Priority: LOW)
- **Network Issue** - Connectivity problems (Priority: LOW)
- **Data Accuracy** - Data quality concerns (Priority: LOW)
- **User Experience** - UX/UI feedback (Priority: LOW)
- **Performance Issue** - App performance problems (Priority: MEDIUM)

### Feedback Channels

1. **In-App Feedback Dialog** - Modal dialog with form
2. **Email Integration** - Direct email client integration
3. **Quick Actions** - Fast access buttons for common feedback
4. **Contextual Feedback** - Context-aware feedback triggers
5. **Error Reporting** - Automatic error feedback collection

## Implementation Guide

### 1. Initialize the Feedback System

```kotlin
// In Application class or MainActivity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize feedback system
        FeedbackIntegrationHelper.initializeFeedbackSystem(this)
    }
}
```

### 2. Add Feedback to UI

#### Quick Feedback Actions
```kotlin
@Composable
fun MainScreen() {
    var showFeedback by remember { mutableStateOf(false) }
    
    Column {
        // Your app content
        
        QuickFeedbackActions(
            onCategorySelected = { category ->
                showFeedback = true
            }
        )
    }
}
```

#### Floating Action Button
```kotlin
@Composable
fun MainScreenWithFAB() {
    Scaffold(
        floatingActionButton = {
            FeedbackFAB(
                onClick = { /* Show feedback dialog */ }
            )
        }
    ) { paddingValues ->
        // Your content
    }
}
```

#### Navigation Integration
```kotlin
// Add to navigation drawer or overflow menu
FeedbackMenuItem(
    onClick = { 
        navController.navigate("feedback_screen")
    }
)
```

### 3. Handle Feedback Submission

```kotlin
@Composable
fun MyScreen() {
    val context = LocalContext.current
    val feedbackManager = UserFeedbackManager.getInstance()
    
    // Regular feedback
    feedbackManager.submitFeedback(
        category = UserFeedbackManager.CATEGORY_BUG_REPORT,
        message = "App crashes when...",
        userEmail = "user@example.com",
        includeSystemInfo = true
    )
    
    // Rigger-specific feedback
    feedbackManager.submitRiggerFeedback(
        feedbackType = RiggerFeedbackType.SAFETY_VIOLATION,
        details = mapOf(
            "description" to "Unsafe rigging practice observed",
            "location" to "Construction site ABC",
            "equipment_involved" to "Tower crane #5"
        ),
        priority = FeedbackPriority.CRITICAL
    )
}
```

### 4. Error Handling Integration

```kotlin
// Automatic error feedback
try {
    // Your app logic
} catch (e: Exception) {
    FeedbackIntegrationHelper.handleErrorFeedback(
        context = context,
        errorMessage = e.message ?: "Unknown error",
        stackTrace = e.stackTrace.toString()
    )
}
```

## UI Components

### FeedbackDialog
Modal dialog with category selection, message input, and optional email field.

**Properties:**
- `isVisible: Boolean` - Controls dialog visibility
- `onDismiss: () -> Unit` - Dismiss callback
- `onSubmit: (category, message, email) -> Unit` - Submit callback

### RiggerFeedbackCard
Expandable card for industry-specific feedback with detailed form.

**Properties:**
- `feedbackType: RiggerFeedbackType` - Type of rigger feedback
- `onSubmitFeedback: (type, details) -> Unit` - Submit callback
- `modifier: Modifier` - UI modifiers

### QuickFeedbackActions
Quick access buttons for common feedback categories.

**Properties:**
- `onCategorySelected: (String) -> Unit` - Category selection callback
- `modifier: Modifier` - UI modifiers

### FeedbackScreen
Complete feedback screen with all components integrated.

## Routing & Prioritization

### Feedback Routing

The system automatically routes feedback based on category and type:

| Category/Type | Recipients | Subject Prefix |
|---------------|------------|----------------|
| Safety Concern | support@, business@ | URGENT: Safety Concern |
| Safety Violation | support@, business@, dev@ | CRITICAL: Safety Violation |
| Business Inquiry | business@ | Business Inquiry |
| Bug Report | dev@ | Technical Issue |
| General | support@ | General Feedback |

### Priority Handling

- **CRITICAL** - Safety violations, immediate routing to all teams
- **HIGH** - Equipment issues, payment disputes, compliance issues
- **MEDIUM** - Job site concerns, certification problems, training issues
- **LOW** - Feature requests, general suggestions, UI feedback

## Integration with Monitoring

The feedback system integrates with Firebase Analytics and Crashlytics:

```kotlin
// Automatic event logging
crashReportingManager.logEvent("user_feedback_submitted", mapOf(
    "feedback_category" to category,
    "has_user_email" to (userEmail != null).toString(),
    "message_length" to message.length.toString()
))

// Exception logging for feedback failures
crashReportingManager.logException(exception, "Feedback submission failed")
```

## Email Templates

### Bug Report Template
```
RiggerConnect Android App Feedback
Category: Bug Report
App Version: 1.0.0
Device: Samsung Galaxy S21
Android Version: 13
Timestamp: 2024-01-15 14:30:00

Message:
[User's message here]

Additional details:
(Please describe your feedback, issue, or suggestion here)
```

### Rigger Feedback Template
```json
{
  "feedback_type": "SAFETY_VIOLATION",
  "priority": "CRITICAL",
  "timestamp": 1705330200000,
  "details": {
    "description": "Unsafe rigging practice observed",
    "location": "Construction site ABC",
    "equipment_involved": "Tower crane #5",
    "additional_info": "Operator bypassed safety protocols"
  },
  "app_version": "1.0.0",
  "user_context": {
    "timestamp": 1705330200000,
    "locale": "en_US",
    "timezone": "America/Los_Angeles"
  }
}
```

## Testing

### Unit Tests
- Test feedback manager initialization
- Test category routing logic
- Test priority determination
- Test email template generation

### Integration Tests
- Test Firebase integration
- Test email intent creation
- Test error handling flows

### UI Tests
- Test feedback dialog interactions
- Test category selection
- Test form validation
- Test submission flows

## Security Considerations

1. **Data Privacy** - User emails and messages are handled securely
2. **Input Validation** - All user inputs are validated and sanitized
3. **Error Handling** - Graceful error handling prevents data leaks
4. **Email Security** - Email intents use Android's secure mechanisms

## Accessibility

- Full screen reader support
- Keyboard navigation
- High contrast support
- Content descriptions for all interactive elements
- Semantic labels for form fields

## Performance

- Lazy loading of feedback history
- Efficient image handling for attachments
- Background processing for submissions
- Minimal memory footprint

## Analytics & Metrics

Track key metrics:
- Feedback submission rates by category
- User engagement with feedback features
- Response times to critical feedback
- Popular feedback categories
- User satisfaction trends

## Future Enhancements

1. **In-App Screenshots** - Automatic screenshot capture
2. **Voice Feedback** - Audio message support
3. **Feedback History** - User's feedback tracking
4. **Response System** - Two-way communication
5. **Template Library** - Pre-filled feedback templates
6. **Offline Support** - Queue feedback when offline
7. **Multi-language** - Localized feedback forms
8. **Video Feedback** - Screen recording support

## Troubleshooting

### Common Issues

1. **Email client not opening**
   - Check if device has email app installed
   - Verify email intent configuration

2. **Feedback not submitting**
   - Check network connectivity
   - Verify Firebase configuration
   - Check error logs

3. **UI components not showing**
   - Verify Compose dependencies
   - Check theme configuration
   - Validate component imports

### Debug Logging

Enable debug logging for troubleshooting:

```kotlin
Log.d("UserFeedbackManager", "Feedback submitted successfully: $category")
Log.e("UserFeedbackManager", "Failed to submit feedback", exception)
```

## Best Practices

1. **Clear Categories** - Use descriptive feedback categories
2. **Quick Access** - Provide multiple access points
3. **Context Awareness** - Pre-fill relevant information
4. **Feedback Loop** - Acknowledge receipt and follow up
5. **Privacy First** - Respect user privacy preferences
6. **Mobile Optimized** - Design for mobile-first experience
7. **Industry Specific** - Cater to rigging industry needs

## Contact & Support

For questions about the feedback system implementation:

- **Technical Support**: tiatheone@protonmail.com
- **Business Inquiries**: jackjonas95@gmail.com
- **General Support**: support@riggerconnect.com

---

*This feedback system is designed to enhance user experience and safety in the rigging industry through comprehensive feedback collection and responsive support channels.*
