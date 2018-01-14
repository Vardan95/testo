package com.vpetrosyan.app.testo.engine

import android.util.Log
import com.vpetrosyan.app.testo.engine.def.FailureDef
import com.vpetrosyan.app.testo.engine.def.MemoryDef
import com.vpetrosyan.app.testo.engine.def.RunCommand
import com.vpetrosyan.app.testo.engine.error.RunError

class Program {
    var test_modules = arrayListOf<TestModule>()
    var memory_defs = arrayListOf<MemoryDef>()
    var failure_defs = arrayListOf<FailureDef>()
    var run_commands = arrayListOf<RunCommand>()

    fun run() {
        val env = Environment()

        for (module in test_modules) {
            env.test_modules.put(module.name, module)
        }

        for (memoryDef in memory_defs) {
            env.memory_chunks.put(memoryDef.name, Memory(name = memoryDef.name, height = memoryDef.height,
                    width = memoryDef.width))
        }

        for (failure in failure_defs) {
            if(env.memory_chunks.containsKey(failure.memoryId)) {
                val failureType : MemoryFailure = if(failure.failure == Failure.ST_0) {
                    ST0Failure()
                } else {
                    ST1Failure()
                }
                env.memory_chunks.getValue(failure.memoryId).add_failure(row = failure.row, column = failure.column,
                        failure = failureType)
            } else {
                throw RunError("Memory ${failure.memoryId} wasn't declared!")
            }
        }

        var totalTestCount = 0
        var totalTestPassed = 0
        var totalTestFailed = 0

        Log.d("Testo", "========> Testing Started: <========")

        for (runCommand in run_commands) {
            val state =  runCommand.run(env)
            totalTestCount += state.test_count
            totalTestPassed += state.test_passed
            totalTestFailed += state.test_failed
        }

        Log.d("Testo", "========> Testing finished: Total Test Count = $totalTestCount," +
                " Total Tests Passed = $totalTestPassed, Total Tests Failed = $totalTestFailed <========")
    }
}