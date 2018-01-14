package com.vpetrosyan.app.testo.engine

import com.vpetrosyan.app.testo.engine.error.TestError
import junit.framework.TestFailure

interface TestReadOperation : TestOperation

class TestR0Operation : TestReadOperation {
    override fun execute(memory: Memory, row: Int, column: Int) {
        if(memory.read(row = row, column = column) != 0) {
            val desc = memory.get_failure_desc(row, column)
            throw TestError(memory = memory.name, operation = "r0", pos_i = row, pos_j = column, failure = desc)
        }
    }
}

class TestR1Operation : TestReadOperation {
    override fun execute(memory: Memory, row: Int, column: Int) {
        if(memory.read(row = row, column = column) != 1) {
            val desc = memory.get_failure_desc(row, column)
            throw TestError(memory = memory.name, operation = "r1", pos_i = row, pos_j = column, failure = desc)
        }
    }
}