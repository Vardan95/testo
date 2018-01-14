package com.vpetrosyan.app.testo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.vpetrosyan.app.testo.parser.Parser
import com.vpetrosyan.app.testo.parser.Scanner
import com.vpetrosyan.app.testo.parser.Token

class MainActivity : AppCompatActivity() {

    val test_1_program = "memory mem1[100][100]\n" +
                    "memory mem2[50][100]\n\n" +
                    "let mem1[20][20] st0\n" +
                    "let mem1[85][85] st0\n\n" +
                    "module test_module_1 {\n" +
                    "    test test_1 {  \n" +
                    "       =>(w1,r1)   \n" +
                    "    }\n\n" +
                    "    test test_2 {  \n" +
                    "       <=(w0,r0)   \n" +
                    "    }\n\n" +
                    "}\n\n" +
                    "run strict test_module_1 on mem1"

    fun test_1() {
        val pars = Parser(test_1_program)
        val program = pars.parse()

        assert(program == null)

        program?.run()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.text).setOnClickListener {
            test_1()

            //var scanner = Scanner("memory mem1[100][100]!")
          // var scanner = Scanner(" let mem1[50][50] st0!")
         //  var scanner = Scanner(" let mem1[50][50] st0\n\n    memory mem1[100][100]/sdasd!")
           var scanner = Scanner("memory mem1[100][100]/sdasd!")
            var lexeme = scanner.next()
            while (lexeme.kind != Token.EOF) {
                Log.e("Vardan", lexeme.toString())
                lexeme = scanner.next();
            }
            Log.e("Vardan", lexeme.toString())
        }
    }
}
