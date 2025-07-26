pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "RiggerConnect"

// Presentation Layer
include(":app")

// Domain Layer (Core Business Logic)
include(":domain")

// Data Layer
include(":data")

// Core/Shared Modules
include(":core-ui")
include(":core-common")
include(":core-navigation")
include(":core-network")
include(":core-database")

// Feature Modules
include(":feature-auth")
include(":feature-jobs")
include(":feature-profile")
include(":feature-payments")
include(":feature-onboarding")
include(":feature-compliance")

// Testing Support
include(":common-test")
