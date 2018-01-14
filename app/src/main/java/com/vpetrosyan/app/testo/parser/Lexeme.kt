package com.vpetrosyan.app.testo.parser

data class Lexeme constructor(val kind : Token, val value : String, val line : Int) {
    fun isKindOf(vararg other: Token): Boolean {
        return other.contains(kind)
    }
}