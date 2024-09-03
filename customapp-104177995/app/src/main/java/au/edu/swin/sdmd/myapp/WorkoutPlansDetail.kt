package au.edu.swin.sdmd.myapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class WorkoutPlansDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_plans_detail)

        val level = intent.getStringExtra("LEVEL")
        val levelText = findViewById<TextView>(R.id.levelText)
        val timeText = findViewById<TextView>(R.id.timeText)
        val noRound = findViewById<TextView>(R.id.no_round)
        val ex1 = findViewById<TextView>(R.id.ex1)
        val rep1 = findViewById<TextView>(R.id.rep1)
        val ex2 = findViewById<TextView>(R.id.ex2)
        val rep2 = findViewById<TextView>(R.id.rep2)
        val ex3 = findViewById<TextView>(R.id.ex3)
        val rep3 = findViewById<TextView>(R.id.rep3)
        val ex4 = findViewById<TextView>(R.id.ex4)
        val rep4 = findViewById<TextView>(R.id.rep4)
        val ex5 = findViewById<TextView>(R.id.ex5)
        val rep5 = findViewById<TextView>(R.id.rep5)

        when (level) {
            "Beginner" -> {
                levelText.text = "Beginner"
                timeText.text = "15 mins"
                noRound.text = "2"
                ex1.text = "Knee push-ups"
                rep1.text = "x8"
                ex2.text = "Crossover crunch"
                rep2.text = "x8"
                ex3.text = "Chair high reach"
                rep3.text = "x8"
                ex4.text = "Dead bug"
                rep4.text = "x8"
                ex5.text = "Backward lunge"
                rep5.text = "x8"
            }
            "Intermediate" -> {
                levelText.text = "Intermediate"
                timeText.text = "25 mins"
                noRound.text = "3"
                ex1.text = "Diamond push-ups"
                rep1.text = "x12"
                ex2.text = "Straight-arm plank"
                rep2.text = "x12"
                ex3.text = "Elbows back"
                rep3.text = "x12"
                ex4.text = "Crunch kicks"
                rep4.text = "x12"
                ex5.text = "Wall push-ups"
                rep5.text = "x12"
            }
            "Advanced" -> {
                levelText.text = "Advanced"
                timeText.text = "45 mins"
                noRound.text = "4"
                ex1.text = "Staggered push-ups"
                rep1.text = "x16"
                ex2.text = "Bicycle crunches"
                rep2.text = "x16"
                ex3.text = "Burpees"
                rep3.text = "x16"
                ex4.text = "Jumping squats"
                rep4.text = "x16"
                ex5.text = "Inchworms"
                rep5.text = "x16"
            }
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