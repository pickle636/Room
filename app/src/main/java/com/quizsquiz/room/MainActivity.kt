package com.quizsquiz.room

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.quizsquiz.room.databinding.ActivityMainBinding
import com.quizsquiz.room.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private val viewModel : MainViewModel by viewModels()
    private lateinit var spinner: Spinner
    private lateinit var arrayAdapter: ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.vm = viewModel
        binding.lifecycleOwner = this

        println(viewModel.selectedDate)

        spinner = findViewById(R.id.sp_ids)
        viewModel.idList.observe(this, { list ->
            arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
            spinner.adapter = arrayAdapter
            spinner.onItemSelectedListener = this
        })

        btn_create.setOnClickListener {
            viewModel.create()
        }
        dp_select_date.setOnClickListener {
            viewModel.clickDatePicker(this@MainActivity)
            println(viewModel.selectedDate)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        Log.e("spin", "$p2")
        et_user_name.isEnabled = p0!!.selectedItemPosition == 0
        viewModel.selectedUserId = p0.getItemAtPosition(p2) as String
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}

//Toast.makeText(
//this,
//"Pet successfully added to user with ID: $selectedUserId",
//Toast.LENGTH_SHORT
//).show()
//
//
//Toast.makeText(
//this,
//"User ${user.name} and his pet ${pet.name} are successfully added to database.",
//Toast.LENGTH_SHORT).show()