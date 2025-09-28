package hristostefanov.minibankingdemo.ui


import android.os.Bundle
import android.view.*
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import hristostefanov.minibankingdemo.R
import hristostefanov.minibankingdemo.databinding.AccountsFragmentBinding
import hristostefanov.minibankingdemo.presentation.AccountsViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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

        viewModel.logoutCommandEnabled
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                invalidateOptionsMenu(requireActivity())
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.accounts_options_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val logoutItem = menu.findItem(R.id.logout)
        logoutItem.isVisible = viewModel.logoutCommandEnabled.value
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                viewModel.onLogout()
                true
            }

            R.id.refresh -> {
                viewModel.onRefresh()
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}

