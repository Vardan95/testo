package com.vpetrosyan.app.testo.engine

/**
 * Created by Vardan on 1/14/2018.
 */
class TestElement constructor(val direction: Direction) {
    private var operations =  arrayListOf<TestOperation>()

    fun addOperation(op: TestOperation) {
        operations.add(op)
    }

    fun run(memory: Memory) {
        when(direction) {
            Direction.UP, Direction.BOTH -> doUpTesting(memory) // TODO implement two direction testing
            Direction.DOWN -> doDownTesting(memory)
        }
    }

    private fun doUpTesting(memory: Memory)
    {
        for (i in 0 until memory.height)
            for (j in 0 until memory.width)  {
                for (operation in operations) {
                    operation.execute(memory, i, j)
                }
            }
    }

    private fun doDownTesting(memory: Memory)
    {
        for (i in memory.height - 1 downTo 0)
            for (j in memory.width - 1 downTo 0)  {
                for (operation in operations) {
                    operation.execute(memory, i, j)
                }
            }
    }
}