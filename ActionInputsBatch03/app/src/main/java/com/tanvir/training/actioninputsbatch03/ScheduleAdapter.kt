package com.tanvir.training.actioninputsbatch03

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tanvir.training.actioninputsbatch03.databinding.ScheduleRowBinding

class ScheduleAdapter(val favoriteCallback: (BusSchedule) -> Unit, val menuClickCallback: (BusSchedule, RowAction) -> Unit)
    : ListAdapter<BusSchedule, ScheduleAdapter.ScheduleViewHolder>(ScheduleDiffUtil()) {

    class ScheduleViewHolder(val binding: ScheduleRowBinding)
        : RecyclerView.ViewHolder(binding.root) {
            fun bind(busSchedule: BusSchedule) {
                binding.schedule = busSchedule
            }
        }

    class ScheduleDiffUtil : DiffUtil.ItemCallback<BusSchedule>() {
        override fun areItemsTheSame(oldItem: BusSchedule, newItem: BusSchedule): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BusSchedule, newItem: BusSchedule): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ScheduleRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = getItem(position)
        holder.bind(schedule)
        holder.binding.favoriteIV.setOnClickListener {
            schedule.favorite = !schedule.favorite
            holder.bind(schedule)
            Log.d("ScheduleAdapter", "onBindViewHolder: ${schedule.favorite}")
            favoriteCallback(schedule)
        }
        val btn = holder.binding.menuBtn
        holder.binding.menuBtn.setOnClickListener {
            val popupMenu = PopupMenu(btn.context, btn)
            popupMenu.menuInflater.inflate(R.menu.row_menu, popupMenu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener {
                val action : RowAction = when(it.itemId) {
                    R.id.item_edit -> RowAction.EDIT
                    R.id.item_delete -> RowAction.DELETE
                    else -> RowAction.NONE
                }
                menuClickCallback(schedule, action)
                true
            }
        }
    }
}

enum class RowAction {
    EDIT, DELETE, NONE
}