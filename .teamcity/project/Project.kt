package javamavenproject

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.*
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.*
import jetbrains.buildServer.configs.kotlin.v2019_2.projectFeatures.SonarQubeProjectFeature
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

version = "2023.11"

project {
    description = "CI/CD Pipeline for JavaMavenProject"
    
    buildType(JavaMavenBuildPipeline)
}

object JavaMavenBuildPipeline : BuildType({
    name = "Java Maven CI/CD Pipeline"

    // Pull code from GitHub
    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        // Step 1: Git Checkout (automatically handled by TeamCity)
        
        // Step 2: Compile Code
        maven {
            name = "Compile Code"
            goals = "clean compile"
            pomLocation = "pom.xml"
        }

        // Step 3: Run Unit Tests
        maven {
            name = "Run Tests"
            goals = "test"
            pomLocation = "pom.xml"
        }

        // Step 4: Package Application
        maven {
            name = "Package JAR"
            goals = "package"
            pomLocation = "pom.xml"
        }

        // Step 5: Run SonarQube Analysis (Optional)
        step {
            name = "SonarQube Scan"
            type = "SonarQubeRunner"
            param("sonarProjectKey", "JavaMavenProject")
            param("sonarHostUrl", "https://sonarcloud.io")
            param("sonar.organization", "skanarul8002")
        }
        // Step 6: Docker Build & Push
        step {
            name = "Docker Build & Push"
            type = "simpleRunner"
            param("use.custom.script", "true")
            scriptContent = """
              docker build -t skanarul8002/javamavenproject:latest .
              docker push skanarul8002/javamavenproject:latest
            """.trimIndent()
        }

        // Step 7: Deploy to Kubernetes
        step {
            name = "Kubernetes Deployment"
            type = "simpleRunner"
            scriptContent = """
              kubectl apply -f k8s/deployment.yaml
            """.trimIndent()
        }
    }

    // Auto-build on each push
    triggers {
        vcs {
            branchFilter = "+:*"  // Monitor all branches
        }
    }
})
