package au.edu.swin.sdmd.myapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.graphics.Color
import androidx.core.content.ContextCompat

interface DiceRoller {
    fun roll(): Int
}

class RandomDiceRoller : DiceRoller {
    override fun roll(): Int {
        return (1..6).random()
    }
}
class MainActivity : AppCompatActivity() {
    private var currentScore = 0
    private var diceRoller: DiceRoller = RandomDiceRoller()
    private var dice = 0

    // Add this method to allow setting a custom DiceRoller for testing
    fun setDiceRoller(diceRoller: DiceRoller) {
        this.diceRoller = diceRoller
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Restore the currentScore if there is a savedInstanceState
        if (savedInstanceState != null) {
            currentScore = savedInstanceState.getInt("currentScore", 0)
            Log.d("MainActivity", "Restored currentScore from savedInstanceState: $currentScore")
        }

        val roll = findViewById<Button>(R.id.roll_button)
        val score_init = findViewById<TextView>(R.id.score_init)
        val image = findViewById<ImageView>(R.id.imageView)
        val score = findViewById<TextView>(R.id.score)
        val add = findViewById<Button>(R.id.add_button)
        val sub = findViewById<Button>(R.id.subtract_button)
        val reset = findViewById<Button>(R.id.reset_button)

        // Set the score to the currentScore
        score.text = currentScore.toString()

        // Restore the currentScore and text color if there is a savedInstanceState
        if (savedInstanceState != null) {
            currentScore = savedInstanceState.getInt("currentScore", 0)
            score.setTextColor(savedInstanceState.getInt("textColor", Color.BLACK))
            Log.d("MainActivity", "Restored currentScore from savedInstanceState: $currentScore")
        }

        add.isEnabled = false
        sub.isEnabled = false

        roll.setOnClickListener {
            // When the roll button is clicked, hide the TextView and show the ImageView
            score_init.visibility = View.INVISIBLE
            image.visibility = View.VISIBLE

            dice = diceRoller.roll()
            Log.d("MainActivity", "Rolled a dice: $dice")

            val drawableResource = when (dice) {
                1 -> R.drawable.dice_1
                2 -> R.drawable.dice_2
                3 -> R.drawable.dice_3
                4 -> R.drawable.dice_4
                5 -> R.drawable.dice_5
                else -> R.drawable.dice_6
            }
            image.setImageResource(drawableResource)

            // Disable the roll button and change its color
            roll.isEnabled = false
            roll.setBackgroundColor(Color.GRAY)

            // Enable the add and subtract buttons
            add.isEnabled = true
            add.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500))
            sub.isEnabled = true
            sub.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500))
        }

        add.setOnClickListener {
            currentScore = score.text.toString().toInt()
            currentScore += dice
            score.text = currentScore.toString()
            Log.d("MainActivity", "Added dice to currentScore: $currentScore")

            // Change the color of the score based on its value
            if (currentScore > 20) {
                score.setTextColor(Color.RED)
            } else if (currentScore == 20) {
                score.setTextColor(Color.GREEN)
            } else {
                score.setTextColor(Color.BLACK)
            }

            // Disable the add and sub button and change its color
            add.isEnabled = false
            add.setBackgroundColor(Color.GRAY)
            sub.isEnabled = false
            sub.setBackgroundColor(Color.GRAY)

            // Enable the roll button
            roll.isEnabled = true
            roll.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500))
        }

        sub.setOnClickListener {
            currentScore = score.text.toString().toInt()
            currentScore -= dice
            score.text = currentScore.toString()
            Log.d("MainActivity", "Subtracted dice from currentScore: $currentScore")

            // Change the color of the score based on its value
            if (currentScore > 20) {
                score.setTextColor(Color.RED)
            } else if (currentScore == 20) {
                score.setTextColor(Color.GREEN)
            } else {
                score.setTextColor(Color.BLACK)
            }

            // Disable the add and sub button and change its color
            add.isEnabled = false
            add.setBackgroundColor(Color.GRAY)
            sub.isEnabled = false
            sub.setBackgroundColor(Color.GRAY)

            // Enable the roll button
            roll.isEnabled = true
            roll.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500))
        }

        reset.setOnClickListener {
            // Reset the score to 0
            score.text = "0"
            score.setTextColor(Color.BLACK)
            Log.d("MainActivity", "Reset currentScore to 0")

            // Disable the add and sub button and change its color
            add.isEnabled = false
            add.setBackgroundColor(Color.GRAY)
            sub.isEnabled = false
            sub.setBackgroundColor(Color.GRAY)

            // Enable the roll button
            roll.isEnabled = true
            roll.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500))

            // Hide the ImageView and show the TextView
            image.visibility = View.INVISIBLE
            score_init.visibility = View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val score = findViewById<TextView>(R.id.score)

        // Save the currentScore and text color into the outState Bundle
        outState.putInt("currentScore", currentScore)
        outState.putInt("textColor", score.currentTextColor)
        Log.d("MainActivity", "Saved currentScore to savedInstanceState: $currentScore")
    }
    
}