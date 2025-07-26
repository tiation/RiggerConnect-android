#!/bin/bash

# RiggerConnect Android - Auto Format Script
# Automatically fixes code formatting issues

set -e  # Exit on any error

echo "🎨 Auto-formatting RiggerConnect Android code..."
echo "=============================================="

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

# Ensure we're in the project root
if [ ! -f "build.gradle.kts" ]; then
    echo -e "${RED}[ERROR]${NC} Please run this script from the project root directory"
    exit 1
fi

# Run ktlint format
print_status "Running ktlint auto-format..."
./gradlew ktlintFormat
print_success "ktlint formatting completed ✓"

# Run spotless apply
print_status "Running Spotless auto-format..."
./gradlew spotlessApply
print_success "Spotless formatting completed ✓"

echo ""
echo "=============================================="
print_success "🎉 Auto-formatting completed!"
echo "=============================================="
echo ""
echo "All code formatting issues have been automatically fixed."
echo "Please review the changes and commit them if appropriate."
echo ""
