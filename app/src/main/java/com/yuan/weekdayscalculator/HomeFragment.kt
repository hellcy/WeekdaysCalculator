package com.yuan.weekdayscalculator

import android.app.Activity
import android.content.ComponentCallbacks
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Delayed
import kotlin.concurrent.thread


class HomeFragment : Fragment() {
    val startDateCode = 1
    val endDateCode = 2
    var selectedDate = ""
    var startDate = ""
    var endDate = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        view.startDate.setOnClickListener {
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.setTargetFragment(this, startDateCode)
            datePickerFragment.show(activity!!.supportFragmentManager, "datePicker")
        }

        view.endDate.setOnClickListener {
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.setTargetFragment(this, endDateCode)
            datePickerFragment.show(activity!!.supportFragmentManager, "datePicker")
        }

        view.calculateDaysBtn.setOnClickListener {
//            calculateButtonPressed(startDate, endDate) { result ->
//                view.weekdays.setText(result)
//            }
            v -> test(startDate, endDate)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == startDateCode && resultCode == Activity.RESULT_OK) {
            // display Start Date
            selectedDate = data!!.getStringExtra("selectedDate")
            view?.startDate?.setText(selectedDate)
            view?.startDate?.setTextColor(Color.BLACK)
            startDate = selectedDate
        }

        if (requestCode == endDateCode && resultCode == Activity.RESULT_OK) {
            // display End Date
            selectedDate = data!!.getStringExtra("selectedDate")
            view?.endDate?.setText(selectedDate)
            view?.endDate?.setTextColor(Color.BLACK)
            endDate = selectedDate
        }
    }

    private fun test(startDate: String, endDate: String) {
        var test2 = startDate
    }

    private fun calculateButtonPressed(startDate: String, endDate: String, callback: (result: String?) -> Unit) {
        // calculate function
        var result = ""
        val startDateCalendar = Calendar.getInstance()
        startDateCalendar.time =
                SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(startDate)

        val endDateCalendar = Calendar.getInstance()
        endDateCalendar.time = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(endDate)

        if (startDateCalendar.after(endDateCalendar)) result = "Start Date cannot be later than End Date."
        else {

        }

        callback.invoke(result)
    }

    companion object {
        val TAG = HomeFragment::class.java.simpleName
        @JvmStatic
        fun newInstance(startDate: String, endDate: String): HomeFragment {
            val fragment = HomeFragment(startDate, endDate)
            return fragment
        }
    }

}