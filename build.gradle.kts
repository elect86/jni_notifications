import org.nativeSupport.Arch
import org.nativeSupport.OS
import org.nativeSupport.addRuntimeVariantsFor
import org.nativeSupport.nativeVariantOf

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

addRuntimeVariantsFor(nativeVariantOf(OS.linux, Arch.x86_64, "libjni_notifications.so"),
                      nativeVariantOf(OS.linux, Arch.aarch_64, "libjni_notifications_raspi.so"),
    //                                    NativeVariant(OS.windows, Arch.x86_64, "jni_notificationsi.dll"),
                      nativeVariantOf(OS.osx, Arch.x86_64, "libjni_notifications.jnilib"),
                      nativeVariantOf(OS.osx, Arch.aarch_64, "libjni_notifications_arm64.jnilib"))

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.zoffcc.applications"
            artifactId = "jni_notifications"
            version = "0.0.5"
            from(components["java"])
        }
    }
    repositories {
//        maven {
//            name = "sciJava"
//            credentials(PasswordCredentials::class)
//            url = uri("https://maven.scijava.org/content/repositories/releases")
//        }
        maven {
            name = "repo"
            url = uri("repo")
        }
    }
}
