package au.edu.swin.sdmd.myapp

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class EditActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance() // Initialize Firestore instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit) // Set the layout for the activity

        val exercise = intent.getParcelableExtra<Exercise>("EXERCISE") // Get the exercise object from the intent
        val position = intent.getIntExtra("POSITION", -1) // Get the position from the intent

        val titleInput = findViewById<TextInputEditText>(R.id.titleInput) // Find the title input field
        val dateInput = findViewById<TextInputEditText>(R.id.dateInput) // Find the date input field
        val durationInput = findViewById<TextInputEditText>(R.id.durationInput) // Find the duration input field
        val updateButton = findViewById<Button>(R.id.updateButton) // Find the update button

        exercise?.let {
            titleInput.setText(it.title) // Set the title input field with the exercise title
            dateInput.setText(it.date) // Set the date input field with the exercise date
            durationInput.setText(it.duration) // Set the duration input field with the exercise duration
        }

        val calendar = Calendar.getInstance() // Get the current date and time
        val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year) // Format the selected date
            dateInput.setText(selectedDate) // Set the selected date in the date input field
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)) // Initialize date picker dialog with current date

        val dateInputLayout = findViewById<TextInputLayout>(R.id.dateInputLayout) // Find the date input layout
        dateInputLayout.setEndIconOnClickListener {
            datePickerDialog.show() // Show the date picker dialog when the end icon is clicked
        }

        updateButton.setOnClickListener {
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

            val updatedExercise = Exercise(
                titleInput.text.toString(),
                dateInput.text.toString(),
                durationInput.text.toString(),
                exercise?.id ?: 1L // Use the existing ID or 1 if null
            )

            db.collection("exercises").document(updatedExercise.id.toString())
                .set(updatedExercise)
                .addOnSuccessListener {
                    val resultIntent = Intent().apply {
                        putExtra("UPDATED_EXERCISE", updatedExercise) // Put the updated exercise object in the result intent
                        putExtra("POSITION", position) // Put the position in the result intent
                    }
                    setResult(Activity.RESULT_OK, resultIntent) // Set the result of the activity
                    Toast.makeText(this, "Exercise updated successfully", Toast.LENGTH_SHORT).show() // Show success message
                    finish() // Finish the activity
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to update exercise: ${e.message}", Toast.LENGTH_SHORT).show() // Show failure message
                }
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
            R.id.nav_plan -> {
                val intent = Intent(this, WorkoutPlansActivity::class.java) // Create an intent for WorkoutPlansActivity
                startActivity(intent) // Start WorkoutPlansActivity
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
