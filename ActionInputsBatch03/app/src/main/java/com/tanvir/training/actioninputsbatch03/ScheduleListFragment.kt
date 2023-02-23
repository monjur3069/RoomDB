package com.tanvir.training.actioninputsbatch03

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tanvir.training.actioninputsbatch03.customdialogs.CustomAlertDialog
import com.tanvir.training.actioninputsbatch03.databinding.FragmentScheduleListBinding

class ScheduleListFragment : Fragment() {
    private val viewModel: ScheduleViewModel by activityViewModels()
    private lateinit var binding: FragmentScheduleListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduleListBinding
            .inflate(inflater, container, false)
        val adapter = ScheduleAdapter({busSchedule ->
            viewModel.updateSchedule(busSchedule)
        }, {busSchedule, rowAction ->
            performRowAction(busSchedule, rowAction)
        })
        binding.scheduleRV.layoutManager = LinearLayoutManager(requireActivity())
        binding.scheduleRV.adapter = adapter
        viewModel.getAllSchedule().observe(viewLifecycleOwner) {
            Log.d("ScheduleAdapter", "new list")
            adapter.submitList(it)
        }
        binding.floatingActionButton.setOnClickListener {
            findNavController()
                .navigate(R.id.action_scheduleListFragment_to_newScheduleFragment)
        }
        return binding.root
    }

    private fun performRowAction(schedule: BusSchedule, action: RowAction) {
        when(action) {
            RowAction.EDIT -> {
                val bundle = bundleOf("id" to schedule.id)
                findNavController()
                    .navigate(R.id.action_scheduleListFragment_to_newScheduleFragment, bundle)
            }
            RowAction.DELETE -> {
                CustomAlertDialog(
                    icon = R.drawable.ic_baseline_delete_24,
                    title = "Delete ${schedule.busName}?",
                    body = "Are you sure to delete this item?",
                    positiveButtonText = "YES",
                    negativeButtonText = "CANCEL"
                ){
                    viewModel.deleteSchedule(schedule)
                }.show(childFragmentManager, null)
            }
        }
    }

}