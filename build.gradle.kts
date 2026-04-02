import com.android.build.gradle.LibraryExtension
import java.util.Properties

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
}

data class PublishModule(
    val artifactId: String,
    val versionName: String,
    val pomName: String,
    val pomDescription: String,
)

val localProps = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        localFile.inputStream().use(::load)
    }
}

fun propFromConfigOrEnv(project: Project, name: String): String {
    return localProps.getProperty(name)
        ?: project.findProperty(name)?.toString()
        ?: System.getenv(name)
        ?: ""
}

val publishModules = mapOf(
    "core" to PublishModule("core", "1.0.1", "Zanpakuto core", "Zanpakuto core component"),
    "lifecycle" to PublishModule(
        "lifecycle",
        "1.0.0.f",
        "Zanpakuto lifecycle ext",
        "Zanpakuto lifecycle support extensions"
    ),
    "view" to PublishModule(
        "view",
        "0.1-SNAPSHOT",
        "Zanpakuto android-view ext",
        "Zanpakuto android views extensions"
    ),
    "viewbinding" to PublishModule(
        "viewbinding",
        "0.1-SNAPSHOT",
        "Zanpakuto view-binding ext",
        "Zanpakuto view-binding support extensions"
    ),
    "databinding" to PublishModule(
        "databinding",
        "0.1-SNAPSHOT",
        "Zanpakuto data-binding ext",
        "Zanpakuto data-binding support extensions"
    ),
    "reactivex-rxjava2" to PublishModule(
        "rxjava2",
        "1.0.0-SNAPSHOT",
        "Zanpakuto RxJava2 ext",
        "Zanpakuto RxJava2 support extensions"
    ),
    "serialization-gson" to PublishModule(
        "serialization-gson",
        "0.0.1-SNAPSHOT",
        "Zanpakuto gson serialization ext",
        "Zanpakuto gson serialization extensions"
    ),
)

subprojects {
    group = "cn.alvince.zanpakuto"

    publishModules[name]?.let { module ->
        version = module.versionName
    }

    plugins.withId("com.android.library") {
        pluginManager.apply("maven-publish")

        extensions.configure<LibraryExtension>("android") {
            buildFeatures {
                buildConfig = false
            }
            publishing {
                singleVariant("release") {
                    withSourcesJar()
                    withJavadocJar()
                }
            }
        }

        val module = publishModules[name] ?: return@withId

        extensions.configure<PublishingExtension>("publishing") {
            publications {
                register("release", MavenPublication::class.java) {
                    artifactId = module.artifactId
                    groupId = project.group.toString()
                    version = project.version.toString()
                    afterEvaluate {
                        from(components.getByName("release"))
                    }

                    pom {
                        name.set(module.pomName)
                        description.set(module.pomDescription)
                        url.set("https://github.com/alvince/android-zanpakuto")

                        licenses {
                            license {
                                name.set("The Apache License, Version 2.0")
                                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                            }
                        }
                        developers {
                            developer {
                                id.set("alvince")
                                name.set("Alvince")
                                email.set("alvince.zy@gmail.com")
                            }
                        }
                    }
                }
            }

            val repoUsername = propFromConfigOrEnv(project, "ossrhUsername")
            val repoPassword = propFromConfigOrEnv(project, "ossrhPassword")
            if (repoUsername.isNotBlank() && repoPassword.isNotBlank()) {
                repositories {
                    maven {
                        name = "sonatype"
                        val releasesRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/releases/")
                        val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                        url = if (project.version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
                        credentials {
                            username = repoUsername
                            password = repoPassword
                        }
                    }
                }
            }
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
