package com.quizsquiz.room


import android.content.ContentValues
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.quizsquiz.room.databinding.ActivityMainBinding
import com.quizsquiz.room.ui.MainViewModel
import com.quizsquiz.room.util.MyReceiver
import com.quizsquiz.room.util.MyService
import com.quizsquiz.room.util.StudentsProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var receiver: MyReceiver
    private lateinit var spinner: Spinner
    private lateinit var arrayAdapter: ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )

        binding.vm = viewModel
        binding.lifecycleOwner = this

        receiver = MyReceiver()


        spinner = findViewById(R.id.sp_ids)
        viewModel.idList.observe(this, { list ->
            arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
            spinner.adapter = arrayAdapter
            spinner.onItemSelectedListener = this
        })

        dp_select_date.setOnClickListener {
            viewModel.clickDatePicker(this@MainActivity)
            println(viewModel.selectedDate)
        }

        btn_start_service.setOnClickListener {
            Intent(this, MyService::class.java).also { intent ->
                startService(intent)
            }
        }
        btn_stop_service.setOnClickListener {
            Intent(this, MyService::class.java).also { intent ->
                stopService(intent)
            }
        }

        btn_broadcast_intent.setOnClickListener {
            Intent().also { intent ->
                intent.action = "com.quizsquiz.room.CUSTOM_INTENT"
                sendBroadcast(intent)
            }
            Log.e("tyt", "Here!")
        }

        btn_add_name.setOnClickListener {
            onClickAddName()
        }

        btn_retrieve_student.setOnClickListener {
            onClickRetrieveStudents()
        }
        IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED).also {
            registerReceiver(receiver, it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        Log.e("spin", "$p2")
        et_user_name.isEnabled = p0?.selectedItemPosition == 0
        viewModel.selectedUserId = p0?.getItemAtPosition(p2) as String
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    private fun onClickAddName() {
        val values = ContentValues()
        values.put(
            StudentsProvider.NAME, et_name.text.toString()
        )
        values.put(
            StudentsProvider.GRADE, et_grade.text.toString()
        )
        val uri: Uri? = contentResolver.insert(StudentsProvider.CONTENT_URI, values)
        Toast.makeText(
            baseContext,
            uri.toString(), Toast.LENGTH_LONG
        ).show()
    }

    private fun onClickRetrieveStudents() {
        // Retrieve student records
        val URL = "content://com.quizsquiz.room.util.StudentsProvider"
        val students: Uri = Uri.parse(URL)
        val c: Cursor = managedQuery(students, null, null, null, "name")
        if (c.moveToFirst()) {
            do {
                Toast.makeText(
                    this,
                    c.getString(c.getColumnIndex(StudentsProvider._ID)).toString() +
                            ", " + c.getString(c.getColumnIndex(StudentsProvider.NAME)) +
                            ", " + c.getString(c.getColumnIndex(StudentsProvider.GRADE)),
                    Toast.LENGTH_SHORT
                ).show()
            } while (c.moveToNext())
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }
}