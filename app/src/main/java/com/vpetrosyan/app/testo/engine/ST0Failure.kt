package com.vpetrosyan.app.testo.engine

class ST0Failure : MemoryFailure {
    override fun onFail(memory: Memory, row: Int, column: Int) {
        memory.force_write(0, row, column)
    }

    override fun desc(): String {
        return "ST0 Fault (Memory cell value is always 0)"
    }
}