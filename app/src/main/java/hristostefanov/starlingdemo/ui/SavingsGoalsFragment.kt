package hristostefanov.starlingdemo.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import hristostefanov.starlingdemo.R
import hristostefanov.starlingdemo.presentation.SavingsGoalsViewModel
import kotlinx.android.synthetic.main.savings_goals_fragment.*

class SavingsGoalsFragment : Fragment() {

    private val args: SavingsGoalsFragmentArgs by navArgs()

    private val viewModel: SavingsGoalsViewModel by viewModels {
        UIUnitTestRegistry.viewModelFactory ?: ViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.savings_goals_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savingsGoalsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.list.observe(viewLifecycleOwner, Observer {
            savingsGoalsRecyclerView.adapter =
                SavingsGoalsRecyclerViewAdapter(it, ::onSavingsGoalClicked)
        })

        addSavingsGoalButton.setOnClickListener {
            viewModel.onAddSavingsGoalCommand()
        }
    }

    private fun onSavingsGoalClicked(position: Int) {
        viewModel.onSavingsGoalClicked(position)
    }

    private inner class ViewModelFactory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return SavingsGoalsViewModel(args).also { sessionComponent().inject(it) } as T
        }
    }
}
