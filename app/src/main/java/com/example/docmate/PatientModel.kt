package com.example.docmate

// ── PatientModel.kt ──────────────────────────────────────────
data class PatientModel(
    val patientId : String = "",
    val name      : String = "",
    val condition : String = "",
    val age       : String = ""
)
