package io.github.westonal.lint;

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.ApiKt;
import com.android.tools.lint.detector.api.Issue;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public final class Registry extends IssueRegistry {

    @NotNull
    @Override
    public List<Issue> getIssues() {
        return Arrays.asList(
                VersionCodeDetector.VERSION_CODE_USAGE);
    }

    @Override
    public int getApi() {
        return ApiKt.CURRENT_API;
    }
}