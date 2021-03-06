package com.dino.todo.home

import android.app.*
import android.content.Context.ALARM_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.dino.todo.R
import com.dino.todo.database.Todo
import com.dino.todo.database.TodoDatabase
import com.dino.todo.databinding.AddTodoFragmentBinding
import com.dino.todo.utility.Constants.ConstantVariables.NOTIFICATION_CHANNEL
import com.dino.todo.utility.Constants.ConstantVariables.TODO_DESCRIPTION
import com.dino.todo.utility.Constants.ConstantVariables.TODO_TITLE
import com.dino.todo.utility.Constants.ConstantVariables.TODO_UPDATE
import com.google.gson.Gson
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AddTodoFragment : DialogFragment() {
    private lateinit var addTodoViewModel: AddTodoViewModel
    private lateinit var binding: AddTodoFragmentBinding
    private lateinit var alarmManager: AlarmManager
    private lateinit var notifyPendingIntent: PendingIntent
    private lateinit var mNotificationManager: NotificationManager
    private lateinit var todo: Todo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.add_todo_fragment, container, false)
        val application = requireNotNull(this.activity).application
        val dataSource = TodoDatabase.getInstance(application).todoDatabaseDao
        val viewModelFactory = AddTodoViewModelFactory(dataSource, application)
        addTodoViewModel =
            ViewModelProvider(this, viewModelFactory).get(AddTodoViewModel::class.java)
        binding.addTodoViewModel = addTodoViewModel

        if (arguments != null) {
            val todoUpdate = arguments?.get(TODO_UPDATE) as String
            todo = Gson().fromJson(todoUpdate, Todo::class.java)
            binding.todo = todo
            addTodoViewModel.isUpdate = true
        }
        createNotificationChannel()
        return binding.root
    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    private fun createNotificationChannel() {

        // Create a notification manager object.
        mNotificationManager =
            (requireActivity().getSystemService(NOTIFICATION_SERVICE) as NotificationManager?)!!

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            // Create the NotificationChannel with all the parameters.
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        setObservers()
    }

    private fun setObservers() {
        addTodoViewModel.eventSubmit.observe(viewLifecycleOwner, { clicked ->
            if (clicked) {
                updateTodo()
            }
        })

        addTodoViewModel.eventCancel.observe(viewLifecycleOwner, { clicked ->
            if (clicked) {
                dismiss()
                addTodoViewModel.onCancelComplete()
            }
        })

        addTodoViewModel.eventDate.observe(viewLifecycleOwner, { clicked ->
            if (clicked) {
                // Get Current Date
                val c: Calendar = Calendar.getInstance()
                val mYear = c.get(Calendar.YEAR)
                val mMonth = c.get(Calendar.MONTH)
                val mDay = c.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = context?.let {
                    DatePickerDialog(it, R.style.DialogTheme,
                        { _, year, monthOfYear, dayOfMonth ->
                            val date = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                            binding.editTextDate.setText(date)
                        }, mYear, mMonth, mDay
                    )
                }
                datePickerDialog?.datePicker?.minDate = System.currentTimeMillis()
                datePickerDialog?.show()
                addTodoViewModel.onDateClickComplete()
            }
        })

        addTodoViewModel.eventTime.observe(viewLifecycleOwner, { clicked ->
            if (clicked) {
                val c = Calendar.getInstance()
                val mHour = c[Calendar.HOUR_OF_DAY]
                val mMinute = c[Calendar.MINUTE]
                // Launch Time Picker Dialog
                val timePickerDialog = TimePickerDialog(context,
                    R.style.DialogTheme,
                    { _, hourOfDay, minute ->
                        val time = "$hourOfDay:$minute"
                        binding.editTextTime.setText(time)
                    },
                    mHour,
                    mMinute,
                    true
                )
                timePickerDialog.show()
                addTodoViewModel.onTimeClickComplete()
            }
        })
    }

    //Function to update or add a new task to database
    private fun updateTodo() {
        if (binding.titleEditText.text.isNotEmpty()) {
            val newTodo = Todo()
            newTodo.title = binding.titleEditText.text.toString().trim()
            newTodo.description = binding.descriptionEditText.text.toString().trim()

            if (dateInUnixTimeStamp() > 0) {
                newTodo.date = dateInUnixTimeStamp()
                newTodo.isNotification = true
                setNotification(newTodo)
            }
            if (addTodoViewModel.isUpdate) {
                newTodo.todoId = todo.todoId
                newTodo.date = dateInUnixTimeStamp()
                addTodoViewModel.updateTodo(newTodo)
            } else {
                addTodoViewModel.addTodo(newTodo)
            }
            addTodoViewModel.onSubmitComplete()
            dismiss()
        } else {
            Toast.makeText(context, getString(R.string.error_message), Toast.LENGTH_SHORT).show()
        }
    }

    private fun dateInUnixTimeStamp(): Long {
        return if (binding.editTextDate.text.toString().isNotEmpty()) {
            val newDate =
                binding.editTextDate.text.toString() + " " + binding.editTextTime.text.toString()
            val formatter: DateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.getDefault())
            val date = formatter.parse(newDate) as Date
            date.time
        } else 0
    }

    private fun setNotification(todo: Todo) {
        alarmManager = requireActivity().getSystemService(ALARM_SERVICE) as AlarmManager
        val notifyIntent = Intent(context, AlarmReceiver::class.java)
        notifyIntent.putExtra(TODO_TITLE, todo.title)
        notifyIntent.putExtra(TODO_DESCRIPTION, todo.description)
        notifyPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, todo.date, notifyPendingIntent)
    }
}