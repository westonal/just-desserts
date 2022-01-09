@file:Suppress("UnstableApiUsage")

package io.github.westonal.lint

import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestFiles.java
import com.android.tools.lint.checks.infrastructure.TestLintTask
import org.intellij.lang.annotations.Language
import org.junit.Test

class VersionCodeDetectorTest {

    @Test
    fun version_code_constant_referenced_in_code() {
        TestLintTask.lint()
            .files(
                javaIndentTrim(
                    """
                          package foo;
                          
                          import android.os.Build;
                          
                          public class Example {
                              public void versionCodeMention() {
                                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                  }
                              }
                          }
                          """
                )
            )
            .issues(VersionCodeDetector.VERSION_CODE_USAGE)
            .run()
            .expect(
                """
                src/foo/Example.java:7: Warning: Using 'VERSION_CODES' reference instead of the numeric value, 21 [VersionCodeUsage]
                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~
               0 errors, 1 warnings
               """.trimIndent()
            )
            .expectFixDiffs(
                """
                Fix for src/foo/Example.java line 7: Inline Build.VERSION_CODES.LOLLIPOP = 21:
                @@ -7 +7
                -         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                +         if (Build.VERSION.SDK_INT >= 21) {
                """.trimIndent()
            )
    }

    @Test
    fun numeric_value_referenced_in_code() {
        TestLintTask.lint()
            .files(
                javaIndentTrim(
                    """
                          package foo;
                          
                          import android.os.Build;
                          
                          public class Example {
                              public void versionCodeMention() {
                                  if (Build.VERSION.SDK_INT >= 22) {
                                  }
                              }
                          }
                          """
                )
            )
            .issues(VersionCodeDetector.VERSION_CODE_USAGE)
            .run()
            .expectClean()
    }

    @Test
    fun non_version_code_constant_referenced_in_code() {
        TestLintTask.lint()
            .files(
                javaIndentTrim(
                    """
                          package foo;
                          
                          import android.os.Build;
                          
                          public class Example {
                              private final static int LOLLIPOP = 21;
                              public void versionCodeMention() {
                                 if (Build.VERSION.SDK_INT >= LOLLIPOP) {
                                 }
                              }
                          }
                          """
                )
            )
            .issues(VersionCodeDetector.VERSION_CODE_USAGE)
            .run()
            .expectClean()
    }

    @Test
    fun version_code_constant_referenced_in_TargetApi_attribute_and_inner_class_import() {
        TestLintTask.lint()
            .files(
                javaIndentTrim(
                    """
                     package foo;
                      
                     import android.os.Build.VERSION_CODES;                          
                     import android.annotation.TargetApi;
                      
                     public class Example {
                         @TargetApi(VERSION_CODES.N)
                         public void versionCodeMention() {
                         }
                     }
                     """
                )
            )
            .issues(VersionCodeDetector.VERSION_CODE_USAGE)
            .run()
            .expect(
                """
                    src/foo/Example.java:7: Warning: Using 'VERSION_CODES' reference instead of the numeric value, 24 [VersionCodeUsage]
                        @TargetApi(VERSION_CODES.N)
                                   ~~~~~~~~~~~~~~~
                    0 errors, 1 warnings"""
            )
            .expectFixDiffs(
                """
                Fix for src/foo/Example.java line 7: Inline VERSION_CODES.N = 24:
                @@ -7 +7
                -     @TargetApi(VERSION_CODES.N)
                +     @TargetApi(24)
                """.trimIndent()
            )
    }

    @Test
    fun version_code_constant_referenced_in_RequiresApi_attribute_with_named_parameter() {
        TestLintTask.lint()
            .files(
                androidX_RequiresApi,
                javaIndentTrim(
                    """
                          package foo;
                          
                          import android.os.Build;
                          import androidx.annotation.RequiresApi;
                          
                          public class Example {
                              @RequiresApi(app = Build.VERSION_CODES.M)
                              public void versionCodeMention() {
                              }
                          }
                          """
                )
            )
            .issues(VersionCodeDetector.VERSION_CODE_USAGE)
            .run()
            .expect(
                """
                src/foo/Example.java:7: Warning: Using 'VERSION_CODES' reference instead of the numeric value, 23 [VersionCodeUsage]
                    @RequiresApi(app = Build.VERSION_CODES.M)
                                       ~~~~~~~~~~~~~~~~~~~~~
                0 errors, 1 warnings
                """.trimIndent()
            )
            .expectFixDiffs(
                """
                Fix for src/foo/Example.java line 7: Inline Build.VERSION_CODES.M = 23:
                @@ -7 +7
                -     @RequiresApi(app = Build.VERSION_CODES.M)
                +     @RequiresApi(app = 23)
                """.trimIndent()
            )
    }

    companion object {
        var androidX_RequiresApi: TestFile = javaIndentTrim(
            """
                  package androidx.annotation;
                  
                  import static java.lang.annotation.ElementType.CONSTRUCTOR;
                  import static java.lang.annotation.ElementType.FIELD;
                  import static java.lang.annotation.ElementType.METHOD;
                  import static java.lang.annotation.ElementType.PACKAGE;
                  import static java.lang.annotation.ElementType.TYPE;
                  import static java.lang.annotation.RetentionPolicy.CLASS;
                  
                  import java.lang.annotation.Target;
                  
                  @Target({TYPE, METHOD, CONSTRUCTOR, FIELD, PACKAGE})
                  public @interface RequiresApi {                  
                  }
                  """
        )
    }
}

fun javaIndentTrim(@Language("JAVA") source: String):TestFile = java(source.trimIndent())