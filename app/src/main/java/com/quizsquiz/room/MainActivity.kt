package com.quizsquiz.room

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.quizsquiz.room.db.DatabaseRoom
import com.quizsquiz.room.entities.Pet
import com.quizsquiz.room.entities.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    lateinit var spinner: Spinner
    lateinit var arrayAdapter: ArrayAdapter<String>
    lateinit var job: Job

    private var selectedUserId = ""
    private var listLiveData = MutableLiveData(listOf("Loading list..."))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val userDao = DatabaseRoom.getInstance(this).userDao
        val petDao = DatabaseRoom.getInstance(this).petDao

        GlobalScope.launch {
//            val strings = userDao.getAllUserIds().map { it.toString() }
//            val list = listOf("Select") + strings
//            Log.e("TAG", "$list")
//            withContext(Dispatchers.Main) {
//                listLiveData.value = list
//            }
        }

        spinner = findViewById(R.id.sp_ids)
        listLiveData.observe(this, { list ->
            arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
            spinner.adapter = arrayAdapter
            spinner.onItemSelectedListener = this
        })

        btn_create.setOnClickListener {
            job = Job()
            if (selectedUserId != "Select") {
                val pet = Pet(0, 0, et_pet_name.text.toString(), "")
                job.let { theJob ->
                    CoroutineScope(Dispatchers.IO + theJob).launch {
                        val usr = userDao.findUserById(selectedUserId.toInt())
                        pet.userId = usr.id
                        petDao.insertPet(pet)
                        println(petDao.getPets())
                    }
                    when (theJob.isCompleted) {
                        false -> Toast.makeText(
                            this,
                            "Pet successfully added to user with ID: $selectedUserId",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                val pet = Pet(0, 0, et_pet_name.text.toString(), "")
                val user = User(0, et_user_name.text.toString())
                job.let { theJob ->
                    CoroutineScope(Dispatchers.IO + theJob).launch {
//                        userDao.insertUser(user)
//                        petDao.insertPet(pet)
//                        println(userDao.getUsers())
//                        println(petDao.getPets())
//                println(petDao.getUserById(2))
//                petDao.insertUserIdInPet(userDao.findUserById(1), petDao.getPets())
//                println(userDao.findUserById(1))
//                println(petDao.getPets())
//                println(userDao.getAllUserIds())
                        println(petDao.getPetNames())
                        println(petDao.getPets1())

                    }
                    when (theJob.isCompleted) {
                        false -> Toast.makeText(
                            this,
                            "User ${user.name} and his pet ${pet.name} are successfully added to database.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        Log.e("spin", "$p2")
        et_user_name.isEnabled = p0!!.selectedItemPosition == 0
        selectedUserId = p0.getItemAtPosition(p2) as String
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onStop() {
        super.onStop()
        job.cancel()
    }
}