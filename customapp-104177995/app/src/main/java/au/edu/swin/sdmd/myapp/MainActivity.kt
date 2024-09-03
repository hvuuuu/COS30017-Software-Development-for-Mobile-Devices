package au.edu.swin.sdmd.myapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var exerciseAdapter: ExerciseAdapter // Declare ExerciseAdapter instance
    private val db = FirebaseFirestore.getInstance() // Initialize Firestore instance

    private val addExerciseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d("MainActivity", "ActivityResult received with resultCode: ${result.resultCode}") // Log the result code
        if (result.resultCode == Activity.RESULT_OK) {
            val exercise = result.data?.getParcelableExtra<Exercise>("EXERCISE") // Get the Exercise object from the result data
            exercise?.let {
                Log.d("MainActivity", "Adding exercise: $exercise") // Log the exercise being added
                exerciseAdapter.addExercise(it) // Add the exercise to the adapter
                fetchExercises() // Fetch updated list of exercises
            } ?: Log.d("MainActivity", "No Exercise data received") // Log if no exercise data is received
        }
    }

    private val editExerciseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val updatedExercise = result.data?.getParcelableExtra<Exercise>("UPDATED_EXERCISE") // Get the updated Exercise object from the result data
            val position = result.data?.getIntExtra("POSITION", -1) ?: -1 // Get the position from the result data
            if (updatedExercise != null && position != -1) {
                exerciseAdapter.updateExercise(updatedExercise, position) // Update the exercise in the adapter
                fetchExercises() // Fetch updated list of exercises
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Set the layout for the activity

        val add = findViewById<ImageButton>(R.id.imageButton) // Find the add button
        val recyclerView = findViewById<RecyclerView>(R.id.list) // Find the RecyclerView
        exerciseAdapter = ExerciseAdapter(mutableListOf()) { exercise, position ->
            val intent = Intent(this, EditActivity::class.java).apply {
                putExtra("EXERCISE", exercise) // Put the exercise object in the intent
                putExtra("POSITION", position) // Put the position in the intent
            }
            editExerciseLauncher.launch(intent) // Launch EditActivity with the intent
        }
        recyclerView.adapter = exerciseAdapter // Set the adapter for the RecyclerView

        // Set the LayoutManager
        recyclerView.layoutManager = LinearLayoutManager(this) // Set the layout manager for the RecyclerView

        add.setOnClickListener {
            Log.d("MainActivity", "Launching AddActivity") // Log the launch of AddActivity
            val intent = Intent(this, AddActivity::class.java) // Create an intent for AddActivity
            addExerciseLauncher.launch(intent) // Launch AddActivity with the intent
        }
        fetchExercises() // Fetch the exercises from the database
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

    fun fetchExercises() {
        db.collection("exercises")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d("MainActivity", "No documents found") // Log if no documents are found
                } else {
                    val exercisesList = documents.documents.mapNotNull { doc ->
                        doc.toObject(Exercise::class.java)?.apply {
                            id = doc.id.toLongOrNull() ?: 1L // Set the exercise ID from the document ID
                        }
                    }.sortedBy { it.id } // Sort the exercises by ID
                    exerciseAdapter.updateExercises(exercisesList) // Update the adapter with the fetched exercises
                    Log.d("MainActivity", "Fetched and updated exercises list: ${exercisesList.size} items") // Log the number of fetched items
                }
            }
            .addOnFailureListener { exception ->
                Log.w("MainActivity", "Error getting documents: ", exception) // Log if there is an error getting documents
            }
    }
}
