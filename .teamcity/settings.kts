import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.triggers.vcs

version = "2024.12"

project {
    buildType(Build)
}

object Build : BuildType({
    name = "Java Maven CI/CD Pipeline"

    // Connect to VCS
    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        // Step 1: Compile Code
        maven {
            name = "Compile Code"
            goals = "clean compile"
            pomLocation = "pom.xml"
        }

        // Step 2: Run Unit Tests
        maven {
            name = "Run Tests"
            goals = "test"
            pomLocation = "pom.xml"
        }

        // Step 3: Package Application
        maven {
            name = "Package JAR"
            goals = "package"
            pomLocation = "pom.xml"
        }

        // Step 4: Docker Build & Push
        script {
            name = "Docker Build & Push"
            scriptContent = """
              docker build -t skanarul8002/javamavenproject:latest .
              docker push skanarul8002/javamavenproject:latest
            """.trimIndent()
        }

        // Step 5: Kubernetes Deployment
        script {
            name = "Kubernetes Deployment"
            scriptContent = """
              kubectl apply -f k8s/deployment.yaml
            """.trimIndent()
        }
    }

    triggers {
        vcs {  // Auto-build on each push
            branchFilter = "+:*"
        }
    }

    features {
        perfmon {}  // Monitor performance
    }
})
