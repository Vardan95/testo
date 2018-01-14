package com.vpetrosyan.app.testo.engine

class ST1Failure : MemoryFailure {
    override fun onFail(memory: Memory, row: Int, column: Int) {
        memory.force_write(1, row, column)
    }

    override fun desc(): String {
        return "ST1 Fault (Memory cell value is always 1)"
    }
}