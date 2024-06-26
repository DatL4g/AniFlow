import org.gradle.api.JavaVersion

object CompileOptions {
    val sourceCompatibility = JavaVersion.VERSION_17
    val targetCompatibility = JavaVersion.VERSION_17
    val jvmTargetString = targetCompatibility.toString()
    val jvmTargetVersion = when {
        targetCompatibility.isJava5 -> 5
        targetCompatibility.isJava6 -> 6
        targetCompatibility.isJava7 -> 7
        targetCompatibility.isJava8 -> 8
        else -> targetCompatibility.majorVersion.toIntOrNull() ?: (targetCompatibility.ordinal + 1)
    }
}