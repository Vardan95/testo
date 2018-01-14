package com.vpetrosyan.app.testo.engine

import android.util.Log
import com.vpetrosyan.app.testo.engine.error.TestError

class TestModule constructor(val name: String) {
    private var tests =  arrayListOf<Test>()

    fun  addTest(test: Test) {
        tests.add(test)
    }

    fun run(memory: Memory, isStrict: Boolean, onReport: ((message:String, isErrorLog: Boolean) -> Unit)?) : RunStats {
        var failCount = 0
        var passedCount = 0
        var isAborted = false

        val startLog = "====> Running module $name on ${memory.name} memory <===="
        Log.w("Testo", startLog)
        onReport?.invoke(startLog, false)

        for (test in tests) {
            val testFullName = "${memory.name}.${test.name}"
            try {
                test.run(memory)
                passedCount++
                val testPassedLog = "Test $testFullName passed"
                Log.d("Testo", testPassedLog)
                onReport?.invoke(testPassedLog, false)
            } catch (te: TestError) {
                val testFailedLog = "Test $testFullName failed " + te.message
                Log.d("Testo", testFailedLog)
                onReport?.invoke(testFailedLog, true)

                failCount++
                if(isStrict) {
                    isAborted = true
                    break
                }
            }
        }

        val moduleStatLog = "====> Module $name completed: Test Count = ${tests.size}," +
                " Test Passed = $passedCount, Test Failed = $failCount, Aborted = $isAborted)<===="
        Log.w("Testo", moduleStatLog)
        onReport?.invoke(moduleStatLog, false)

        return RunStats(test_count = tests.size, test_passed = passedCount, test_failed = failCount,
                isAborted = isAborted)
    }
}