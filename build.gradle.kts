import java.nio.file.Paths
import kotlin.io.path.absolutePathString

plugins {
    java
    `maven-publish`
}

data class NativeVariant(val os: String, val arch: String, val classifier: String)

private val nativeVariants = listOf(
    NativeVariant(OperatingSystemFamily.LINUX, "x86-64", "natives-linux-x64"),
    NativeVariant(OperatingSystemFamily.LINUX, "aarm64", "natives-linux-aarm64"),
    NativeVariant(OperatingSystemFamily.MACOS, "x86-64", "natives-macos-x64"),
    NativeVariant(OperatingSystemFamily.MACOS, "aarch64", "natives-macos-aarch64"),
//    NativeVariant(OperatingSystemFamily.WINDOWS, "x86-64", "natives-windows-x64"),
)
println(Paths.get("").absolutePathString())
// Add a different runtime variant for each platform
val javaComponent = components.findByName("java") as AdhocComponentWithVariants
nativeVariants.forEach { variantDefinition ->
    // Creation of the native jars
    val nativeJar = tasks.create<Jar>(variantDefinition.classifier + "Jar") {
        archiveClassifier = variantDefinition.classifier
        actions = listOf()
        doLast {
            val lib = when (variantDefinition.os) {
                OperatingSystemFamily.LINUX -> when (variantDefinition.arch) {
                    "x86-64" -> "libjni_notifications.so"
                    else -> "libjni_notifications_raspi.so"
                }
                OperatingSystemFamily.MACOS -> when (variantDefinition.arch) {
                    "x86-64" -> "libjni_notifications.jnilib"
                    else -> "libjni_notifications_arm64.jnilib"
                }
                OperatingSystemFamily.WINDOWS -> "jni_notificationsi.dll"
                else -> error("invalid os ${variantDefinition.os}")
            }
            archiveFile.get().asFile.writeBytes(projectDir.resolve(lib).readBytes())
        }
    }

    val nativeRuntimeElements = configurations.consumable(variantDefinition.classifier + "RuntimeElements") {
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME)) // this is also by default
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY)) // this is also by default
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR)) // this is also by default
            attribute(OperatingSystemFamily.OPERATING_SYSTEM_ATTRIBUTE, objects.named(variantDefinition.os))
            attribute(MachineArchitecture.ARCHITECTURE_ATTRIBUTE, objects.named(variantDefinition.arch))
        }
        outgoing {
            artifact(tasks.named("jar"))
            artifact(nativeJar)
        }
        extendsFrom(configurations["runtimeElements"])
    }
    javaComponent.addVariantsFromConfiguration(nativeRuntimeElements.get()) {}
}

// don't publish the default runtime without native jar
javaComponent.withVariantsFromConfiguration(configurations["runtimeElements"]) {
    skip()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.zoffcc.applications"
            artifactId = "jni_notifications"
            version = "0.0.1"
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
