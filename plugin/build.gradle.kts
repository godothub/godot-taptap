import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

val pluginName = "GodotTapTapSDK"
val tapSdkVersion = "4.10.7"
val pluginPackageName = "com.sakuya.godot_taptap"

android {
    namespace = pluginPackageName
    compileSdk = 33

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        minSdk = 21

        manifestPlaceholders["godotPluginName"] = pluginName
        manifestPlaceholders["godotPluginPackageName"] = pluginPackageName
        buildConfigField("String", "GODOT_PLUGIN_NAME", "\"${pluginName}\"")
        setProperty("archivesBaseName", pluginName)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("org.godotengine:godot:4.2.0.stable")

    implementation("com.taptap.sdk:tap-core:$tapSdkVersion")
    implementation("com.taptap.sdk:tap-login:$tapSdkVersion")
    implementation("com.taptap.sdk:tap-compliance:$tapSdkVersion")
    implementation("com.taptap.sdk:tap-moment:$tapSdkVersion")
    implementation("com.taptap.sdk:tap-update:$tapSdkVersion")
    implementation("com.taptap.sdk:tap-license:$tapSdkVersion")
    implementation("com.taptap.sdk:tap-share:$tapSdkVersion")
    implementation("com.taptap.sdk:tap-review:$tapSdkVersion")
    implementation("com.taptap.sdk:tap-achievement:$tapSdkVersion")
    implementation("com.taptap.sdk:tap-rep:$tapSdkVersion")
    implementation("com.taptap.sdk:tap-cloudsave:$tapSdkVersion")
    implementation("com.taptap.sdk:tap-relation:$tapSdkVersion")
    implementation("com.taptap.sdk:tap-leaderboard-androidx:$tapSdkVersion")
}

// BUILD TASKS DEFINITION
val copyDebugAARToDemoAddons by tasks.registering(Copy::class) {
    description = "Copies the generated debug AAR binary to the plugin's addons directory"
    from("build/outputs/aar")
    include("$pluginName-debug.aar")
    into("demo/addons/$pluginName/bin/debug")
}

val copyReleaseAARToDemoAddons by tasks.registering(Copy::class) {
    description = "Copies the generated release AAR binary to the plugin's addons directory"
    from("build/outputs/aar")
    include("$pluginName-release.aar")
    into("demo/addons/$pluginName/bin/release")
}

val cleanDemoAddons by tasks.registering(Delete::class) {
    delete("demo/addons/$pluginName")
}

val copyAddonsToDemo by tasks.registering(Copy::class) {
    description = "Copies the export scripts templates to the plugin's addons directory"

    dependsOn(cleanDemoAddons)
    finalizedBy(copyDebugAARToDemoAddons)
    finalizedBy(copyReleaseAARToDemoAddons)

    from("export_scripts_template")
    into("demo/addons/$pluginName")
}

tasks.named("assemble").configure {
    finalizedBy(copyAddonsToDemo)
}

tasks.named<Delete>("clean").apply {
    dependsOn(cleanDemoAddons)
}
