package com.example.docmate

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class IllnessListActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var btnBack: ImageButton
    private lateinit var btnAddIllness: Button
    private lateinit var tvScreenTitle: TextView
    private lateinit var rvIllnesses: RecyclerView

    private val illnessList = mutableListOf<IllnessModel>()
    private lateinit var adapter: IllnessAdapter

    private var patientId   = ""
    private var patientName = ""
    private var screenType  = "illness"   // illness | report | surgery
    private var screenTitle = "Past Illness Records"

    // Firestore collection depends on screenType
    private val collectionName get() = when (screenType) {
        "report"  -> "medical_reports"
        "surgery" -> "past_surgeries"
        else      -> "past_illnesses"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_illness_list)

        firestore   = FirebaseFirestore.getInstance()
        patientId   = intent.getStringExtra("patientId")   ?: ""
        patientName = intent.getStringExtra("patientName") ?: ""
        screenType  = intent.getStringExtra("screenType")  ?: "illness"
        screenTitle = intent.getStringExtra("screenTitle") ?: "Past Illness Records"

        btnBack       = findViewById(R.id.btnBack)
        btnAddIllness = findViewById(R.id.btnAddIllness)
        tvScreenTitle = findViewById(R.id.tvScreenTitle)
        rvIllnesses   = findViewById(R.id.rvIllnesses)

        tvScreenTitle.text = screenTitle
        btnAddIllness.text = when (screenType) {
            "report"  -> "+ Add Report"
            "surgery" -> "+ Add Surgery"
            else      -> "+ Add Illness"
        }

        adapter = IllnessAdapter(illnessList) { illness ->
            // View button clicked → open detail screen
            val intent = Intent(this, IllnessDetailActivity::class.java)
            intent.putExtra("illnessName",  illness.name)
            intent.putExtra("diagnosed",    illness.diagnosed)
            intent.putExtra("treatment",    illness.treatment)
            intent.putExtra("doctor",       illness.doctor)
            intent.putExtra("status",       illness.status)
            startActivity(intent)
        }
        rvIllnesses.layoutManager = LinearLayoutManager(this)
        rvIllnesses.adapter = adapter

        btnBack.setOnClickListener { finish() }
        btnAddIllness.setOnClickListener { showAddDialog() }

        loadIllnesses()
    }

    private fun loadIllnesses() {
        if (patientId.isEmpty()) return
        firestore.collection("users")
            .document(patientId)
            .collection(collectionName)
            .get()
            .addOnSuccessListener { snapshot ->
                illnessList.clear()
                for (doc in snapshot) {
                    illnessList.add(
                        IllnessModel(
                            id         = doc.id,
                            name       = doc.getString("name")      ?: "",
                            diagnosed  = doc.getString("diagnosed") ?: "",
                            treatment  = doc.getString("treatment") ?: "",
                            doctor     = doc.getString("doctor")    ?: "",
                            status     = doc.getString("status")    ?: "Ongoing"
                        )
                    )
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_add_illness, null)

        val etName      = dialogView.findViewById<EditText>(R.id.etIllnessName)
        val etDiagnosed = dialogView.findViewById<EditText>(R.id.etDiagnosed)
        val etTreatment = dialogView.findViewById<EditText>(R.id.etTreatment)
        val etDoctor    = dialogView.findViewById<EditText>(R.id.etDoctor)
        val etStatus    = dialogView.findViewById<EditText>(R.id.etStatus)

        AlertDialog.Builder(this)
            .setTitle(btnAddIllness.text)
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val data = hashMapOf(
                    "name"      to etName.text.toString().trim(),
                    "diagnosed" to etDiagnosed.text.toString().trim(),
                    "treatment" to etTreatment.text.toString().trim(),
                    "doctor"    to etDoctor.text.toString().trim(),
                    "status"    to etStatus.text.toString().trim().ifEmpty { "Ongoing" },
                    "patientId" to patientId
                )
                firestore.collection("users")
                    .document(patientId)
                    .collection(collectionName)
                    .add(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
                        loadIllnesses()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
