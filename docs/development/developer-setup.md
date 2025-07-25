# Developer Setup Guide - RiggerConnect Android

## Prerequisites & System Requirements

### Hardware Requirements
- **RAM**: Minimum 8GB, Recommended 16GB+
- **Storage**: 10GB free space for Android Studio + SDKs
- **CPU**: x64 processor with hardware acceleration support
- **Graphics**: Dedicated GPU recommended for emulator performance

### Operating System Support
- ✅ **macOS**: 10.14 (Mojave) or later
- ✅ **Windows**: Windows 10 (64-bit) or later
- ✅ **Linux**: Ubuntu 18.04+, CentOS 7+, Debian 10+

## Software Installation

### 1. Java Development Kit (JDK)
```bash
# macOS (using Homebrew)
brew install openjdk@17
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc

# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# Windows (using Chocolatey)
choco install openjdk17

# Verify installation
java -version
javac -version
```

### 2. Android Studio Installation

#### macOS
```bash
# Download from https://developer.android.com/studio
# Or use Homebrew Cask
brew install --cask android-studio
```

#### Windows
```powershell
# Download from https://developer.android.com/studio
# Or use Chocolatey
choco install androidstudio
```

#### Linux (Ubuntu/Debian)
```bash
# Download and install
wget https://redirector.gvt1.com/edgedl/android/studio/ide-zips/2022.2.1.20/android-studio-2022.2.1.20-linux.tar.gz
tar -xzf android-studio-*.tar.gz
sudo mv android-studio /opt/
sudo ln -s /opt/android-studio/bin/studio.sh /usr/local/bin/android-studio

# Install dependencies
sudo apt install libc6:i386 libncurses5:i386 libstdc++6:i386 lib32z1 libbz2-1.0:i386
```

### 3. Android SDK Configuration

#### Initial Setup
1. Open Android Studio
2. Complete the setup wizard
3. Install recommended SDK components

#### Command Line Configuration
```bash
# Add to ~/.bashrc or ~/.zshrc
export ANDROID_HOME=$HOME/Library/Android/sdk  # macOS
export ANDROID_HOME=$HOME/Android/Sdk          # Linux
export ANDROID_HOME=%LOCALAPPDATA%\\Android\\Sdk  # Windows

export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin

# Verify installation
adb version
emulator -version
```

### 4. Required SDK Components
```bash
# Install via command line (after setting up ANDROID_HOME)
sdkmanager "platform-tools" "platforms;android-33" "build-tools;33.0.2"
sdkmanager "system-images;android-33;google_apis;x86_64"
sdkmanager "emulator"

# Or install via Android Studio:
# Tools → SDK Manager → Install required components
```

### 5. Version Control Setup
```bash
# Git installation
# macOS
brew install git

# Ubuntu/Debian
sudo apt install git

# Windows
choco install git

# Git configuration
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
git config --global init.defaultBranch main

# SSH key setup for GitHub
ssh-keygen -t ed25519 -C "your.email@example.com"
cat ~/.ssh/id_ed25519.pub
# Add the output to your GitHub SSH keys
```

## Project Setup

### 1. Clone Repository
```bash
# Using SSH (recommended)
git clone git@github.com:chasewhiterabbit/RiggerConnect-android.git
cd RiggerConnect-android

# Using HTTPS
git clone https://github.com/chasewhiterabbit/RiggerConnect-android.git
cd RiggerConnect-android
```

### 2. Environment Configuration
```bash
# Copy environment template
cp app/src/main/res/values/config_template.xml app/src/main/res/values/config.xml

# Create local properties file
echo "sdk.dir=$ANDROID_HOME" > local.properties

# Create keystore properties (for signing)
touch keystore.properties
```

#### Configuration Files

**app/src/main/res/values/config.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- API Configuration -->
    <string name="api_base_url">http://10.0.2.2:3001/api/</string>
    <string name="api_base_url_release">https://api.rigger.sxc.codes/api/</string>
    
    <!-- Supabase Configuration -->
    <string name="supabase_url">https://your-project.supabase.co</string>
    <string name="supabase_anon_key">your-supabase-anon-key</string>
    
    <!-- Firebase Configuration -->
    <string name="firebase_project_id">rigger-connect-dev</string>
    
    <!-- Maps API Key -->
    <string name="google_maps_key">your-google-maps-api-key</string>
    
    <!-- Analytics -->
    <bool name="analytics_enabled">false</bool>
    <bool name="crash_reporting_enabled">false</bool>
    
    <!-- Debug Settings -->
    <bool name="debug_mode">true</bool>
    <bool name="mock_api_enabled">false</bool>
