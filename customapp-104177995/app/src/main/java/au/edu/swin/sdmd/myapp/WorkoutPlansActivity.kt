package au.edu.swin.sdmd.myapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class WorkoutPlansActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_plans)
        val nextButton1 = findViewById<ImageButton>(R.id.nextButton1)
        val nextButton2 = findViewById<ImageButton>(R.id.nextButton2)
        val nextButton3 = findViewById<ImageButton>(R.id.nextButton3)

        nextButton1.setOnClickListener {
            val intent = Intent(this, WorkoutPlansDetail::class.java)
            intent.putExtra("LEVEL", "Beginner")
            startActivity(intent)
        }
        nextButton2.setOnClickListener {
            val intent = Intent(this, WorkoutPlansDetail::class.java)
            intent.putExtra("LEVEL", "Intermediate")
            startActivity(intent)
        }
        nextButton3.setOnClickListener {
            val intent = Intent(this, WorkoutPlansDetail::class.java)
            intent.putExtra("LEVEL", "Advanced")
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_profile -> {
                val intent = Intent(this, UserProfileActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_plan -> {
                val intent = Intent(this, WorkoutPlansActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}