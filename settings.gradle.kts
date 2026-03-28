import java.util.Properties

val localProps = Properties().apply {
    val file = rootDir.resolve("local.properties")
    if (file.exists()) file.inputStream().use(::load)
}

fun githubUser(): String? =
    localProps.getProperty("gpr.user")
        ?: providers.gradleProperty("gpr.user").orNull
        ?: System.getenv("GITHUB_ACTOR")

fun githubToken(): String? =
    localProps.getProperty("gpr.key")
        ?: providers.gradleProperty("gpr.key").orNull
        ?: System.getenv("GITHUB_TOKEN")

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        maven("https://jitpack.io") {
            metadataSources {
                mavenPom()
                artifact()
            }
        }
        maven {
            url = uri("https://maven.pkg.github.com/SilvaTechB/silva-patcher")
            credentials {
                val hardcodedUser = ""
                val hardcodedToken = ""
                val gprUser: String? = providers.gradleProperty("gpr.user").orNull
                val gprKey: String? = providers.gradleProperty("gpr.key").orNull

                username = (if (hardcodedUser.isNotBlank()) hardcodedUser else System.getenv("GITHUB_ACTOR") ?: gprUser)
                password = (if (hardcodedToken.isNotBlank()) hardcodedToken else System.getenv("GITHUB_TOKEN") ?: gprKey)
            }
        }
    }
}

rootProject.name = "silva-manager"
include(":app")

// Include silva-patcher and silva-library as composite builds if they exist locally
mapOf(
    "silva-patcher" to "app.silva:silva-patcher",
    "silva-library" to "app.silva:silva-library",
    "ARSCLib" to "com.github.REAndroid:arsclib"
).forEach { (libraryPath, libraryName) ->
    val libDir = file("../$libraryPath")
    if (libDir.exists()) {
        includeBuild(libDir) {
            dependencySubstitution {
                substitute(module(libraryName)).using(project(":"))
            }
        }
    }
}
