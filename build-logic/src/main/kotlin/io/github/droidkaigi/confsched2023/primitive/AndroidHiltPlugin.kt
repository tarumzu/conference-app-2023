package io.github.droidkaigi.confsched2023.primitive

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

@Suppress("unused")
class AndroidHiltPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("kotlin-kapt")
                apply("dagger.hilt.android.plugin")
            }

            android {
                packagingOptions {
                    resources {
                        excludes += "META-INF/gradle/incremental.annotation.processors"
                    }
                }
            }
            dependencies {
                implementation(libs.findLibrary("daggerHiltAndroid"))
                // https://issuetracker.google.com/issues/237567009
                implementation(libs.findLibrary("androidxFragment"))
                kapt(libs.findLibrary("daggerHiltAndroidCompiler"))
                testImplementation(libs.findLibrary("daggerHiltAndroidTesting"))
                kaptTest(libs.findLibrary("daggerHiltAndroidTesting"))
            }
            extensions.configure<KaptExtension> {
                correctErrorTypes = true
            }
        }
    }
}