</resources>
```

**keystore.properties**
```properties
# Development keystore (for debug builds)
keyAlias=rigger-debug
keyPassword=debug123
storeFile=debug.keystore
storePassword=debug123

# Production keystore (for release builds)
# releaseKeyAlias=rigger-release
# releaseKeyPassword=your-secure-password
# releaseStoreFile=release.keystore
# releaseStorePassword=your-secure-password
```

### 3. Firebase Setup (Optional for Development)
```bash
# Download google-services.json from Firebase Console
# Place in app/ directory

# For development, you can use a mock file:
cp app/google-services-mock.json app/google-services.json
```

### 4. Gradle Sync and Build
```bash
# Sync project with Gradle files
./gradlew sync

# Clean and build debug version
./gradlew clean assembleDebug

# Run tests
./gradlew test

# Install debug APK on connected device
./gradlew installDebug
```

## Development Environment Setup

### 1. Android Studio Configuration

#### Recommended Plugins
1. **Kotlin** (built-in)
2. **Android Jetpack Compose** (built-in)
3. **Git Integration** (built-in)
4. **Detekt** - Code analysis
5. **Rainbow Brackets** - Bracket matching
6. **GitToolBox** - Enhanced Git integration
7. **ADB Idea** - ADB commands integration

#### Studio Settings
```
File → Settings (Preferences on macOS)

Editor → Code Style → Kotlin
- Set indent size: 4 spaces
- Continuation indent: 8 spaces
- Enable format on save

Editor → Inspections
- Enable Kotlin inspections
- Enable Android Lint checks

Build → Compiler
- Build process heap size: 2048 MB
- Enable parallel compilation
```

### 2. Emulator Setup
```bash
# Create AVD (Android Virtual Device)
avdmanager create avd -n "Pixel_6_API_33" -k "system-images;android-33;google_apis;x86_64"

# Start emulator
emulator -avd Pixel_6_API_33

# Or use Android Studio:
# Tools → AVD Manager → Create Virtual Device
```

#### Recommended Test Devices
- **Pixel 6 (API 33)** - Latest Android features
- **Pixel 4 (API 29)** - Common target device
- **Nexus 5X (API 23)** - Minimum supported version
- **Tablet (API 33)** - Tablet layout testing

### 3. Physical Device Setup
```bash
# Enable Developer Options on device:
# Settings → About Phone → Tap Build Number 7 times

# Enable USB Debugging:
# Settings → Developer Options → USB Debugging

# Verify device connection
adb devices
```

## Code Quality Tools

### 1. Static Analysis Setup
```bash
# Detekt configuration
./gradlew detekt

# KtLint formatting
./gradlew ktlintFormat

# Android Lint checks
./gradlew lint
```

### 2. Pre-commit Hooks
```bash
# Install pre-commit hook
cp scripts/pre-commit .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit

# The hook will run:
# - KtLint formatting
# - Detekt analysis
# - Unit tests
# - Build verification
```

### 3. Code Coverage
```bash
# Generate test coverage report
./gradlew jacocoTestReport

# View report
open app/build/reports/jacoco/jacocoTestReport/html/index.html
```

## Development Workflow

### 1. Feature Development
```bash
# Create feature branch
git checkout -b feature/job-search-enhancements

# Make changes following coding standards
# Write unit tests for new functionality
./gradlew test

# Run code quality checks
./gradlew detekt ktlintCheck

# Commit with conventional commit messages
git add .
git commit -m "feat: add advanced job search filters"

# Push and create pull request
git push origin feature/job-search-enhancements
```

### 2. Testing Strategy
```bash
# Unit tests
./gradlew test

# Integration tests
./gradlew connectedAndroidTest

# UI tests (requires running emulator)
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.UITests

# Manual testing checklist
# - Test on multiple screen sizes
# - Test offline functionality
# - Test with different Android versions
# - Test accessibility features
```

### 3. Debugging Techniques

#### Android Studio Debugger
1. Set breakpoints in code
2. Run app in debug mode (Shift+F9)
3. Use variable inspection and expression evaluation

#### Logging Best Practices
```kotlin
// Use Timber for logging
Timber.d("Debug message")
Timber.i("Info message")
Timber.w("Warning message")
Timber.e("Error message")

// For production builds, only log warnings and errors
if (BuildConfig.DEBUG) {
    Timber.plant(Timber.DebugTree())
} else {
    Timber.plant(CrashReportingTree())
}
```

#### Network Debugging
```bash
# Use ADB to monitor network requests
adb shell am start -a android.intent.action.VIEW -d "https://api.rigger.sxc.codes/health"

