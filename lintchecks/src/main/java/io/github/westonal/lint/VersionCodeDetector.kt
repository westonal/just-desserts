@file:Suppress("UnstableApiUsage")

package io.github.westonal.lint

import com.android.tools.lint.client.api.JavaEvaluator
import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.android.tools.lint.detector.api.Category.Companion.CORRECTNESS
import com.android.tools.lint.detector.api.Detector.UastScanner
import com.android.tools.lint.detector.api.Scope.Companion.JAVA_FILE_SCOPE
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiType
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UExpression
import org.jetbrains.uast.tryResolve

class VersionCodeDetector : Detector(), UastScanner {

    companion object {
        val VERSION_CODE_USAGE = Issue.create(
            id = "VersionCodeUsage",
            briefDescription = "Using 'VERSION_CODES' reference instead of the numeric value",
            explanation = "Use of the numeric value is consistent with API documentation.",
            CORRECTNESS,
            priority = 5,
            Severity.WARNING,
            Implementation(VersionCodeDetector::class.java, JAVA_FILE_SCOPE)
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>> {
        return listOf(UExpression::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return ExpressionChecker(context)
    }

    private inner class ExpressionChecker(private val context: JavaContext) : UElementHandler() {
        private val evaluator: JavaEvaluator = context.evaluator
        private val versionCodeClass: PsiClass? =
            evaluator.findClass("android.os.Build.VERSION_CODES")

        override fun visitExpression(node: UExpression) {
            if (versionCodeClass == null) return
            if (node.getExpressionType() !== PsiType.INT) return
            val resolved = node.tryResolve() ?: return
            if (resolved.parent != versionCodeClass) return

            val constantValue = node.evaluate()
            context.report(
                VERSION_CODE_USAGE,
                node,
                context.getLocation(node),
                "Using 'VERSION_CODES' reference instead of the numeric value" +
                        if (constantValue != null) ", $constantValue" else "",
                if (constantValue != null) quickFixIssueInlineValue(
                    constantValue.toString(),
                    node.asSourceString()
                ) else null
            )
        }
    }

    private fun quickFixIssueInlineValue(fixSource: String, original: String) =
        fix()
            .name("Inline $original = $fixSource")
            .family("Inline version codes")
            .replace()
            .all()
            .reformat(true)
            .with(fixSource)
            .autoFix()
            .build()
}
