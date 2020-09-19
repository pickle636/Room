package com.quizsquiz.room.ui

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.quizsquiz.room.MainActivity
import com.quizsquiz.room.Repository
import com.quizsquiz.room.entities.Pet
import com.quizsquiz.room.entities.User
import kotlinx.coroutines.*
import java.util.*

class MainViewModel@ViewModelInject constructor(application: Application, private val repository: Repository): AndroidViewModel(application) {
    var selectedDate = MutableLiveData<String>()
    var selectedUserId = ""
    var idList = MutableLiveData(listOf("Loading list..."))
    private val job = Job()

    var pet = Pet(0, 0, "", selectedDate.value)
    var user = User(0, "")

    init {
        updateUsersId()
    }


    fun create() {
        if (selectedUserId != "Select" && selectedUserId != "Loading list...") {
            viewModelScope.launch(Dispatchers.IO + job) {
                insertPetForSelectedUser()
            }
        } else {
            viewModelScope.launch(Dispatchers.IO + job) {
                insertUserAndPet()
                updateUsersId()
            }
            when (job.isCompleted) {
                false -> Toast.makeText(
                    getApplication(),
                    "User ${user.name} and his pet ${pet.name} are successfully added to database.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }


    private suspend fun insertPetForSelectedUser() {
        val usr = repository.findUserById(selectedUserId.toInt())
        pet.userId = usr.id
        repository.insertPet(pet)
    }
    private suspend fun insertUserAndPet() {
        repository.insertUser(user)
        repository.insertPet(pet)
    }
    fun clickDatePicker(activity: MainActivity) {
        val picker = MaterialDatePicker.Builder.datePicker().build()
        picker.show(activity.supportFragmentManager, picker.toString())
        picker.addOnCancelListener {
            //selectedDate = ""
            Log.d("DatePicker Activity", "Dialog was cancelled")
        }
        picker.addOnNegativeButtonClickListener {
            //selectedDate = ""
            Log.d("DatePicker Activity", "Dialog Negative Button was clicked")
        }
        picker.addOnPositiveButtonClickListener {

            val date = Date(it)
            selectedDate.value = picker.headerText
            Log.d("DatePicker Activity", "Date String = $selectedDate")
        }
    }
    private fun updateUsersId() {
        viewModelScope.launch(Dispatchers.IO + job) {
            val strings = repository.getAllUserIds().map { it.toString() }
            val list = listOf("Select") + strings
            Log.e("TAG", "$list")
            withContext(Dispatchers.Main) {
                idList.value = list
                Toast.makeText(getApplication(), "test", Toast.LENGTH_SHORT).show()
            }
        }
    }
}