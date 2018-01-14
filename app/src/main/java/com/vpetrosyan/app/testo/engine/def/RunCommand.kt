package com.vpetrosyan.app.testo.engine.def

import com.vpetrosyan.app.testo.engine.Environment
import com.vpetrosyan.app.testo.engine.error.RunError
import com.vpetrosyan.app.testo.engine.RunStats

class RunCommand(val test_module : String, var test_memory: String, var isStrcit: Boolean ) {
    fun run(env: Environment, onReport: ((message:String, isErrorLog: Boolean) -> Unit)?) : RunStats {
        if(!env.test_modules.containsKey(test_module)) {
            throw RunError("Module $test_module wasn't declared")
        }

        val testModule = env.test_modules[test_module]

        if(!env.memory_chunks.containsKey(test_memory)) {
            throw RunError("Memory $test_memory wasn't declared")
        }

        val testMemory = env.memory_chunks[test_memory]

        return testModule!!.run(testMemory!!, isStrcit, onReport)
    }
}