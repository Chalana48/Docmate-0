package com.example.docmate

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class DoctorRegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private lateinit var etNic: EditText
    private lateinit var etLicenseNumber: EditText
    private lateinit var spinnerSpecialization: Spinner
    private lateinit var etHospital: EditText
    private lateinit var etWorkEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var layoutUpload: LinearLayout
    private lateinit var tvUploadLabel: TextView
    private lateinit var btnSubmit: Button
    private lateinit var btnAlreadyVerified: Button
    private lateinit var btnBack: ImageButton

    private var selectedFileUri: Uri? = null
    private val PICK_FILE_REQUEST = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_register)

        auth    = FirebaseAuth.getInstance()
        db      = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        etNic              = findViewById(R.id.etNic)
        etLicenseNumber    = findViewById(R.id.etLicenseNumber)
        spinnerSpecialization = findViewById(R.id.spinnerSpecialization)
        etHospital         = findViewById(R.id.etHospital)
        etWorkEmail        = findViewById(R.id.etWorkEmail)
        etPhone            = findViewById(R.id.etPhone)
        layoutUpload       = findViewById(R.id.layoutUpload)
        tvUploadLabel      = findViewById(R.id.tvUploadLabel)
        btnSubmit          = findViewById(R.id.btnSubmit)
        btnAlreadyVerified = findViewById(R.id.btnAlreadyVerified)
        btnBack            = findViewById(R.id.btnBack)

        // Specialization spinner options
        val specializations = listOf(
            "Select Specialization",
            "General Practitioner",
            "Cardiologist",
            "Dermatologist",
            "Neurologist",
            "Pediatrician",
            "Surgeon",
            "Orthopedic",
            "Other"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, specializations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSpecialization.adapter = adapter

        // Upload document
        layoutUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf", "image/jpeg", "image/png"))
            startActivityForResult(intent, PICK_FILE_REQUEST)
        }

        btnSubmit.setOnClickListener { submitRegistration() }

        btnAlreadyVerified.setOnClickListener {
            startActivity(Intent(this, DoctorLoginActivity::class.java))
            finish()
        }

        btnBack.setOnClickListener { finish() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedFileUri = data?.data
            tvUploadLabel.text = selectedFileUri?.lastPathSegment ?: "File selected"
        }
    }

    private fun submitRegistration() {
        val nic             = etNic.text.toString().trim()
        val license         = etLicenseNumber.text.toString().trim()
        val specialization  = spinnerSpecialization.selectedItem.toString()
        val hospital        = etHospital.text.toString().trim()
        val email           = etWorkEmail.text.toString().trim()
        val phone           = etPhone.text.toString().trim()

        if (nic.isEmpty() || license.isEmpty() || hospital.isEmpty() ||
            email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Create Firebase Auth account (password = NIC for simplicity)
        val tempPassword = nic + "Doc@2024"
        auth.createUserWithEmailAndPassword(email, tempPassword)
            .addOnSuccessListener { result ->
                val uid = result.user!!.uid
                val doctorData = hashMapOf(
                    "uid"            to uid,
                    "nic"            to nic,
                    "licenseNumber"  to license,
                    "specialization" to specialization,
                    "hospital"       to hospital,
                    "email"          to email,
                    "phone"          to phone,
                    "status"         to "pending",   // admin changes to "verified"
                    "role"           to "doctor"
                )

                db.collection("doctors").document(uid)
                    .set(doctorData)
                    .addOnSuccessListener {
                        // Upload verification document if selected
                        selectedFileUri?.let { uri ->
                            uploadDocument(uid, uri)
                        } ?: run {
                            showSuccessAndGoLogin()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Registration failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun uploadDocument(uid: String, uri: Uri) {
        val ref = storage.reference.child("doctor_docs/$uid/verification_doc")
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { url ->
                    db.collection("doctors").document(uid)
                        .update("documentUrl", url.toString())
                    showSuccessAndGoLogin()
                }
            }
            .addOnFailureListener {
                showSuccessAndGoLogin() // still proceed even if upload fails
            }
    }

    private fun showSuccessAndGoLogin() {
        Toast.makeText(this,
            "Registration submitted! Wait for admin verification.",
            Toast.LENGTH_LONG).show()
        startActivity(Intent(this, DoctorLoginActivity::class.java))
        finish()
    }
}
