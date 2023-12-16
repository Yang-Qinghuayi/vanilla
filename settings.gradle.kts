pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.aliyun.com/repository/google")
        maven( "https://maven.aliyun.com/repository/jcenter")
        maven ("https://maven.aliyun.com/repository/public/")

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.aliyun.com/repository/google")
        maven( "https://maven.aliyun.com/repository/jcenter")
        maven ("https://maven.aliyun.com/repository/public/")

    }
}

rootProject.name = "M5"
include(":app")
 