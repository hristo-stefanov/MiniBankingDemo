package hristostefanov.starlingdemo.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import hristostefanov.starlingdemo.R
import hristostefanov.starlingdemo.presentation.DisplayAccount
import kotlinx.android.synthetic.main.account_item.view.*

class AccountListAdapter(private val list: List<DisplayAccount>): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view =  convertView
            ?: (parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.account_item, parent, false)

        val account = list[position]
        view.accountNumTextView.text = account.number
        view.currencyTextView.text = account.currency
        view.balanceTextView.text = account.balance

        return view
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