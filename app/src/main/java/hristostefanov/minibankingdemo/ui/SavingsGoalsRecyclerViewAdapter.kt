package hristostefanov.minibankingdemo.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hristostefanov.minibankingdemo.business.entities.SavingsGoal
import hristostefanov.minibankingdemo.databinding.SavingsGoalItemBinding
import java.util.function.Consumer

class SavingsGoalsRecyclerViewAdapter(
    private val onClick: (SavingsGoal) -> Unit
) : ListAdapter<SavingsGoal, SavingsGoalsRecyclerViewAdapter.ViewHolder>(
    itemCallback
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SavingsGoalItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val savingsGoal = getItem(position)
        holder.bind(savingsGoal, onClick)
    }

    class ViewHolder(private val binding: SavingsGoalItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SavingsGoal, onClick: Consumer<SavingsGoal>) {
            binding.item = item
            binding.onClick = onClick
            // https://stackoverflow.com/questions/53043412/android-why-use-executependingbindings-in-recyclerview
            // https://github.com/google-developer-training/android-kotlin-fundamentals-apps/blob/master/RecyclerViewHeaders/app/src/main/java/com/example/android/trackmysleepquality/sleeptracker/SleepNightAdapter.kt
            binding.executePendingBindings()
        }
    }

}

private val itemCallback = object : DiffUtil.ItemCallback<SavingsGoal?>() {
    override fun areItemsTheSame(
        oldItem: SavingsGoal,
        newItem: SavingsGoal
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: SavingsGoal,
        newItem: SavingsGoal
    ): Boolean {
        return oldItem == newItem
    }
}
