package SimpleApp

import jetbrains.buildServer.configs.kotlin.v2024_1.*
import jetbrains.buildServer.configs.kotlin.v2024_1.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2024_1.projectFeatures.BuildReportTab
import jetbrains.buildServer.configs.kotlin.v2024_1.projectFeatures.buildReportTab
import jetbrains.buildServer.configs.kotlin.v2024_1.vcs.GitVcsRoot

version = "2024.1"

project {
    vcsRoot(SimpleAppVcs)

    buildType(BuildAndTest)
    buildType(Deploy)

    features {
        buildReportTab {
            title = "Build Log"
            startPage = "log.html"
        }
    }
}

object SimpleAppVcs : GitVcsRoot({
    name = "SimpleApp VCS"
    url = "https://github.com/your/repo.git"
    branch = "refs/heads/main"
    checkoutPolicy = GitVcsRoot.AgentCheckoutPolicy.USE_AGENT_MIRROR
})

object BuildAndTest : BuildType({
    name = "1. Build and Test"

    vcs {
        root(SimpleAppVcs)
    }

    steps {
        maven {
            goals = "clean package"
            pomLocation = "pom.xml"
        }
    }

    triggers {
        vcs {
            branchFilter = "+:refs/heads/main"
        }
    }
})

object Deploy : BuildType({
    name = "2. Deploy"

    steps {
        maven {
            goals = "deploy"
            pomLocation = "pom.xml"
        }
    }

    dependencies {
        snapshot(BuildAndTest) {}
    }
})
