package com.yuan.weekdayscalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.yuan.weekdayscalculator.models.Hoilday

class MainActivity : AppCompatActivity() {
    private lateinit var homeFragment: HomeFragment
    public var listOfHoildays = mutableListOf<Hoilday>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // create Hoildays for NSW
        listOfHoildays.add(Hoilday("Anzac Day", false, false, 25, 4))
        listOfHoildays.add(Hoilday("New Year's Day", false, true))
        listOfHoildays.add((Hoilday("Australia Day", false, true)))
        listOfHoildays.add(Hoilday("Christmas Day", false, true))
        listOfHoildays.add(Hoilday("Boxing Day", false, true))

        listOfHoildays.add(Hoilday("Easter Monday", true, false))
        listOfHoildays.add(Hoilday("Good Friday", true, false))
        listOfHoildays.add(Hoilday("Queen's Birthday", true, false))
        listOfHoildays.add(Hoilday("Labor Day", true, false))


        homeFragment = HomeFragment()
        setFragment(homeFragment)
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.hostFragment, fragment)
                .commit()
    }
}