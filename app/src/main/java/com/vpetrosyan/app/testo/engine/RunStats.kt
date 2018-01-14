package com.vpetrosyan.app.testo.engine

data class RunStats(val test_count: Int, val test_passed: Int, val test_failed: Int, val isAborted: Boolean) {
    fun isPassed():Boolean {
        return test_failed == 0
    }
}