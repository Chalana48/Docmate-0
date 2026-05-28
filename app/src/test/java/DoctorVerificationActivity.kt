package com.example.docmate

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class DoctorVerificationActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val targetDoctorId = "sample_doctor_uid" // Replace with intent data dynamically later

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_verification)

        val btnApproveDoctor = findViewById<Button>(R.id.btnApproveDoctor)

        btnApproveDoctor.setOnClickListener {
            // Update doctor verification status in Firestore
            db.collection("doctors").document(targetDoctorId)
                .update("status", "verified")
                .addOnSuccessListener {
                    Toast.makeText(this, "Doctor status updated to verified!", Toast.LENGTH_SHORT).show()
                    writeAuditTrailLog("Approved Doctor profile verification: $targetDoctorId")
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun writeAuditTrailLog(actionDescription: String) {
        val logData = hashMapOf(
            "action" to actionDescription,
            "performedBy" to "SystemAdmin",
            "timestamp" to Date()
        )

        db.collection("logs")
            .add(logData)
            .addOnSuccessListener {
                finish()
            }
    }
}