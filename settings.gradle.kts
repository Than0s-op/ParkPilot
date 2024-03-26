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
        // QR generator (zxing), avatar
        maven ("https://jitpack.io" )
    }
}

rootProject.name = "ParkPilot"
include(":app")
 