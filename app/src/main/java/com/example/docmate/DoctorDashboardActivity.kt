package com.example.docmate

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DoctorDashboardActivity : AppCompatActivity() {

    private lateinit var db: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var tvDoctorName: TextView
    private lateinit var tvTotalPatients: TextView
    private lateinit var tvPendingReports: TextView
    private lateinit var rvPastPatients: RecyclerView
    private lateinit var layoutSearch: LinearLayout

    private val patientList   = mutableListOf<PatientModel>()
    private lateinit var adapter: PatientAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_dashboard)

        auth      = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        tvDoctorName     = findViewById(R.id.tvDoctorName)
        tvTotalPatients  = findViewById(R.id.tvTotalPatients)
        tvPendingReports = findViewById(R.id.tvPendingReports)
        rvPastPatients   = findViewById(R.id.rvPastPatients)
        layoutSearch     = findViewById(R.id.layoutSearch)

        // Tap search bar → open search screen
        layoutSearch.setOnClickListener {
            startActivity(Intent(this, PatientSearchActivity::class.java))
        }

        // RecyclerView
        adapter = PatientAdapter(patientList) { patient ->
            val intent = Intent(this, PatientDetailsActivity::class.java)
            intent.putExtra("patientId",   patient.patientId)
            intent.putExtra("patientName", patient.name)
            intent.putExtra("patientAge",  patient.age)
            startActivity(intent)
        }
        rvPastPatients.layoutManager = LinearLayoutManager(this)
        rvPastPatients.adapter = adapter

        loadDoctorInfo()
        loadPatients()
    }

    private fun loadDoctorInfo() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("doctors").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val name = doc.getString("name") ?: doc.getString("email") ?: "Doctor"
                tvDoctorName.text = "Dr. $name"
            }
    }

    private fun loadPatients() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users")
            .whereEqualTo("assignedDoctor", uid)
            .get()
            .addOnSuccessListener { snapshot ->
                patientList.clear()
                for (doc in snapshot) {
                    patientList.add(
                        PatientModel(
                            patientId = doc.id,
                            name      = doc.getString("name") ?: "",
                            condition = doc.getString("condition") ?: "",
                            age       = doc.getString("age") ?: ""
                        )
                    )
                }
                tvTotalPatients.text = patientList.size.toString()
                adapter.notifyDataSetChanged()
                loadPendingCount(uid)
            }
            .addOnFailureListener {
                tvTotalPatients.text = "0"
            }
    }

    private fun loadPendingCount(uid: String) {
        firestore.collection("prescriptions")
            .whereEqualTo("doctorId", uid)
            .whereEqualTo("status", "pending")
            .get()
            .addOnSuccessListener { snap ->
                tvPendingReports.text = snap.size().toString()
            }
    }
}
