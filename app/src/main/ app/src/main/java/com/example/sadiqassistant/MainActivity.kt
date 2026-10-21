
package com.example.sadiqassistant

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.LinearLayout
import android.view.Gravity
import android.graphics.Color
import android.view.ViewGroup

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(24, 24, 24, 24)
            }

            val message = TextView(this).apply {
                text = "صديقي الصوتي\n\nالتطبيق يعمل الآن.\n\nهذه نسخة اختبار لتحديد سبب المشكلة."
                textSize = 22f
                gravity = Gravity.CENTER
                setTextColor(Color.BLACK)
            }

            layout.addView(
                message,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )

            setContentView(layout)

        } catch (e: Exception) {
            Log.e("SadiqCrash", "خطأ أثناء تشغيل التطبيق", e)
            throw e
        }
    }
}
