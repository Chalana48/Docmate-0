package com.example.docmate

data class IllnessModel(
    val id        : String = "",
    val name      : String = "",
    val diagnosed : String = "",
    val treatment : String = "",
    val doctor    : String = "",
    val status    : String = "Ongoing"
)
