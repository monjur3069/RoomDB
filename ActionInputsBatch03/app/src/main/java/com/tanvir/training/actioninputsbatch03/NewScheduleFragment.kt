package com.tanvir.training.actioninputsbatch03

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.tanvir.training.actioninputsbatch03.customdialogs.DatePickerFragment
import com.tanvir.training.actioninputsbatch03.customdialogs.TimePickerFragment
import com.tanvir.training.actioninputsbatch03.databinding.FragmentNewScheduleBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class NewScheduleFragment : Fragment() {
    private val viewModel: ScheduleViewModel by activityViewModels()
    private lateinit var binding: FragmentNewScheduleBinding
    private var from = "Dhaka"
    private var to = "Dhaka"
    private var busType = busTypeEconomy
    private var id: Long? = null
    val REQUEST_IMAGE_CAPTURE = 1
    private var currentPhotoPath: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewScheduleBinding.inflate(inflater, container, false)
        initSpinner()
        initBusTypeRadioGroup()
        id = arguments?.getLong("id")
        if (id != null) {
            binding.saveBtn.setText("Update")
            viewModel.getScheduleById(id!!).observe(viewLifecycleOwner){
                setData(it)
            }
        }
        binding.dateBtn.setOnClickListener {
            DatePickerFragment{
                binding.showDateTV.text = it
            }.show(childFragmentManager, null)
        }
        binding.timeBtn.setOnClickListener {
            TimePickerFragment {
                binding.showTimeTV.text = it
            }.show(childFragmentManager, null)
        }
        binding.captureBtn.setOnClickListener {
            dispatchTakePictureIntent()
        }
        binding.saveBtn.setOnClickListener {
            saveInfo()
        }

        return binding.root
    }

    private fun setData(it: BusSchedule) {
        binding.busNameInputET.setText(it.busName)
        binding.showDateTV.setText(it.departureDate)
        binding.showTimeTV.setText(it.departureTime)
        val fromIndex = cityList.indexOf(it.from)
        val toIndex = cityList.indexOf(it.to)
        binding.citySpinnerFrom.setSelection(fromIndex)
        binding.citySpinnerTo.setSelection(toIndex)
        if (it.busType == busTypeEconomy) {
            binding.busTypeRG.check(R.id.economyRB)
        }else if (it.busType == busTypeBusiness) {
            binding.busTypeRG.check(R.id.businessRB)
        }
    }

    private fun saveInfo() {
        val name = binding.busNameInputET.text.toString()
        val date = binding.showDateTV.text.toString()
        val time = binding.showTimeTV.text.toString()
        // TODO: validate name, date, time
        if (from == to) {
            Toast.makeText(
                requireActivity(),
                "Origin and destination cannot be same",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val schedule = BusSchedule(
            busName = name,
            busType = busType,
            departureDate = date,
            departureTime = time,
            from = from,
            to = to,
            image = currentPhotoPath
        )
        Log.d("NewScheduleFragment", "saveInfo: $schedule")
        if (id != null) {
            schedule.id = id!!
            viewModel.updateSchedule(schedule)
        }else {
            viewModel.addSchedule(schedule)
        }
        findNavController().popBackStack()
        Log.d("NewScheduleFragment", "saveInfo: $schedule")
    }

    private fun initBusTypeRadioGroup() {
        binding.busTypeRG.setOnCheckedChangeListener { radioGroup, i ->
            val rb: RadioButton = radioGroup.findViewById(i)
            busType = rb.text.toString()
        }
    }

    private fun initSpinner() {
        val adapter = ArrayAdapter<String>(
            requireActivity(),
            android.R.layout.simple_spinner_dropdown_item,
            cityList
        )
        binding.citySpinnerFrom.adapter = adapter
        binding.citySpinnerTo.adapter = adapter

        binding.citySpinnerFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                from = p0?.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
        binding.citySpinnerTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                to = p0?.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {

                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireActivity(),
                        "com.tanvir.training.actioninputsbatch03.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            binding.busImage.setImageURI(Uri.parse(currentPhotoPath))
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
            Log.d("NewScheduleFragment", "createImageFile: $currentPhotoPath")
        }
    }

}