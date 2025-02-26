#!/usr/bin/env kotlin

@file:DependsOn("io.github.typesafegithub:github-workflows-kt:1.13.0")

import io.github.typesafegithub.workflows.actions.actions.CacheV4
import io.github.typesafegithub.workflows.actions.actions.CheckoutV4
import io.github.typesafegithub.workflows.actions.actions.SetupJavaV4
import io.github.typesafegithub.workflows.actions.gradle.GradleBuildActionV3
import io.github.typesafegithub.workflows.domain.RunnerType
import io.github.typesafegithub.workflows.domain.triggers.PullRequest
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.dsl.workflow
import io.github.typesafegithub.workflows.yaml.writeToFile

val workflow = workflow(
    name = "Process Pull Request",
    on = listOf(
        Push(
            branches = listOf("main"),
        ),
        PullRequest(
            branches = listOf("main"),
        ),
    ),
    sourceFile = __FILE__.toPath(),
    targetFileName = "pr_master.yaml",
) {
    job(
        id = "build-and-test",
        name = "Build And Test",
        runsOn = RunnerType.UbuntuLatest,
        timeoutMinutes = 30,
    ) {
        uses(
            name = "checkout",
            action = CheckoutV4(),
        )
        uses(
            name = "setup jdk",
            action = SetupJavaV4(
                javaPackage = SetupJavaV4.JavaPackage.Jdk,
                javaVersion = "21",
                distribution = SetupJavaV4.Distribution.Corretto,
                cache = SetupJavaV4.BuildPlatform.Gradle,
            ),
        )
        uses(
            name = "cache konan dir",
            action = CacheV4(
                path = listOf("~/.konan/**/*"),
                key = ".konan",
            ),
        )
        uses(
            name = "build with gradle",
            action = GradleBuildActionV3(
                arguments = "check",
            ),
        )
    }
}

workflow.writeToFile(addConsistencyCheck = true)
