package com.jera.vision.presentation.view.vision

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.jera.vision.domain.bill.*

class BillTypesAdapter(
    context: Context,
    res: Int
) : ArrayAdapter<Bill>(context, res) {

    private val bills = listOf(
        CopelBill(),
        AesSulBill(),
        CelgBill(),
        EnergisaBill(),
        Energisa2Bill(),
        ElektroBill(),
        CebBill(),
        CelpaBill()
    )

    override fun getCount() = bills.size

    override fun getItem(position: Int): Bill = bills[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent) as TextView
        return view.apply { text = getItem(position).name }
    }
}