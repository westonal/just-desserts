package io.github.westonal.lint;

import com.android.tools.lint.client.api.JavaEvaluator;
import com.android.tools.lint.client.api.UElementHandler;
import com.android.tools.lint.detector.api.*;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UExpression;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public final class VersionCodeDetector extends Detector implements Detector.UastScanner {

    static final Issue VERSION_CODE_USAGE = Issue.create("VersionCodeUsage",
            "Using 'VERSION_CODES' reference instead of the numeric value",
            "Use of the numeric value is consistent with API documentation.",
            Category.CORRECTNESS,
            5,
            Severity.WARNING,
            new Implementation(VersionCodeDetector.class, Scope.JAVA_FILE_SCOPE));

    @Override
    public List<Class<? extends UElement>> getApplicableUastTypes() {
        return Collections.singletonList(UExpression.class);
    }

    @Override
    public UElementHandler createUastHandler(@NotNull JavaContext context) {
        return new ExpressionChecker(context);
    }

    private class ExpressionChecker extends UElementHandler {
        private final JavaContext context;
        private final JavaEvaluator evaluator;
        private final PsiClass versionCodeClass;

        public ExpressionChecker(JavaContext context) {
            this.context = context;
            this.evaluator = context.getEvaluator();
            this.versionCodeClass = evaluator.findClass("android.os.Build.VERSION_CODES");
        }

        @Override
        public void visitExpression(@NotNull UExpression node) {
            if (versionCodeClass == null) return;
            if (node.getExpressionType() != PsiType.INT) return;

            PsiElement javaPsi = node.getJavaPsi();

            if (javaPsi != null) {
                PsiElement resolved = evaluator.resolve(javaPsi);

                if (resolved != null && resolved.getParent().equals(versionCodeClass)) {
                    Object evaluated = node.evaluate();

                    if (evaluated != null) {
                        context.report(VERSION_CODE_USAGE, node, context.getLocation(node), "Using 'VERSION_CODES' reference instead of the numeric value " + evaluated, quickFixIssueInlineValue(evaluated.toString()));
                    } else {
                        context.report(VERSION_CODE_USAGE, node, context.getLocation(node), "Using 'VERSION_CODES' reference instead of the numeric value", null);
                    }
                }
            }
        }
    }

    private LintFix quickFixIssueInlineValue(@NotNull String fixSource) {
        return fix()
                .replace()
                .all()
                .reformat(true)
                .with(fixSource)
                .autoFix()
                .build();
    }
}