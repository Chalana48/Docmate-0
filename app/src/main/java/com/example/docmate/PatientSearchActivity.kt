package com.example.docmate

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class PatientSearchActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var etSearch: EditText
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var btnBack: ImageButton

    private val allPatients      = mutableListOf<PatientModel>()
    private val filteredPatients = mutableListOf<PatientModel>()
    private lateinit var adapter: PatientAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_search)

        firestore      = FirebaseFirestore.getInstance()
        etSearch       = findViewById(R.id.etSearch)
        rvSearchResults = findViewById(R.id.rvSearchResults)
        btnBack        = findViewById(R.id.btnBack)

        adapter = PatientAdapter(filteredPatients) { patient ->
            val intent = Intent(this, PatientDetailsActivity::class.java)
            intent.putExtra("patientId",   patient.patientId)
            intent.putExtra("patientName", patient.name)
            intent.putExtra("patientAge",  patient.age)
            startActivity(intent)
        }
        rvSearchResults.layoutManager = LinearLayoutManager(this)
        rvSearchResults.adapter = adapter

        btnBack.setOnClickListener { finish() }

        // Live search filter
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        loadAllPatients()
    }

    private fun loadAllPatients() {
        firestore.collection("users")
            .get()
            .addOnSuccessListener { snapshot ->
                allPatients.clear()
                for (doc in snapshot) {
                    allPatients.add(
                        PatientModel(
                            patientId = doc.id,
                            name      = doc.getString("name") ?: "",
                            condition = doc.getString("condition") ?: "",
                            age       = doc.getString("age") ?: ""
                        )
                    )
                }
                // Show all initially
                filteredPatients.clear()
                filteredPatients.addAll(allPatients)
                adapter.notifyDataSetChanged()
            }
    }

    private fun filterList(query: String) {
        filteredPatients.clear()
        if (query.isEmpty()) {
            filteredPatients.addAll(allPatients)
        } else {
            val lower = query.lowercase()
            allPatients.forEach { p ->
                if (p.name.lowercase().contains(lower) ||
                    p.condition.lowercase().contains(lower)) {
                    filteredPatients.add(p)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }
}
