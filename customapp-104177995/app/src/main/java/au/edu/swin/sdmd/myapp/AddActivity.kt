package au.edu.swin.sdmd.myapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.app.DatePickerDialog
import android.view.Menu
import android.view.MenuItem
import java.util.Calendar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class AddActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance() // Initialize Firestore instance
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add) // Set the layout for the activity

        val titleInput = findViewById<TextInputEditText>(R.id.titleInput) // Find the title input field
        val dateInput = findViewById<TextInputEditText>(R.id.dateInput) // Find the date input field
        val durationInput = findViewById<TextInputEditText>(R.id.durationInput) // Find the duration input field
        val saveButton = findViewById<Button>(R.id.addButton) // Find the save button

        val calendar = Calendar.getInstance() // Get the current date and time
        val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year) // Format the selected date
            dateInput.setText(selectedDate) // Set the selected date in the date input field
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)) // Initialize date picker dialog with current date

        val dateInputLayout = findViewById<TextInputLayout>(R.id.dateInputLayout) // Find the date input layout
        dateInputLayout.setEndIconOnClickListener {
            datePickerDialog.show() // Show the date picker dialog when the end icon is clicked
        }

        saveButton.setOnClickListener {
            val title = titleInput.text.toString() // Get the text from the title input field
            val date = dateInput.text.toString() // Get the text from the date input field
            val duration = durationInput.text.toString() // Get the text from the duration input field

            if (title.isBlank()) {
                Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show() // Show error if title is empty
                return@setOnClickListener
            }

            if (!date.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))) {
                Toast.makeText(this, "Date must be in dd/mm/yyyy format", Toast.LENGTH_SHORT).show() // Show error if date format is incorrect
                return@setOnClickListener
            }

            val durationNumber = duration.toIntOrNull() // Convert duration to integer
            if (durationNumber == null || durationNumber <= 0) {
                Toast.makeText(this, "Duration must be a number greater than 0", Toast.LENGTH_SHORT).show() // Show error if duration is not a valid number
                return@setOnClickListener
            }

            Log.d("AddActivity", "Saving exercise with title: $title, date: $date, duration: $duration") // Log the exercise details

            val exercise = Exercise(title, date, duration) // Create an exercise object with the input details
            addExerciseWithIncrementedId(exercise) // Add the exercise with an incremented ID
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu) // Inflate the menu options
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, MainActivity::class.java) // Create an intent for MainActivity
                startActivity(intent) // Start MainActivity
                true
            }
            R.id.nav_profile -> {
                val intent = Intent(this, UserProfileActivity::class.java) // Create an intent for UserProfileActivity
                startActivity(intent) // Start UserProfileActivity
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addExerciseWithIncrementedId(exercise: Exercise) {
        val metadataRef = db.collection("metadata").document("exerciseCounter") // Reference to the metadata document for exercise counter
        db.runTransaction { transaction ->
            val snapshot = transaction.get(metadataRef) // Get the current snapshot of the metadata document
            val nextId = (snapshot.getLong("lastId") ?: 0) + 1 // Increment the last ID by 1
            exercise.id = nextId // Set the exercise ID starting from 1
            val exerciseRef = db.collection("exercises").document(nextId.toString()) // Reference to the new exercise document

            transaction.set(exerciseRef, exercise) // Set the new exercise document in the transaction
            transaction.set(metadataRef, mapOf("lastId" to nextId), SetOptions.merge()) // Update the metadata document with the new last ID

            null // Transaction must return null if it's void
        }.addOnSuccessListener {
            Toast.makeText(this, "Exercise added successfully with ID ${exercise.id}", Toast.LENGTH_SHORT).show() // Show success message
            val resultIntent = Intent().apply {
                putExtra("EXERCISE", exercise) // Put the exercise object in the result intent
            }
            setResult(Activity.RESULT_OK, resultIntent) // Set the result of the activity
            finish() // Finish the activity
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed to add exercise: ${e.message}", Toast.LENGTH_SHORT).show() // Show failure message
        }
    }
}
