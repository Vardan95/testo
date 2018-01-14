package com.vpetrosyan.app.testo

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import com.vpetrosyan.app.testo.parser.Parser
import android.text.Spannable
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.widget.ImageButton
import com.vpetrosyan.app.testo.engine.error.RunError
import android.R.attr.mimeType
import android.app.Activity
import android.util.Log
import android.os.ParcelFileDescriptor
import java.io.*


class MainActivity : AppCompatActivity() {

    private val READ_REQUEST_CODE = 42
    private val WRITE_REQUEST_CODE = 43

    var sourceText : EditText? = null
    var resultText : EditText? = null

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

        findViewById<ImageButton>(R.id.btn_execute).setOnClickListener {
            executeTest()
        }

        findViewById<ImageButton>(R.id.btn_save).setOnClickListener {
            saveProgram()
        }

        findViewById<ImageButton>(R.id.btn_clear).setOnClickListener {
            sourceText?.text?.clear()
            resultText?.text?.clear()
        }
    }

    private fun executeTest() {
        resultText?.text?.clear()

        val source_txt = sourceText?.text.toString()

        if(!TextUtils.isEmpty(source_txt)) {
            val parser = Parser(source = source_txt)
            parser.onFail = { str: String -> updateResultScreen(str, true) }
            val program = parser.parse()

            try {
                program?.onReport = { str: String, isFault: Boolean -> updateResultScreen(str = str, isFault = isFault) }
                program?.run()
            } catch (re: RunError) {
                updateResultScreen(str = re.message + "", isFault = true)
            }
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
            R.id.open -> {
                performFileSearch()
                return true
            }
            R.id.about -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://github.com/Vardan95/testo/blob/master/Discussion.md")
                startActivity(intent)
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

    fun performFileSearch() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)

        intent.addCategory(Intent.CATEGORY_OPENABLE)

        intent.type = "*/*"

        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    fun saveProgram() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)

        intent.addCategory(Intent.CATEGORY_OPENABLE)

        intent.type = "file/text"
        intent.putExtra(Intent.EXTRA_TITLE, "testo_program_${System.currentTimeMillis()}.testo")
        startActivityForResult(intent, WRITE_REQUEST_CODE)
    }

    private fun alterDocument(uri: Uri) {
        try {
            val pfd = contentResolver.openFileDescriptor(uri, "w")
            val fileOutputStream = FileOutputStream(pfd.fileDescriptor)
            fileOutputStream.write(sourceText?.text.toString().toByteArray())
            // Let the document provider know you're done by closing the stream.
            fileOutputStream.close()
            pfd.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode != Activity.RESULT_OK) {
            return
        }

        if(requestCode == WRITE_REQUEST_CODE) {
            alterDocument(data?.data!!)
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, data.data)
            shareIntent.type = "file/text"
            startActivity(shareIntent)
        }

        if(requestCode == READ_REQUEST_CODE) {
            val result = data?.data
            sourceText?.setText(readTextFromUri(result!!))
        }
    }

    private fun  readTextFromUri( uri : Uri) : String {
        val inputStream = contentResolver.openInputStream(uri)
        val reader = BufferedReader( InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line = reader.readLine()

        while (line != null) {
            stringBuilder.append(line)
            stringBuilder.append("\n")
            line = reader.readLine()
        }

        inputStream.close()
        reader.close()
        return stringBuilder.toString()
    }
}
