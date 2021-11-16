package hristostefanov.minibankingdemo.ui


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import hristostefanov.minibankingdemo.R
import hristostefanov.minibankingdemo.databinding.AccountsFragmentBinding
import hristostefanov.minibankingdemo.presentation.AccountsViewModel

@AndroidEntryPoint
class AccountsFragment : Fragment() {
    private lateinit var binding: AccountsFragmentBinding

    private val viewModel: AccountsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = AccountsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // needed for observing LiveData
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.accounts_options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.logout) {
            viewModel.onLogout()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}

