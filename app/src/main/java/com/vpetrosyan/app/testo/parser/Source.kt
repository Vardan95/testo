package com.vpetrosyan.app.testo.parser

class Source constructor(source: String) : Iterable<Char> {
    private val input = source
    private var pos = 0

    override fun iterator(): Iterator<Char>
    {
        return object : Iterator<Char> {
            override fun hasNext() : Boolean = (pos <= input.length - 1)

            override fun next() : Char
            {
                val ch = input[pos]
                pos++
                return ch
            }
        }
    }
}