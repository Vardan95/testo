package com.vpetrosyan.app.testo.engine

interface MemoryFailure {
    fun onFail(memory: Memory, row: Int, column: Int)
    fun desc():String
}