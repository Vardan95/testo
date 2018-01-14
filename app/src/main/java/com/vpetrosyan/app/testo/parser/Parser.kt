package com.vpetrosyan.app.testo.parser

import android.util.Log
import com.vpetrosyan.app.testo.engine.*
import com.vpetrosyan.app.testo.engine.def.MemoryDef
import com.vpetrosyan.app.testo.engine.def.FailureDef
import com.vpetrosyan.app.testo.engine.def.RunCommand

class Parser constructor(source: String) {
    private val scanner = Scanner(source + "!") // Add end symbol
    private var lookahead = scanner.next()

    private val program = Program()

    var onFail: ((failMessage:String) -> Unit)? = null

    fun parse(): Program?
    {
        try {
            parseProgram()
        }
        catch(se: ParseError) {
            Log.d("Testo",  "Error while parsing...", se)
            onFail?.invoke(se.message!!)
            return null
        }

        return program
    }

    private fun parseProgram() {
        while( lookahead.kind == Token.NEWLINE ) {
            match(Token.NEWLINE)
        }

        while( true ) {
            if( lookahead.kind == Token.KEYWORD_MEMORY ) {
                val memoryDef = parseMemoryDef()
                program.memory_defs.add(memoryDef)
            }

            if( lookahead.kind == Token.KEYWORD_LET) {
                val failureDef = parseFailureDef()
                program.failure_defs.add(failureDef)
            }

            if( lookahead.kind ==  Token.KEYWORD_RUN) {
                val runDef = parseRunCommand()
                program.run_commands.add(runDef)
            }

            if( lookahead.kind == Token.KEYWORD_MODULE) {
                val testModule = parseTestModule()
                program.test_modules.add(testModule)
            }

            if(lookahead.kind == Token.EOF) {
                break
            }

            if(!lookahead.isKindOf(Token.KEYWORD_MEMORY, Token.KEYWORD_LET, Token.KEYWORD_RUN,
                    Token.KEYWORD_MODULE, Token.EOF, Token.NEWLINE)) {
                if(lookahead.kind == Token.UNKNOWN) {
                    throw ParseError("Unknown ${lookahead.value} token at line ${lookahead.line}")
                } else {
                    throw ParseError("Wrong ${lookahead.value} token at line ${lookahead.line}")
                }
            }

            parseNewLines()
        }
    }

    private fun parseTestModule(): TestModule {
        match(Token.KEYWORD_MODULE)
        val moduleName = lookahead.value
        match(Token.IDENTIFIER)
        parseNewLines()
        match(Token.BR_LEFT)
        parseNewLines()

        var module = TestModule(moduleName)

        while (lookahead.kind == Token.KEYWORD_TEST) {
            var test = parseTest()
            module.addTest(test = test)
            parseNewLines()
        }

        parseNewLines()
        match(Token.BT_RIGHT)

        return module
    }

    private fun parseTest(): Test {
        match(Token.KEYWORD_TEST)
        var testName = lookahead.value
        match(Token.IDENTIFIER)
        val test = Test(testName)

        parseNewLines()
        match(Token.BR_LEFT)
        parseNewLines()

        while (lookahead.isKindOf(Token.DIR_UP, Token.DIR_DOWN)) {
            var testElement = parseTestElement()
            test.addElement(testElement)
            parseNewLines()
        }

        parseNewLines()
        match(Token.BT_RIGHT)
        return test
    }

    private fun parseTestElement(): TestElement {
        var direction: Direction

        if(lookahead.kind == Token.DIR_DOWN) {
            match(Token.DIR_DOWN)
            direction = Direction.DOWN
        } else {
            match(Token.DIR_UP)
            direction = Direction.UP
        }

        if(lookahead.isKindOf(Token.DIR_UP, Token.DIR_DOWN)) {
            if(direction == Direction.UP) {
                match(Token.DIR_DOWN)
            } else {
                match(Token.DIR_UP)
            }

            direction = Direction.BOTH
        }

        val element = TestElement(direction)
        match(Token.PAR_LEFT)

        while (lookahead.isKindOf(Token.OP_R_0, Token.OP_R_1, Token.OP_W_0, Token.OP_W_1)) {
            when(lookahead.kind) {
                Token.OP_R_0 -> {
                    element.addOperation(TestR0Operation())
                    match(Token.OP_R_0)
                }
                Token.OP_R_1 -> {
                    element.addOperation(TestR1Operation())
                    match(Token.OP_R_1)
                }
                Token.OP_W_0 -> {
                    element.addOperation(TestW0Operation())
                    match(Token.OP_W_0)
                }
                Token.OP_W_1 -> {
                    element.addOperation(TestW1Operation())
                    match(Token.OP_W_1)
                }
            }

            if(lookahead.kind == Token.PAR_RIGHT) {
                break
            }

            match(Token.COMMA)
        }

        match(Token.PAR_RIGHT)

        return element
    }

    private fun parseRunCommand(): RunCommand {
        match(Token.KEYWORD_RUN)

        var isStrict = false
        if(lookahead.kind == Token.KEYWORD_STRICT) {
            match(Token.KEYWORD_STRICT)
            isStrict = true
        }

        val test_module = lookahead.value
        match(Token.IDENTIFIER)
        match(Token.KEYWORD_ON)
        val test_memory = lookahead.value
        match(Token.IDENTIFIER)

        return RunCommand(test_module = test_module, test_memory = test_memory, isStrcit = isStrict)
    }

    private fun parseMemoryDef(): MemoryDef {
        match(Token.KEYWORD_MEMORY)

        return parseMemory()
    }

    private fun parseFailureDef(): FailureDef {
        match(Token.KEYWORD_LET)

        val memory = parseMemory()

        val failureType = lookahead.kind
        match(Token.FAILURE_ST_0, Token.FAILURE_ST_1)

        var fType = Failure.ST_0

        if(failureType == Token.FAILURE_ST_1) {
            fType = Failure.ST_1
        }

        return FailureDef(memoryId = memory.name, row = memory.height,
                column = memory.width, failure = fType)
    }

    private fun parseMemory() : MemoryDef {
        val memoryId = lookahead.value
        match(Token.IDENTIFIER)

        match(Token.BRCK_LEFT)
        val heightStr = lookahead.value
        match(Token.INTEGER)
        val height = Integer.valueOf(heightStr)
        match(Token.BRCK_RIGHT)

        match(Token.BRCK_LEFT)
        val widthStr = lookahead.value
        match(Token.INTEGER)
        val width = Integer.valueOf(widthStr)
        match(Token.BRCK_RIGHT)

        return MemoryDef(memoryId, height = height, width = width)
    }

    private fun parseNewLines()
    {
        if(lookahead.kind == Token.NEWLINE) {
            match(Token.NEWLINE)
            while (lookahead.kind == Token.NEWLINE) {
                match(Token.NEWLINE)
            }
        }
    }


    private fun match(vararg other: Token)
    {
        if( lookahead.isKindOf(*other) ) {
            lookahead = scanner.next()
        }
        else {
            var expectedTokens = ""

            for (token in other) {
                expectedTokens += "$token,"
            }

            throw ParseError("Error: ${lookahead.line} line, ${lookahead.value} text, expecting $expectedTokens found ${lookahead.kind}։")
        }
    }
}