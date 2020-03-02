package hristostefanov.starlingdemo.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hristostefanov.starlingdemo.R
import hristostefanov.starlingdemo.presentation.DisplaySavingsGoal

class SavingsGoalsRecyclerViewAdapter(
    private val list: List<DisplaySavingsGoal>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<SavingsGoalsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewItem =
            LayoutInflater.from(parent.context).inflate(R.layout.savings_goal_item, parent, false)
        return ViewHolder(viewItem)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val savingsGoal = list[position]
        holder.nameTextView.text = savingsGoal.name
        holder.nameTextView.setOnClickListener {
            onClick(position)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
    }
}