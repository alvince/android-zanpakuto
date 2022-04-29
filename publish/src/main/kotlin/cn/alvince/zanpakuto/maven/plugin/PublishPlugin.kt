package cn.alvince.zanpakuto.maven.plugin

import cn.alvince.zanpakuto.maven.dsl.PublishExtension
import cn.alvince.zanpakuto.maven.gradle.android.getAndroid
import cn.alvince.zanpakuto.maven.gradle.android.isAndroidLib
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.bundling.Jar

/**
 * Created by alvince on 2022/1/7
 *
 * @author alvincezhang@didiglobal.com
 */
class PublishPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create("publish", PublishExtension::class.java)
        if (project.isAndroidLib) {
            project.pluginManager.apply("maven-plugin")
            project.prepareTask()
        }
    }

    private fun Project.prepareTask() {
        tasks.register<Jar>("artifactSourcesJar") {
            archiveClassifier.set("sources")
            project.getAndroid<LibraryExtension>().also {
                it.sourceSets.getByName("main").java.srcDirs
            }
        }
    }

    private inline fun <reified T : Task> TaskContainer.register(name: String, crossinline action: T.() -> Unit) {
        this.register(name, T::class.java) { action(it as T) }
    }
}
