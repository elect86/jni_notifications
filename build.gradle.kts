import org.example.Arch
import org.example.NativeVariant
import org.example.OS
import org.example.addRuntimeVariantsFor

plugins {
    java
    `maven-publish`
    id("native-support")
}

repositories {
    mavenCentral()
}

dependencies {
    val lwjglVersion = "3.3.3"
    val lwjglNatives = "natives-linux"
    implementation("org.lwjgl", "lwjgl", lwjglVersion)
    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives, version = lwjglVersion)
}

addRuntimeVariantsFor(NativeVariant(OS.linux, Arch.x86_64, projectDir.resolve("libjni_notifications.so")),
                      NativeVariant(OS.linux, Arch.aarch_64, projectDir.resolve("libjni_notifications_raspi.so")),
    //                                    NativeVariant(OS.windows, Arch.x86_64, projectDir.resolve("jni_notificationsi.dll")),
                      NativeVariant(OS.osx, Arch.x86_64, projectDir.resolve("libjni_notifications.jnilib")),
                      NativeVariant(OS.osx, Arch.aarch_64, projectDir.resolve("libjni_notifications_arm64.jnilib")))

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.zoffcc.applications"
            artifactId = "jni_notifications"
            version = "0.0.3"
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "repo"
            url = uri("repo")
        }
    }
}
