package com.vpetrosyan.app.testo.engine

class Memory constructor(val name: String, val height: Int, val width: Int) {
    private var cells = Array(height) {IntArray(width)}
    private val failureTable = mutableMapOf<Pair<Int, Int>, MemoryFailure>()

    init {
        for (i in 0 until height)
            for (j in 0 until width)
                cells[i][j] = -1
    }

    fun add_failure(row: Int, column: Int, failure: MemoryFailure) {
        failureTable.put(Pair(row, column), failure)
    }

    fun get_failure_desc(row: Int, column: Int) : String {
        return  failureTable[Pair(row, column)]!!.desc()
    }

    fun force_write(m_val: Int, row: Int, column: Int) {
        cells[row][column] = m_val
    }

    fun write(m_val: Int, row: Int, column: Int) {
        cells[row][column] = m_val

        if(failureTable.containsKey(Pair(row, column)))
            failureTable[Pair(row, column)]!!.onFail(memory = this, row = row, column =  column)
    }

    fun read(row: Int, column: Int) : Int {
        return cells[row][column]
    }
}