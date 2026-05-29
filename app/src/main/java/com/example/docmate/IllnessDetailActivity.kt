package com.example.docmate

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class IllnessDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_illness_detail)

        val illnessName = intent.getStringExtra("illnessName") ?: ""
        val diagnosed   = intent.getStringExtra("diagnosed")   ?: ""
        val treatment   = intent.getStringExtra("treatment")   ?: ""
        val doctor      = intent.getStringExtra("doctor")      ?: ""

        findViewById<TextView>(R.id.tvIllnessTitle).text = illnessName
        findViewById<TextView>(R.id.tvDiagnosed).text    = "DIAGNOSED: ${diagnosed.uppercase()}"
        findViewById<TextView>(R.id.tvTreatment).text    = "TREATMENT: ${treatment.uppercase()}"
        findViewById<TextView>(R.id.tvDoctor).text       = "DOCTOR: ${doctor.uppercase()}"

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
    }
}
