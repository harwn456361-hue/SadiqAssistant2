
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

class MainActivity : Activity() {

    private lateinit var conversation: TextView
    private lateinit var input: EditText
    private var speaker: TextToSpeech? = null
    private var speakerReady = false

    private val requestSpeech = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            createInterface()
            initializeSpeaker()
        } catch (e: Exception) {
            android.util.Log.e(
                "SadiqAssistant",
                "Startup error",
                e
            )
            throw e
        }
    }

    private fun createInterface() {
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
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
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
                    conversation.append(
                        "\nصديقي: استلمت رسالتك، وسنطوّر ذكائي لاحقاً."
                    )
                    input.text.clear()
                }
            }
        }
        layout.addView(send)

        val listen = Button(this).apply {
            text = "تحدث معي"
            setOnClickListener {
                startListening()
            }
        }
        layout.addView(listen)

        val speak = Button(this).apply {
            text = "اقرأ الرد بصوت"
            setOnClickListener {
                if (speakerReady) {
                    try {
                        speaker?.speak(
                            conversation.text.toString(),
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "sadiq_reply"
                        )
                    } catch (e: Exception) {
                        showMessage("تعذر تشغيل الصوت.")
                        android.util.Log.e(
                            "SadiqAssistant",
                            "Speech error",
                            e
                        )
                    }
                } else {
                    showMessage("الصوت غير جاهز بعد.")
                }
            }
        }
        layout.addView(speak)

        setContentView(layout)
    }

    private fun initializeSpeaker() {
        try {
            speaker = TextToSpeech(this) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    try {
                        val result = speaker?.setLanguage(
                            Locale("ar")
                        )
                        speakerReady =
                            result != null &&
                            result != TextToSpeech.LANG_MISSING_DATA &&
                            result != TextToSpeech.LANG_NOT_SUPPORTED

                        if (!speakerReady) {
                            showMessage(
                                "حزمة الصوت العربي غير متاحة."
                            )
                        }
                    } catch (e: Exception) {
                        speakerReady = false
                        android.util.Log.e(
                            "SadiqAssistant",
                            "Language initialization error",
                            e
                        )
                    }
                } else {
                    speakerReady = false
                    showMessage("تعذر تهيئة الصوت.")
                }
            }
        } catch (e: Exception) {
            speakerReady = false
            android.util.Log.e(
                "SadiqAssistant",
                "TextToSpeech initialization error",
                e
            )
            showMessage("تعذر تشغيل خدمة الصوت.")
        }
    }

    private fun startListening() {
        try {
            val intent = Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH
            ).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    "ar"
                )
                putExtra(
                    RecognizerIntent.EXTRA_PROMPT,
                    "تحدث إلى صديقي"
                )
            }

            startActivityForResult(intent, requestSpeech)
        } catch (e: Exception) {
            showMessage("تعذر تشغيل التعرف على الصوت.")
            android.util.Log.e(
                "SadiqAssistant",
                "Speech recognition error",
                e
            )
        }
    }

    @Deprecated("Deprecated by Android, retained for compatibility")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (
            requestCode == requestSpeech &&
            resultCode == RESULT_OK
        ) {
            val message = data
                ?.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )
                ?.firstOrNull()
                .orEmpty()

            if (message.isNotBlank()) {
                input.setText(message)
                input.setSelection(input.text.length)
            }
        }
    }

    private fun showMessage(message: String) {
        if (::conversation.isInitialized) {
            conversation.append("\n\n$message")
        }
    }

    override fun onDestroy() {
        try {
            speaker?.stop()
            speaker?.shutdown()
        } catch (e: Exception) {
            android.util.Log.e(
                "SadiqAssistant",
                "Audio shutdown error",
                e
            )
        }

        speaker = null
        speakerReady = false
        super.onDestroy()
    }
}
