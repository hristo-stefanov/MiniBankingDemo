package hristostefanov.minibankingdemo.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import hristostefanov.minibankingdemo.databinding.SavingsGoalsFragmentBinding
import hristostefanov.minibankingdemo.presentation.SavingsGoalsViewModel

class SavingsGoalsFragment : Fragment() {

    private val args: SavingsGoalsFragmentArgs by navArgs()

    private lateinit var binding: SavingsGoalsFragmentBinding

    private val viewModel: SavingsGoalsViewModel by viewModels {
        viewModelFactory {
            SavingsGoalsViewModel(args).also { sessionComponent().inject(it) }
        }
    }

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

        viewModel.list.observe(viewLifecycleOwner, { list ->
            binding.savingsGoalsRecyclerView.adapter =
                SavingsGoalsRecyclerViewAdapter(list){
                    viewModel.onSavingsGoalClicked(it.id)
                }
        })
    }
}
