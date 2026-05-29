package com.example.docmate

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PatientDetailsActivity : AppCompatActivity() {

    private lateinit var tvPatientName: TextView
    private lateinit var tvPatientAge: TextView
    private lateinit var btnBack: ImageButton
    private lateinit var layoutPastIllnesses: LinearLayout
    private lateinit var layoutMedicalReports: LinearLayout
    private lateinit var layoutPastSurgeries: LinearLayout

    private var patientId   = ""
    private var patientName = ""
    private var patientAge  = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_details)

        // Get data passed from previous screen
        patientId   = intent.getStringExtra("patientId")   ?: ""
        patientName = intent.getStringExtra("patientName") ?: "Patient"
        patientAge  = intent.getStringExtra("patientAge")  ?: ""

        tvPatientName = findViewById(R.id.tvPatientName)
        tvPatientAge  = findViewById(R.id.tvPatientAge)
        btnBack       = findViewById(R.id.btnBack)

        layoutPastIllnesses  = findViewById(R.id.layoutPastIllnesses)
        layoutMedicalReports = findViewById(R.id.layoutMedicalReports)
        layoutPastSurgeries  = findViewById(R.id.layoutPastSurgeries)

        tvPatientName.text = patientName
        tvPatientAge.text  = if (patientAge.isNotEmpty()) "Age: $patientAge" else ""

        btnBack.setOnClickListener { finish() }

        // Past Illnesses → IllnessListActivity
        layoutPastIllnesses.setOnClickListener {
            val intent = Intent(this, IllnessListActivity::class.java)
            intent.putExtra("patientId",    patientId)
            intent.putExtra("patientName",  patientName)
            intent.putExtra("screenType",   "illness")
            intent.putExtra("screenTitle",  "Past Illness Records")
            startActivity(intent)
        }

        // Medical Reports → IllnessListActivity (same screen, different type)
        layoutMedicalReports.setOnClickListener {
            val intent = Intent(this, IllnessListActivity::class.java)
            intent.putExtra("patientId",   patientId)
            intent.putExtra("patientName", patientName)
            intent.putExtra("screenType",  "report")
            intent.putExtra("screenTitle", "Medical Reports")
            startActivity(intent)
        }

        // Past Surgeries → IllnessListActivity (same screen, different type)
        layoutPastSurgeries.setOnClickListener {
            val intent = Intent(this, IllnessListActivity::class.java)
            intent.putExtra("patientId",   patientId)
            intent.putExtra("patientName", patientName)
            intent.putExtra("screenType",  "surgery")
            intent.putExtra("screenTitle", "Past Surgeries")
            startActivity(intent)
        }
    }
}
