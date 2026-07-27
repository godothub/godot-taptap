pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven (  "https://maven.aliyun.com/repository/jcenter" )
        maven (  "https://maven.aliyun.com/repository/google" )
        maven (  "https://maven.aliyun.com/repository/central" )
        maven (  "https://maven.aliyun.com/repository/gradle-plugin" )

        flatDir {
            dirs("libs")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven (  "https://maven.aliyun.com/repository/jcenter" )
        maven (  "https://maven.aliyun.com/repository/google" )
        maven (  "https://maven.aliyun.com/repository/central" )
        maven (  "https://maven.aliyun.com/repository/gradle-plugin" )
        maven("https://plugins.gradle.org/m2/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")

        flatDir {
            dirs("libs")
        }
    }
}

// TODO: Update project"s name.
rootProject.name = "GodotTapTapSDK"
include(":plugin")
