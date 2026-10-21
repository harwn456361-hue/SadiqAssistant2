
package com.example.sadiqassistant

import android.app.Activity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.content.Intent
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {

    private lateinit var conversation: TextView
    private lateinit var input: EditText
    private var speaker: TextToSpeech? = null
    private val requestSpeech = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        speaker = TextToSpeech(this, this)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val title = TextView(this).apply {
            text = "صديقي الصوتي"
            textSize = 28f
            gravity = Gravity.CENTER
        }
        layout.addView(title)

        conversation = TextView(this).apply {
            text = "مرحباً! أنا صديقي. يمكنك الكتابة أو التحدث إليّ."
            textSize = 18f
            setPadding(8, 24, 8, 24)
        }

        val scroll = ScrollView(this)
        scroll.addView(conversation)
        layout.addView(
            scroll,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
            )
        )

        input = EditText(this).apply {
            hint = "اكتب رسالتك هنا"
            textSize = 18f
        }
        layout.addView(input)

        val send = Button(this).apply {
            text = "إرسال"
            setOnClickListener {
                val message = input.text.toString().trim()
                if (message.isNotEmpty()) {
                    conversation.append("\n\nأنت: $message")
                    conversation.append("\nصديقي: استلمت رسالتك، وسنطوّر ذكائي لاحقاً.")
                    input.text.clear()
                }
            }
        }
        layout.addView(send)

        val listen = Button(this).apply {
            text = "تحدث معي"
            setOnClickListener { startListening() }
        }
        layout.addView(listen)

        val speak = Button(this).apply {
            text = "اقرأ الرد بصوت"
            setOnClickListener {
                speaker?.speak(
                    conversation.text.toString(),
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "sadiq_reply"
                )
            }
        }
        layout.addView(speak)

        setContentView(layout)
    }

    private fun startListening() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar")
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "تحدث إلى صديقي")
            startActivityForResult(intent, requestSpeech)
        } catch (e: Exception) {
            conversation.append("\n\nتعذر تشغيل التعرف على الصوت.")
        }
    }

    @Deprecated("Deprecated by Android, retained for compatibility")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestSpeech && resultCode == RESULT_OK) {
            val results = data?.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS
            )
            val message = results?.firstOrNull().orEmpty()

            if (message.isNotBlank()) {
                input.setText(message)
                input.setSelection(input.text.length)
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            speaker?.language = Locale("ar")
        }
    }

    override fun onDestroy() {
        speaker?.stop()
        speaker?.shutdown()
        super.onDestroy()
    }
}
