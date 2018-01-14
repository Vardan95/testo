package com.vpetrosyan.app.testo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import com.vpetrosyan.app.testo.parser.Parser
import com.vpetrosyan.app.testo.parser.Scanner
import com.vpetrosyan.app.testo.parser.Token
import android.widget.TextView
import android.text.Html
import android.text.Html.FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE
import android.text.Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL
import android.text.Spannable
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import com.vpetrosyan.app.testo.engine.error.RunError
import android.text.SpannableString
import android.view.View.NOT_FOCUSABLE


class MainActivity : AppCompatActivity() {

    var sourceText : EditText? = null
    var resultText : TextView? = null

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

    val test_2_program = "memory mem1[100][100]\n" +
            "memory mem2[50][100]\n\n" +
            "let mem1[20][20] st0\n" +
            "let mem1[85][85] st0\n\n" +
            "module test_module_1 {\n" +
            "    test test_1 {  \n" +
            "       =>(w0,r0)   \n" +
            "    }\n\n" +
            "    test test_2 {  \n" +
            "       <=(w1,r1)   \n" +
            "    }\n\n" +
            "}\n\n" +
            "run test_module_1 on mem1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sourceText = findViewById(R.id.text_source)
        resultText = findViewById(R.id.text_result)
        resultText?.isClickable  = false
        resultText?.isEnabled = false
        resultText?.isFocusableInTouchMode = false
        resultText?.isFocusable = false

        findViewById<Button>(R.id.btn_execute).setOnClickListener {
            executeTest()
        }
    }

    private fun executeTest() {
        resultText?.text = ""
        val parser = Parser(source = sourceText?.text.toString())
        parser.onFail = { str: String -> updateResultScreen(str, true)}
        val program = parser.parse()

        try {
            program?.onReport = { str: String, isFault:Boolean -> updateResultScreen(str = str, isFault = isFault)}
            program?.run()
        } catch (re: RunError) {
            updateResultScreen(str = re.message + "", isFault = true)
        }

    }

    private fun updateResultScreen(str: String, isFault: Boolean) {
        appendLog( str + "\n", isFault)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.test_1 -> {
                sourceText?.setText(test_1_program)
                return true
            }
            R.id.test_2 -> {
                sourceText?.setText(test_2_program)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun appendLog(text: String, isFault: Boolean) {
        val start = resultText?.text?.length
        resultText?.append(text)
        val end = resultText?.text?.length

        val spannableText = resultText?.text as Spannable

        var labelColor = resources.getColor(android.R.color.holo_green_light)

        if(isFault) {
            labelColor = resources.getColor(android.R.color.holo_red_light)
        }

        spannableText.setSpan( ForegroundColorSpan(labelColor), start!!, end!!, 0)
    }
}
