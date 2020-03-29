package hristostefanov.starlingdemo.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
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

        // launch a lifecycle aware coroutine
        lifecycleScope.launchWhenStarted {
            // the terminating condition of the loop is the cancellation of the coroutine
            while (true) {
                val directions = viewModel.navigationChannel.receive()
                findNavController().navigate(directions)
            }
        }

        addSavingsGoalButton.setOnClickListener {
            viewModel.onAddSavingsGoalCommand()
        }
    }

    private fun onSavingsGoalClicked(position: Int) {
        viewModel.onSavingsGoalClicked(position)
    }

    private inner class ViewModelFactory :
        AbstractSavedStateViewModelFactory(this, args.toBundle()) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return SavingsGoalsViewModel(handle).also { sessionComponent().inject(it) } as T
        }
    }
}
