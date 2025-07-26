#!/bin/bash

# RiggerConnect Android - Gradle Tasks Helper
# Common Gradle tasks for development workflow

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_usage() {
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Available commands:"
    echo "  quality-check     - Run all quality checks (detekt, ktlint, spotless)"
    echo "  format           - Auto-format code (ktlint, spotless)"
    echo "  detekt           - Run detekt static analysis"
    echo "  detekt-baseline  - Generate detekt baseline"
    echo "  ktlint           - Run ktlint style check"
    echo "  ktlint-format    - Auto-format with ktlint"
    echo "  spotless         - Run spotless format check"
    echo "  spotless-apply   - Apply spotless formatting"
    echo "  test             - Run unit tests"
    echo "  test-coverage    - Run tests with coverage report"
    echo "  build-debug      - Build debug APK"
    echo "  build-release    - Build release APK"
    echo "  lint             - Run Android lint"
    echo "  clean            - Clean build artifacts"
    echo "  dependencies     - Show dependency tree"
    echo "  help             - Show this help message"
}

print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

case "$1" in
    "quality-check")
        print_status "Running complete quality check..."
        ./scripts/quality-check.sh
        ;;
    
    "format")
        print_status "Auto-formatting code..."
        ./scripts/auto-format.sh
        ;;
    
    "detekt")
        print_status "Running detekt static analysis..."
        ./gradlew detekt
        print_success "Detekt analysis completed. Check build/reports/detekt/"
        ;;
    
    "detekt-baseline")
        print_status "Generating detekt baseline..."
        ./gradlew detektBaseline
        print_success "Detekt baseline generated at config/detekt/baseline.xml"
        ;;
    
    "ktlint")
        print_status "Running ktlint style check..."
        ./gradlew ktlintCheck
        print_success "ktlint check completed"
        ;;
    
    "ktlint-format")
        print_status "Auto-formatting with ktlint..."
        ./gradlew ktlintFormat
        print_success "ktlint formatting completed"
        ;;
    
    "spotless")
        print_status "Running spotless format check..."
        ./gradlew spotlessCheck
        print_success "Spotless check completed"
        ;;
    
    "spotless-apply")
        print_status "Applying spotless formatting..."
        ./gradlew spotlessApply
        print_success "Spotless formatting applied"
        ;;
    
    "test")
        print_status "Running unit tests..."
        ./gradlew test
        print_success "Tests completed. Check build/reports/tests/"
        ;;
    
    "test-coverage")
        print_status "Running tests with coverage..."
        ./gradlew test jacocoTestReport
        print_success "Coverage report generated. Check build/reports/jacoco/"
        ;;
    
    "build-debug")
        print_status "Building debug APK..."
        ./gradlew assembleDebug
        print_success "Debug APK built. Check app/build/outputs/apk/debug/"
        ;;
    
    "build-release")
        print_status "Building release APK..."
        ./gradlew assembleRelease
        print_success "Release APK built. Check app/build/outputs/apk/release/"
        ;;
    
    "lint")
        print_status "Running Android lint..."
        ./gradlew lint
        print_success "Lint completed. Check build/reports/lint-results.html"
        ;;
    
    "clean")
        print_status "Cleaning build artifacts..."
        ./gradlew clean
        print_success "Clean completed"
        ;;
    
    "dependencies")
        print_status "Showing dependency tree..."
        ./gradlew dependencies
        ;;
    
    "help"|"")
        print_usage
        ;;
    
    *)
        print_error "Unknown command: $1"
        echo ""
        print_usage
        exit 1
        ;;
esac
