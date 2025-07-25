# RiggerConnect Android Accessibility Guide

## Overview
RiggerConnect is committed to providing an accessible experience for all users. This document outlines the accessibility features implemented in the Android app and provides guidance for developers to maintain and enhance accessibility support.

## Core Features

### 1. TalkBack Support
- All interactive elements have content descriptions
- Custom TalkBack announcements for important state changes
- Proper focus order for logical navigation
- Usage: Enable TalkBack in Android Settings > Accessibility

### 2. High Contrast Mode
- Enhanced color contrast for better visibility
- Clear text-to-background contrast ratios
- Custom high contrast theme available
- Usage: Enable in app settings under Accessibility > High Contrast Mode

### 3. Dynamic Font Sizing
- Support for system font size changes
- In-app font size controls
- Maintains layout integrity across different font sizes
- Usage: Adjust in app settings under Accessibility > Font Size

### 4. Voice Control
- Voice command support for common actions
- Custom voice actions for specific features
- Integration with Android's Voice Access
- Usage: Enable Voice Access in Android Settings > Accessibility

## Additional Features

### Gesture Alternatives
- Alternative input methods for all gestures
- Button alternatives for swipe actions
- Configurable gesture sensitivity
- Usage: Configure in app settings under Accessibility > Gesture Controls

### Testing
The app has been tested with:
- TalkBack screen reader
- Voice Access
- Switch Access
- Various font sizes and display settings
- High contrast mode

## Developer Guidelines

### Adding New Features
1. Implement content descriptions for all UI elements
2. Support both touch and keyboard navigation
3. Test with TalkBack enabled
4. Verify high contrast compatibility
5. Ensure proper focus order

### Code Examples
```kotlin
// Setting content description
view.contentDescription = "Button to submit form"

// Making element focusable
view.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES

// Announcing changes
announceForAccessibility("Form submitted successfully")
```

### Testing Checklist
- [ ] TalkBack navigation works logically
- [ ] All interactive elements are properly labeled
- [ ] High contrast mode displays correctly
- [ ] Font sizing doesn't break layouts
- [ ] Voice controls work as expected
- [ ] Gesture alternatives are functional

## Contact & Support
For accessibility-related issues or feedback, please contact:
- Tia (Developer) - tiatheone@protonmail.com
- Jack Jonas (Product Manager) - jackjonas95@gmail.com

## Compliance
This implementation follows WCAG 2.1 guidelines and Android accessibility best practices.
