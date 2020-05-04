package hristostefanov.minibankingdemo.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import hristostefanov.minibankingdemo.R
import hristostefanov.minibankingdemo.presentation.SavingsGoalsViewModel
import kotlinx.android.synthetic.main.savings_goals_fragment.*

class SavingsGoalsFragment : Fragment() {

    private val args: SavingsGoalsFragmentArgs by navArgs()

    private val viewModel: SavingsGoalsViewModel by viewModels {
        viewModelFactory {
            SavingsGoalsViewModel(args).also { sessionComponent().inject(it) }
        }
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
}
