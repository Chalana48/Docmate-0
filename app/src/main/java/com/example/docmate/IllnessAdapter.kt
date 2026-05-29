package com.example.docmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IllnessAdapter(
    private val list: MutableList<IllnessModel>,
    private val onViewClick: (IllnessModel) -> Unit
) : RecyclerView.Adapter<IllnessAdapter.IllnessViewHolder>() {

    inner class IllnessViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName      : TextView = view.findViewById(R.id.tvIllnessName)
        val tvDiagnosed : TextView = view.findViewById(R.id.tvDiagnosed)
        val tvTreatment : TextView = view.findViewById(R.id.tvTreatment)
        val tvDoctorName: TextView = view.findViewById(R.id.tvDoctorName)
        val tvStatus    : TextView = view.findViewById(R.id.tvStatus)
        val btnView     : Button   = view.findViewById(R.id.btnView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IllnessViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_illness, parent, false)
        return IllnessViewHolder(view)
    }

    override fun onBindViewHolder(holder: IllnessViewHolder, position: Int) {
        val illness = list[position]
        holder.tvName.text       = illness.name
        holder.tvDiagnosed.text  = "DIAGNOSED: ${illness.diagnosed.uppercase()}"
        holder.tvTreatment.text  = "TREATMENT: ${illness.treatment.uppercase()}"
        holder.tvDoctorName.text = "DOCTOR: ${illness.doctor.uppercase()}"
        holder.tvStatus.text     = illness.status
        holder.btnView.setOnClickListener { onViewClick(illness) }
    }

    override fun getItemCount() = list.size
}
