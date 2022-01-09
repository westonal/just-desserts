package com.android.example

import android.os.Build

@Suppress("unused")
class LintingExample {
    fun lint() {
        // The code below should show lint for the version code check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            throw RuntimeException()
        }
    }
}
