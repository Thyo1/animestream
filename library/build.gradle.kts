import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.dokka.gradle.engine.parameters.KotlinPlatform
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("com.android.library")
    id("com.codingfeline.buildkonfig")
    id("org.jetbrains.dokka")
}

val javaTarget = JvmTarget.fromTarget(libs.versions.jvmTarget.get())

kotlin {
    version = "1.0.0"
    androidTarget()
    jvm()

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain.dependencies {
            // Mengubah implementation menjadi api agar AIDE/AndroidIDE mendeteksinya secara penuh
            api(libs.nicehttp) 
            api(libs.jackson.module.kotlin) 
            api(libs.kotlinx.coroutines.core)
            api(libs.fuzzywuzzy) 
            api(libs.rhino) 
            api(libs.newpipeextractor)
            api(libs.tmdb.java) 
            // Menambahkan Jsoup secara eksplisit untuk mengatasi error 'document' dan 'text'
            api("org.jsoup:jsoup:1.17.2") 
        }
        
        // Membantu kompiler KMP AndroidIDE yang sering bug kehilangan library
        androidMain.dependencies {
            api(libs.nicehttp)
            api("org.jsoup:jsoup:1.17.2")
        }
    }
}

tasks.withType<KotlinJvmCompile> {
    compilerOptions {
        jvmTarget.set(javaTarget)
    }
}

buildkonfig {
    packageName = "com.thyo.api"
    exposeObjectWithName = "BuildConfig"

    defaultConfigs {
        val isDebug = kotlin.runCatching { extra.get("isDebug") }.getOrNull() == true
        if (isDebug) {
            logger.quiet("Compiling library with debug flag")
        } else {
            logger.quiet("Compiling library with release flag")
        }
        buildConfigField(FieldSpec.Type.BOOLEAN, "DEBUG", isDebug.toString())

        val localProperties = gradleLocalProperties(rootDir, project.providers)

        buildConfigField(
            FieldSpec.Type.STRING,
            "MDL_API_KEY", (System.getenv("MDL_API_KEY") ?: localProperties["mdl.key"]).toString()
        )
    }
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    namespace = "com.thyo.api"

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(javaTarget.target)
        targetCompatibility = JavaVersion.toVersion(javaTarget.target)
    }

    @Suppress("UnstableApiUsage")
    testOptions {
        targetSdk = libs.versions.targetSdk.get().toInt()
    }

    lint {
        targetSdk = libs.versions.targetSdk.get().toInt()
    }
}

publishing {
    publications {
        withType<MavenPublication> {
            groupId = "com.thyo.api"
        }
    }
}

dokka {
    moduleName = "Library"
    dokkaSourceSets {
        configureEach {
            analysisPlatform = KotlinPlatform.AndroidJVM
            documentedVisibilities(
                VisibilityModifier.Public,
                VisibilityModifier.Protected
            )

            sourceLink {
                localDirectory = file("..")
                // Link Dokka juga sudah kita ubah ke animestream
                remoteUrl("https://github.com/thyo1/animestream/tree/master")
                remoteLineSuffix = "#L"
            }
        }
    }
}
