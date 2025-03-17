package Project

import jetbrains.buildServer.configs.kotlin.v2023_11.*
import jetbrains.buildServer.configs.kotlin.v2023_11.Project

object Project : Project({
    id("MyProject")
    name = "My Project"

    buildType(Build)
})

object Build : BuildType({
    name = "Simple Build"

    steps {
        maven {
            goals = "clean install"
            pomLocation = "pom.xml"
        }
    }
})
