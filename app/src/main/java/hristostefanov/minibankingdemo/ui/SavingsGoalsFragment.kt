package hristostefanov.minibankingdemo.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import hristostefanov.minibankingdemo.databinding.SavingsGoalsFragmentBinding
import hristostefanov.minibankingdemo.presentation.SavingsGoalsViewModel

@AndroidEntryPoint
class SavingsGoalsFragment : Fragment() {

    private lateinit var binding: SavingsGoalsFragmentBinding

    private val viewModel: SavingsGoalsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SavingsGoalsFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = SavingsGoalsRecyclerViewAdapter {
            viewModel.onSavingsGoalClicked(it.id)
        }
        binding.savingsGoalsRecyclerView.adapter = adapter

        viewModel.list.observe(viewLifecycleOwner, { list ->
            adapter.submitList(list)
        })
    }
}
