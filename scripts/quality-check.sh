#!/bin/bash

# RiggerConnect Android - Quality Check Script
# Enterprise-grade static analysis and code quality checks

set -e  # Exit on any error

echo "🔍 Starting RiggerConnect Android Quality Checks..."
echo "=================================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Ensure we're in the project root
if [ ! -f "build.gradle.kts" ]; then
    print_error "Please run this script from the project root directory"
    exit 1
fi

# Clean previous builds
print_status "Cleaning previous builds..."
./gradlew clean

# Run ktlint check
print_status "Running ktlint code style checks..."
if ./gradlew ktlintCheck; then
    print_success "ktlint checks passed ✓"
else
    print_error "ktlint checks failed ✗"
    echo "Run './gradlew ktlintFormat' to auto-fix formatting issues"
    exit 1
fi

# Run spotless check
print_status "Running Spotless formatting checks..."
if ./gradlew spotlessCheck; then
    print_success "Spotless checks passed ✓"
else
    print_error "Spotless checks failed ✗"
    echo "Run './gradlew spotlessApply' to auto-fix formatting issues"
    exit 1
fi

# Run detekt static analysis
print_status "Running detekt static analysis..."
if ./gradlew detekt; then
    print_success "Detekt analysis passed ✓"
else
    print_error "Detekt analysis failed ✗"
    echo "Check the detekt report in build/reports/detekt/"
    exit 1
fi

# Run unit tests
print_status "Running unit tests..."
if ./gradlew test; then
    print_success "Unit tests passed ✓"
else
    print_error "Unit tests failed ✗"
    exit 1
fi

# Run lint checks
print_status "Running Android lint checks..."
if ./gradlew lint; then
    print_success "Android lint checks passed ✓"
else
    print_warning "Android lint found issues. Check build/reports/lint-results.html"
fi

# Build the project
print_status "Building the application..."
if ./gradlew assembleDebug; then
    print_success "Build completed successfully ✓"
else
    print_error "Build failed ✗"
    exit 1
fi

echo ""
echo "=================================================="
print_success "🎉 All quality checks completed successfully!"
echo "=================================================="
echo ""
echo "Reports are available at:"
echo "  • Detekt: build/reports/detekt/"
echo "  • ktlint: build/reports/ktlint/"
echo "  • Lint: build/reports/"
echo "  • Test: build/reports/tests/"
echo ""
