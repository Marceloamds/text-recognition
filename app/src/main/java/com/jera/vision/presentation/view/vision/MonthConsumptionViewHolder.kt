package com.jera.vision.presentation.view.vision

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jera.vision.R
import com.jera.vision.databinding.ItemMonthConsumptionBinding
import com.jera.vision.domain.entity.MonthConsumption
import com.jera.vision.domain.util.resource.format
import java.util.*

class MonthConsumptionViewHolder(
    private var binding: ItemMonthConsumptionBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun setupBinding(item: MonthConsumption) {
        with(binding) {
            textViewConsumption.text = binding.root.context.getString(
                R.string.kwh_consumption_template,
                item.kWhConsumption
            )
            textViewMonth.text = item.month.formatMonthYear()
        }
    }

    private fun Date.formatMonthYear(): String {
        return binding.root.context.getString(
            R.string.month_year_template,
            format("MMMM").toLowerCase().capitalize(),
            format("YYYY")
        )
    }

    companion object {
        fun inflate(parent: ViewGroup?) = MonthConsumptionViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent?.context),
                R.layout.item_month_consumption,
                parent,
                false
            )
        )
    }
}