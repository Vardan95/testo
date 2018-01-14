package com.vpetrosyan.app.testo.engine

import android.util.Log
import com.vpetrosyan.app.testo.engine.error.TestError

class TestModule constructor(val name: String) {
    private var tests =  arrayListOf<Test>()

    fun  addTest(test: Test) {
        tests.add(test)
    }

    fun run(memory: Memory, isStrict: Boolean) : RunStats {
        var failCount = 0
        var passedCount = 0
        var isAborted = false

        Log.w("Testo", "====> Running module $name on ${memory.name} memory <====")

        for (test in tests) {
            val testFullName = "${memory.name}.${test.name}"
            try {
                test.run(memory)
                passedCount++
                Log.d("Testo", "Test $testFullName passed")
            } catch (te: TestError) {
                Log.e("Testo", "Test $testFullName failed " + te.message)
                failCount++
                if(isStrict) {
                    isAborted = true
                    break
                }
            }
        }

        Log.w("Testo", "====> Module $name completed: Test Count = ${tests.size}," +
                " Test Passed = $passedCount, Test Failed = $failCount, Aborted = $isAborted)<====")

        return RunStats(test_count = tests.size, test_passed = passedCount, test_failed = failCount,
                isAborted = isAborted)
    }
}