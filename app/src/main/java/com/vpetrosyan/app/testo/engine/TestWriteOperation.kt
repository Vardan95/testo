package com.vpetrosyan.app.testo.engine

interface TestWriteOperation : TestOperation

class TestW0Operation : TestWriteOperation {
    override fun execute(memory: Memory, row: Int, column: Int) {
        memory.write(0, row = row, column = column)
    }
}

class TestW1Operation : TestWriteOperation {
    override fun execute(memory: Memory, row: Int, column: Int) {
        memory.write(1, row = row, column = column)
    }
}