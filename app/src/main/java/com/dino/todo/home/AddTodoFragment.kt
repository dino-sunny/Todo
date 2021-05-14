package com.dino.todo.home

import android.app.*
import android.content.Context.ALARM_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
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
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class AddTodoFragment : DialogFragment() {
    private lateinit var addTodoViewModel: AddTodoViewModel
    private lateinit var binding: AddTodoFragmentBinding
    private lateinit var alarmManager: AlarmManager
    private lateinit var notifyPendingIntent: PendingIntent
    private lateinit var mNotificationManager: NotificationManager

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.add_todo_fragment, container, false)
        val application = requireNotNull(this.activity).application
        val dataSource = TodoDatabase.getInstance(application).todoDatabaseDao
        val viewModelFactory = AddTodoViewModelFactory(dataSource, application)
        addTodoViewModel = ViewModelProvider(this, viewModelFactory).get(AddTodoViewModel::class.java)
        binding.addTodoViewModel = addTodoViewModel

        setUp()
        createNotificationChannel();

        return binding.root
    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    private fun createNotificationChannel() {

        // Create a notification manager object.
        mNotificationManager = (activity!!.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?)!!

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            val notificationChannel = NotificationChannel("primary_notification_channel",
                    "Stand up notification",
                    NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Notifies every 15 minutes to " +
                    "stand up and walk"
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun setUp() {
        alarmManager = activity!!.getSystemService(ALARM_SERVICE) as AlarmManager
        val notifyIntent = Intent(context, AlarmReceiver::class.java)
        notifyPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        setObservers()
    }

    private fun setObservers() {
        addTodoViewModel.eventSubmit.observe(viewLifecycleOwner, { clicked ->
            if (clicked) {
                if (binding.titleEditText.text.isNotEmpty()) {
                    val newTodo = Todo()
                    newTodo.title = binding.titleEditText.text.toString()
                    newTodo.description = binding.descriptionEditText.text.toString()

                    val date = formatedDate()
                    if (date>0) {
                        newTodo.date = date
                        newTodo.isNotification = true
                        setNotification(newTodo)
                    }

                    addTodoViewModel.addTodo(newTodo)
                    addTodoViewModel.onSubmitComplete()
                    dismiss()
                } else {
                    Toast.makeText(context, "Add a title to add a todo", Toast.LENGTH_SHORT).show()
                }
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
                context?.let {
                    DatePickerDialog(it,
                            { view, year, monthOfYear, dayOfMonth ->
                                binding.editTextDate.setText(dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                            }, mYear, mMonth, mDay)
                }?.show()
            }
        })

        addTodoViewModel.eventTime.observe(viewLifecycleOwner, { clicked ->
            if (clicked) {
                val c = Calendar.getInstance()
                val mHour = c[Calendar.HOUR_OF_DAY]
                val mMinute = c[Calendar.MINUTE]
                // Launch Time Picker Dialog
                val timePickerDialog = TimePickerDialog(context,
                        { view, hourOfDay, minute -> binding.editTextTime.setText("$hourOfDay:$minute") }, mHour, mMinute, true)
                timePickerDialog.show()
            }
        })
    }

    private fun formatedDate(): Long {
        if (binding.editTextDate.text.toString().isNotEmpty()) {
            val str_date = binding.editTextDate.text.toString() + " " + binding.editTextTime.text.toString()
            val formatter: DateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm",Locale.getDefault())
            val date = formatter.parse(str_date) as Date
            return date.time
        }else return 0
    }

    private fun setNotification(todo: Todo) {
        alarmManager.set(AlarmManager.RTC_WAKEUP, todo.date, notifyPendingIntent)
    }
}