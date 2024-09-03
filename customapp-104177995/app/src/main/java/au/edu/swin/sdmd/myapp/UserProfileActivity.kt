package au.edu.swin.sdmd.myapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance() // Initialize Firestore instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        loadUserProfile() // Load the user profile data

        findViewById<ImageButton>(R.id.editProfileImage).setOnClickListener {
            // Create an intent to launch the UserProfileEditActivity
            val editProfileIntent = Intent(this, UserProfileEditActivity::class.java)
            editProfileIntent.putExtra("UserData", getUserDataFromUI()) // Pass current user data to the edit activity
            startActivityForResult(editProfileIntent, EDIT_PROFILE_REQUEST_CODE) // Start the edit activity and expect a result
        }
    }

    private fun loadUserProfile() {
        db.collection("profile").document("userId")
            .get() // Get the document from Firestore
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userData = document.toObject(UserData::class.java) // Convert document to UserData object
                    userData?.let {
                        findViewById<TextView>(R.id.username).text = it.name // Set the username
                        findViewById<TextView>(R.id.occupation).text = it.occupation // Set the occupation
                        findViewById<TextView>(R.id.ageDisplay).text = it.age // Set the age
                        findViewById<TextView>(R.id.heightDisplay).text = it.height // Set the height
                        findViewById<TextView>(R.id.weightDisplay).text = it.weight // Set the weight
                        val imageView = findViewById<ImageView>(R.id.userImage)
                        if (it.imageUrl.isNotEmpty()) {
                            // Load image with Glide if URL is available
                            Glide.with(this)
                                .load(it.imageUrl)
                                .placeholder(R.drawable.roundedbutton) // Placeholder image while loading
                                .error(R.drawable.roundedbutton) // Image to show in case of error
                                .into(imageView)
                            imageView.tag = it.imageUrl // Set the image URL as a tag
                        } else {
                            imageView.setImageResource(R.drawable.roundedbutton) // Default image if no URL
                            imageView.tag = "" // Clear the tag
                        }
                    }
                } else {
                    Log.d("UserProfileActivity", "No such document") // Log if the document is not found
                }
            }
            .addOnFailureListener { exception ->
                Log.d("UserProfileActivity", "get failed with ", exception) // Log any errors
            }
    }

    private fun getUserDataFromUI(): UserData {
        return UserData(
            findViewById<TextView>(R.id.username).text.toString(), // Get username
            findViewById<TextView>(R.id.occupation).text.toString(), // Get occupation
            findViewById<TextView>(R.id.ageDisplay).text.toString(), // Get age
            findViewById<TextView>(R.id.heightDisplay).text.toString(), // Get height
            findViewById<TextView>(R.id.weightDisplay).text.toString(), // Get weight
            (findViewById<ImageView>(R.id.userImage).tag as? String) ?: "" // Get image URL from tag
        )
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
        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.getParcelableExtra<UserData>("UPDATED_USER_DATA")?.let { updatedUserData ->
                // Update UI with the received updated user data
                findViewById<TextView>(R.id.username).text = updatedUserData.name
                findViewById<TextView>(R.id.occupation).text = updatedUserData.occupation
                findViewById<TextView>(R.id.ageDisplay).text = updatedUserData.age
                findViewById<TextView>(R.id.heightDisplay).text = updatedUserData.height
                findViewById<TextView>(R.id.weightDisplay).text = updatedUserData.weight
                val imageView = findViewById<ImageView>(R.id.userImage)
                if (updatedUserData.imageUrl.isNotEmpty()) {
                    // Load updated image with Glide
                    Glide.with(this)
                        .load(updatedUserData.imageUrl)
                        .into(imageView)
                } else {
                    imageView.setImageResource(R.drawable.roundedbutton) // Default image if no URL
                }
            }
        }
    }

    companion object {
        private const val EDIT_PROFILE_REQUEST_CODE = 1 // Request code for editing profile
    }
}
