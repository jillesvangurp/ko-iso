#!/usr/bin/env kotlin

@file:DependsOn("io.github.typesafegithub:github-workflows-kt:4.0.0")

import io.github.typesafegithub.workflows.domain.Concurrency
import io.github.typesafegithub.workflows.domain.Mode
import io.github.typesafegithub.workflows.domain.Permission
import io.github.typesafegithub.workflows.domain.RunnerType
import io.github.typesafegithub.workflows.domain.actions.CustomAction
import io.github.typesafegithub.workflows.domain.triggers.PullRequest
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.dsl.workflow
import io.github.typesafegithub.workflows.yaml.CheckoutActionVersionSource
import io.github.typesafegithub.workflows.yaml.ConsistencyCheckJobConfig

workflow(
    name = "Process Pull Request",
    on = listOf(
        Push(
            branches = listOf("main"),
        ),
        PullRequest(
            branches = listOf("main"),
        ),
    ),
    sourceFile = __FILE__,
    targetFileName = "pr_master.yaml",
    concurrency = Concurrency(
        group = "${'$'}{{ github.workflow }}-${'$'}{{ github.ref }}",
        cancelInProgress = true,
    ),
    consistencyCheckJobConfig = ConsistencyCheckJobConfig.Configuration(
        condition = null,
        env = emptyMap(),
        checkoutActionVersion = CheckoutActionVersionSource.Given("v5"),
        additionalSteps = null,
        useLocalBindingsServerAsFallback = false,
    ),
    permissions = mapOf(Permission.Contents to Mode.Read),
) {
    job(
        id = "build-and-test",
        name = "Build And Test",
        runsOn = RunnerType.UbuntuLatest,
        timeoutMinutes = 30,
    ) {
        uses(
            name = "checkout",
            action = CustomAction("actions", "checkout", "v5", inputs = emptyMap()),
        )
        uses(
            name = "setup jdk",
            action = CustomAction(
                "actions",
                "setup-java",
                "v5",
                inputs = mapOf(
                    "java-version" to "25",
                    "distribution" to "corretto",
                    "java-package" to "jdk",
                ),
            ),
        )
        uses(
            name = "cache konan dir",
            action = CustomAction(
                "actions",
                "cache",
                "v4",
                inputs = mapOf(
                    "path" to "~/.konan/**/*",
                    "key" to "konan-${'$'}{{ runner.os }}-${'$'}{{ hashFiles('**/*.gradle.kts', 'gradle.properties', 'versions.properties') }}",
                ),
            ),
        )
        uses(
            name = "build with gradle",
            action = CustomAction(
                "gradle",
                "actions/setup-gradle",
                "v4",
                inputs = mapOf("arguments" to "check"),
            ),
        )
    }
}
