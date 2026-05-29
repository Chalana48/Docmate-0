package com.example.docmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PatientAdapter(
    private val list: MutableList<PatientModel>,
    private val onClick: (PatientModel) -> Unit
) : RecyclerView.Adapter<PatientAdapter.PatientVH>() {

    inner class PatientVH(view: View) : RecyclerView.ViewHolder(view) {
        val tvName      : TextView = view.findViewById(R.id.tvPatientName)
        val tvCondition : TextView = view.findViewById(R.id.tvPatientCondition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_patient, parent, false)
        return PatientVH(view)
    }

    override fun onBindViewHolder(holder: PatientVH, position: Int) {
        val patient = list[position]
        holder.tvName.text      = patient.name.uppercase()
        holder.tvCondition.text = patient.condition.uppercase()
        holder.itemView.setOnClickListener { onClick(patient) }
    }

    override fun getItemCount() = list.size
}
