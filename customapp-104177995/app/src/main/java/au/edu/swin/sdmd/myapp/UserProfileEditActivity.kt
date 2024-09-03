package au.edu.swin.sdmd.myapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileEditActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance() // Initialize Firestore instance
    private val PICK_IMAGE_REQUEST = 1 // Request code for image picker
    private var imageUri: Uri? = null // URI for the selected image

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_edit)

        val userData = intent.getParcelableExtra<UserData>("UserData") // Get user data passed from previous activity
        val nameInput = findViewById<TextInputEditText>(R.id.nameInput)
        val occupationInput = findViewById<TextInputEditText>(R.id.occupationInput)
        val ageInput = findViewById<TextInputEditText>(R.id.ageInput)
        val heightInput = findViewById<TextInputEditText>(R.id.heightInput)
        val weightInput = findViewById<TextInputEditText>(R.id.weightInput)
        val imageView = findViewById<ImageView>(R.id.imageInput)
        val updateButton = findViewById<Button>(R.id.updateProfileButton)

        userData?.let {
            nameInput.setText(it.name) // Set name field
            occupationInput.setText(it.occupation) // Set occupation field
            ageInput.setText(it.age) // Set age field
            heightInput.setText(it.height) // Set height field
            weightInput.setText(it.weight) // Set weight field

            val imageUrl = it.imageUrl
            Log.d("UserProfileEditActivity", "Image URL: $imageUrl") // Log the image URL

            if (imageUrl.isNotEmpty()) {
                // Load image with Glide if URL is available
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.roundedbutton) // Placeholder image while loading
                    .error(R.drawable.roundedbutton) // Image to show in case of error
                    .into(imageView)
            } else {
                imageView.setImageResource(R.drawable.roundedbutton) // Default image if no URL
            }
        }

        imageView.setOnClickListener {
            // Launch an image picker to select a new profile picture
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        updateButton.setOnClickListener {
            Log.d("UserProfileEditActivity", "Update button clicked")
            val name = nameInput.text.toString()
            val occupation = occupationInput.text.toString()
            val age = ageInput.text.toString()
            val height = heightInput.text.toString()
            val weight = weightInput.text.toString()

            // Validate input fields
            if (name.isEmpty() || occupation.isEmpty() || age.isEmpty() || height.isEmpty() || weight.isEmpty()) {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ageInt = age.toIntOrNull()
            if (ageInt == null || ageInt < 0) {
                Toast.makeText(this, "Age must be a positive number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val heightDouble = height.toDoubleOrNull()
            if (heightDouble == null) {
                Toast.makeText(this, "Height must be a valid number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val weightDouble = weight.toDoubleOrNull()
            if (weightDouble == null) {
                Toast.makeText(this, "Weight must be a valid number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create updated UserData object
            val updatedUserData = UserData(
                name,
                occupation,
                age,
                height,
                weight,
                imageUri?.toString() ?: userData?.imageUrl ?: ""
            )

            // Update Firestore document with new user data
            db.collection("profile").document("userId")
                .set(updatedUserData)
                .addOnSuccessListener {
                    Toast.makeText(this, "User information updated successfully", Toast.LENGTH_SHORT).show()
                    val resultIntent = Intent().apply {
                        putExtra("UPDATED_USER_DATA", updatedUserData) // Return updated data
                    }
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish() // Close the activity
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error updating user information: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu) // Inflate the menu resource
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent) // Start MainActivity
                true
            }
            R.id.nav_profile -> {
                val intent = Intent(this, UserProfileActivity::class.java)
                startActivity(intent) // Start UserProfileActivity
                true
            }
            R.id.nav_plan -> {
                val intent = Intent(this, WorkoutPlansActivity::class.java)
                startActivity(intent) // Start WorkoutPlansActivity
                true
            }
            else -> super.onOptionsItemSelected(item) // Handle other menu items
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            if (imageUri != null) {
                // Load selected image with Glide
                Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.roundedbutton) // Placeholder image while loading
                    .error(R.drawable.roundedbutton) // Error image if loading fails
                    .into(findViewById(R.id.imageInput))
                // Update the tag with the new image URI
                findViewById<ImageView>(R.id.imageInput).tag = imageUri.toString()
            }
        }
    }

}
