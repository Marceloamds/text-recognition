package com.jera.vision.presentation.view.vision

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jera.vision.domain.entity.MonthConsumption

class MonthConsumptionAdapter :
    ListAdapter<MonthConsumption, MonthConsumptionViewHolder>(DiffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthConsumptionViewHolder {
        return MonthConsumptionViewHolder.inflate(parent)
    }

    override fun onBindViewHolder(holder: MonthConsumptionViewHolder, position: Int) {
        holder.setupBinding(getItem(position))
    }

    companion object DiffUtilCallback : DiffUtil.ItemCallback<MonthConsumption>() {
        override fun areItemsTheSame(oldItem: MonthConsumption, newItem: MonthConsumption) =
            oldItem.month.time == newItem.month.time

        override fun areContentsTheSame(oldItem: MonthConsumption, newItem: MonthConsumption) =
            oldItem == newItem
    }
}