# Use Flipper for network inspection (if configured)
# Install Flipper desktop app and add network plugin
```

## Performance Optimization

### 1. Build Performance
```bash
# Enable Gradle daemon
echo "org.gradle.daemon=true" >> ~/.gradle/gradle.properties

# Increase heap size
echo "org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=512m" >> ~/.gradle/gradle.properties

# Enable parallel builds
echo "org.gradle.parallel=true" >> gradle.properties

# Enable build cache
echo "org.gradle.caching=true" >> gradle.properties
```

### 2. App Performance Monitoring
```kotlin
// Use Android Profiler in Android Studio
// CPU, Memory, Network, Energy profiling

// Add performance monitoring
class PerformanceMonitor {
    fun startTrace(name: String) {
        if (BuildConfig.DEBUG) {
            android.os.Trace.beginSection(name)
        }
    }
    
    fun endTrace() {
        if (BuildConfig.DEBUG) {
            android.os.Trace.endSection()
        }
    }
}
```

## Troubleshooting Common Issues

### 1. Build Issues
```bash
# Clear Gradle cache
./gradlew clean
rm -rf ~/.gradle/caches/

# Invalidate Android Studio caches
# File → Invalidate Caches and Restart

# Update Gradle wrapper
./gradlew wrapper --gradle-version=8.1

# Sync project files
./gradlew sync
```

### 2. Emulator Issues
```bash
# Cold boot emulator
emulator -avd Pixel_6_API_33 -no-snapshot-load

# Wipe emulator data
emulator -avd Pixel_6_API_33 -wipe-data

# Check hardware acceleration
emulator -accel-check
```

### 3. Device Connection Issues
```bash
# Restart ADB server
adb kill-server
adb start-server

# Check USB drivers (Windows)
# Update Android USB drivers in Device Manager

# Check device authorization
adb devices
# If "unauthorized", check device for authorization dialog
```

### 4. Memory Issues
```bash
# Increase Android Studio memory
# Help → Edit Custom VM Options
-Xms2g
-Xmx8g
-XX:ReservedCodeCacheSize=1g

# Monitor memory usage
# Help → Activity Monitor
```

## External Integrations Setup

### 1. Google Maps Setup
```bash
# Get API key from Google Cloud Console
# Enable Maps SDK for Android
# Add API key to config.xml

# Test Maps integration
./gradlew assembleDebug
# Verify maps load correctly in app
```

### 2. Firebase Integration
```bash
# Download google-services.json
# Add Firebase SDK dependencies
# Initialize Firebase in Application class

# Test push notifications
# Use Firebase Console to send test message
```

### 3. Backend API Integration
```bash
# Configure API endpoints in config.xml
# Test API connectivity

curl -X GET "http://10.0.2.2:3001/api/health"
# Should return 200 OK with health status
```

## Security Setup

### 1. Code Obfuscation
```bash
# Enable ProGuard/R8 for release builds
# Configure proguard-rules.pro
# Test obfuscated builds

./gradlew assembleRelease
```

### 2. Security Testing
```bash
# Run security lint checks
./gradlew lint

# Check for hardcoded secrets
grep -r "password\|secret\|key" app/src/main/ || echo "No hardcoded secrets found"

# Verify network security config
# Check app/src/main/res/xml/network_security_config.xml
```

## Continuous Integration Setup

### 1. Local CI Testing
```bash
# Run full CI pipeline locally
./scripts/ci-test.sh

# This script runs:
# - Lint checks
# - Unit tests
# - Build verification
# - Security checks
```

### 2. GitLab CI Integration
```yaml
# .gitlab-ci.yml configuration for Android
stages:
  - build
  - test
  - security
  - deploy

android_build:
  stage: build
  image: openjdk:17-jdk
  before_script:
    - apt-get update -qy
    - apt-get install -y android-sdk
  script:
    - ./gradlew assembleDebug
  artifacts:
    paths:
      - app/build/outputs/apk/
```

## Getting Help

### Internal Resources
1. Check [Troubleshooting Guide](../troubleshooting.md)
2. Review [Architecture Documentation](../architecture/)
3. Browse [Code Examples](../examples/)

### External Support
- **Android Development**: android-dev@chasewhiterabbit.org
- **API Integration**: api-support@chasewhiterabbit.org
- **Bug Reports**: Create GitHub issue with template

### Community Resources
- **Stack Overflow**: Tag questions with `rigger-connect-android`
- **GitHub Discussions**: Project-specific discussions
- **Android Developer Documentation**: Official Android guides

---

*ChaseWhiteRabbit NGO - Empowering Blue-Collar Excellence Through Ethical Mobile Technology*
