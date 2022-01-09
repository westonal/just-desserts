@file:Suppress("UnstableApiUsage")

package io.github.westonal.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

class Registry : IssueRegistry() {
    override val issues: List<Issue>
        get() = listOf(
            VersionCodeDetector.VERSION_CODE_USAGE
        )

    override val api: Int
        get() = CURRENT_API

    // Requires lint API 30.0+; if you're still building for something
    override val vendor: Vendor = Vendor(
        vendorName = "Alan Evans",
        feedbackUrl = "https://github.com/westonal/just-desserts",
        contact = "https://github.com/westonal/just-desserts"
    )
}