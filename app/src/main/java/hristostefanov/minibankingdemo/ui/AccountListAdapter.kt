package hristostefanov.minibankingdemo.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import hristostefanov.minibankingdemo.databinding.AccountItemBinding
import hristostefanov.minibankingdemo.presentation.DisplayAccount

class AccountListAdapter(private val list: List<DisplayAccount>): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding: AccountItemBinding = convertView?.let {
            // https://developer.android.com/topic/libraries/data-binding/generated-binding
            // It turns out if a view is associated with a binding as with #inflate below,
            // the binding cannot be changed by using #bind, but the binding variables
            // must be changed instead to reflect the association with a new data item
            DataBindingUtil.getBinding<AccountItemBinding>(it)
        } ?: AccountItemBinding.inflate(layoutInflater, parent, false)
        binding.account = list[position]
        binding.executePendingBindings()
        return binding.root
    }

    // this method is used for Spinner pop-up window
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }
}