package hristostefanov.starlingdemo.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import hristostefanov.starlingdemo.App
import hristostefanov.starlingdemo.R
import hristostefanov.starlingdemo.presentation.SavingsGoalsViewModel
import kotlinx.android.synthetic.main.savings_goals_fragment.*

class SavingsGoalsFragment : Fragment() {

    private lateinit var viewModel: SavingsGoalsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.savings_goals_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = (requireActivity().application as App).viewModelFactory
        viewModel = ViewModelProvider(this, viewModelFactory)[SavingsGoalsViewModel::class.java]

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
            // shortcut, just navigating
            findNavController().navigate(SavingsGoalsFragmentDirections.actionToCreateSavingsGoalDestination())
        }
    }

    private fun onSavingsGoalClicked(position: Int) {
        viewModel.onSavingsGoalClicked(position)
    }
}
