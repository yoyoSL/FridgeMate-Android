package com.project.fridgemate.ui.profile
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.project.fridgemate.R
data class AllergyItem(
    val name: String,
    var isChecked: Boolean = false
)
class AllergyAdapter(
    private val items: List<AllergyItem>
) : RecyclerView.Adapter<AllergyAdapter.AllergyViewHolder>() {

    inner class AllergyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.cbAllergy)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllergyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_allergy, parent, false)
        return AllergyViewHolder(view)
    }
    override fun onBindViewHolder(holder: AllergyViewHolder, position: Int) {
        val item = items[position]
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.text = item.name
        holder.checkBox.isChecked = item.isChecked

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            item.isChecked = isChecked
        }
    }
    override fun getItemCount() = items.size
    fun getSelectedAllergies(): List<String> {
        return items.filter { it.isChecked }.map { it.name }
    }
}