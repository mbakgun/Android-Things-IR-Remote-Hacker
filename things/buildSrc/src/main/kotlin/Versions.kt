/**
 * Find which updates are available by running
 *     `$ ./gradlew buildSrcVersions`
 * This will only update the comments.
 *
 * YOU are responsible for updating manually the dependency version. */
object Versions {
    const val androidx_lifecycle: String = "2.0.0"

    const val androidx_room: String = "2.1.0"

    const val com_android_tools_build_gradle: String = "3.4.2"

    const val lint_gradle: String = "26.4.2"

    const val usbserial: String = "6.1.0"

    const val play_services_nearby: String = "17.0.0"

    const val androidthings: String = "1.0"

    const val gson: String = "2.8.5"

    const val com_google_dagger: String = "2.24"

    const val de_fayard_buildsrcversions_gradle_plugin: String = "0.3.2"

    const val org_jetbrains_kotlin: String = "1.3.41"

    const val org_jetbrains_kotlinx: String = "1.2.1"

    /**
     *
     *   To update Gradle, edit the wrapper file at path:
     *      ./gradle/wrapper/gradle-wrapper.properties
     */
    object Gradle {
        const val runningVersion: String = "5.1.1"

        const val currentVersion: String = "5.5.1"

        const val nightlyVersion: String = "5.7-20190805220111+0000"

        const val releaseCandidate: String = "5.6-rc-1"
    }
}
