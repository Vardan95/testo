package com.vpetrosyan.app.testo.engine

/**
 * Created by Vardan on 1/14/2018.
 */
interface TestOperation {
    fun execute(memory: Memory, row: Int, column: Int)
}