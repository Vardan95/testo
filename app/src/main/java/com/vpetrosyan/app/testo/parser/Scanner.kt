package com.vpetrosyan.app.testo.parser

/**
 * Created by Vardan on 1/12/2018.
 */
class Scanner constructor(source: String) {
    private val input = Source(source).iterator()
    private var ch: Char = input.next()
    private var line: Int = 1

    private val keywords = hashMapOf(
            Pair("module", Token.KEYWORD_MODULE),
            Pair("test", Token.KEYWORD_TEST),
            Pair("memory", Token.KEYWORD_MEMORY),
            Pair("let", Token.KEYWORD_LET),
            Pair("run", Token.KEYWORD_RUN),
            Pair("on", Token.KEYWORD_ON),
            Pair("strict", Token.KEYWORD_STRICT)
    )

    private val faults = hashMapOf(
            Pair("st0", Token.FAILURE_ST_0),
            Pair("st1", Token.FAILURE_ST_1)
    )

    private val memory_operations = hashMapOf(
            Pair("w0", Token.OP_W_0),
            Pair("w1", Token.OP_W_1),
            Pair("r0", Token.OP_R_0),
            Pair("r1", Token.OP_R_1)
    )

    private val brackets = hashMapOf(
            Pair("(", Token.PAR_LEFT),
            Pair(")", Token.PAR_RIGHT),
            Pair("[", Token.BRCK_LEFT),
            Pair("]", Token.BRCK_RIGHT),
            Pair("{", Token.BR_LEFT),
            Pair("}", Token.BT_RIGHT)
    )

    fun next() : Lexeme {
        if( !input.hasNext() ) {
            return Lexeme(Token.EOF, "EOF", line)
        }

        // Skip whitespaces
        while(  ch == ' ' || ch == '\t' || ch == '\r' ) {
            ch = input.next()
            if(!input.hasNext()) {
                return Lexeme(Token.EOF, "EOF", line)
            }
        }

        // Skipping comments
        if( ch == '/' ) {
            while( ch != '\n' ) {
                ch = input.next()

                if(!input.hasNext()) {
                    return Lexeme(Token.EOF, "EOF", line)
                }
            }
            return next()
        }

        // Scan for keyword or memory operation or identifier
        if( Character.isLetter(ch) ) {
            return scanKeyword()
        }

        // Scan number
        if( Character.isDigit(ch)) {
            return scanNumber()
        }

        if( ch == '\n' ) {
            val lex = Lexeme(Token.NEWLINE, "<-/", line)
            ++line
            ch = input.next()
            return lex
        }

        if(ch == '=') {
            var s = ""
            s += ch
            if(input.hasNext()) {
                ch = input.next()
                s += ch
                if (ch == '>')
                    ch = input.next()
                    return Lexeme(Token.DIR_UP, s, line)
            }

            return Lexeme(Token.UNKNOWN, s, line)
        }

        if(ch == '<') {
            var s = ""
            s += ch
            if(input.hasNext()) {
                ch = input.next()
                s += ch
                if (ch == '=')
                    ch = input.next()
                    return Lexeme(Token.DIR_DOWN, s, line)
            }

            ch = input.next()
            return Lexeme(Token.UNKNOWN, s, line)
        }

        if(ch == ',') {
            ch = input.next()
            return Lexeme(Token.COMMA, ",", line)
        }

        val kind =  brackets.getOrDefault(ch.toString(), Token.UNKNOWN)
        val lex =  Lexeme(kind, ch.toString(), line)
        ch = input.next()

        return  lex
    }

    private fun scanKeyword(): Lexeme {
        var s = ""
        while( Character.isLetterOrDigit(ch) || ch == '_' ) {
            s += ch
            ch = input.next()

            if(!input.hasNext())
                break
        }

        if( Character.isLetterOrDigit(ch) || ch == '_' ) {
            s += ch
        }

        var kind = keywords.getOrDefault(s, Token.UNKNOWN)

        if(kind == Token.UNKNOWN)
            kind = faults.getOrDefault(s,  Token.UNKNOWN)

        if(kind == Token.UNKNOWN)
            kind = memory_operations.getOrDefault(s, Token.IDENTIFIER)

        return Lexeme(kind, s, line)
    }

    private fun scanNumber(): Lexeme
    {
        var s = ""
        while( Character.isDigit(ch)) {
            s += ch
            ch = input.next()

            if(!input.hasNext())
                break
        }

        if( Character.isDigit(ch) ) {
            s += ch
        }

        return Lexeme(Token.INTEGER, s, line)
    }
}