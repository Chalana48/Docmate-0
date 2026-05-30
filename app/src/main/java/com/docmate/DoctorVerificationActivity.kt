package com.example.docmate

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class DoctorVerificationActivity : AppCompatActivity() {

    // Initialize core structural connection engine
    private val db = FirebaseFirestore.getInstance()
    private val targetDoctorId = "sample_doctor_id" // Dynamic reference target from list intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_verification)

        val btnApprove = findViewById<Button>(R.id.btnApproveDoctor)

        btnApprove.setOnClickListener {
            executeDoctorVerificationWorkflow()
        }
    }

    private fun executeDoctorVerificationWorkflow() {
        // Target specific doctor profile object reference
        val doctorRef = db.collection("doctors").document(targetDoctorId)

        // Objective Update Operation
        doctorRef.update("status", "verified")
            .addOnSuccessListener {
                Toast.makeText(this, "Doctor status updated to verified", Toast.LENGTH_SHORT).show()
                generateAdministrativeAuditRecord("Approved Doctor ID: $targetDoctorId")
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Update error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun generateAdministrativeAuditRecord(actionDescription: String) {
        val logEntry = hashMapOf(
            "action" to actionDescription,
            "performedBy" to "SystemAdmin",
            "timestamp" to Date()
        )

        // Write directly to your logs collection space
        db.collection("logs")
            .add(logEntry)
            .addOnSuccessListener {
                finish() // Terminate review pipeline view safely
            }
    }
}