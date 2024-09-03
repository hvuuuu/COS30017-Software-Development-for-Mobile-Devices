package au.edu.swin.sdmd.myapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.Slider
import android.widget.Toast
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DetailActivity : AppCompatActivity() {
    var boot: Boot? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        boot = intent.getParcelableExtra<Boot>("boot")
        val vPrice = findViewById<TextView>(R.id.price)
        boot?.let {
            vPrice.text = "$${it.price}"
        }

        val slider = findViewById<Slider>(R.id.slider)
        slider.addOnChangeListener { _, value, _ ->
            boot?.let {
                val newPrice = it.price * value.toInt()
                vPrice.text = "$$newPrice"
            }
        }

        val saveButton = findViewById<Button>(R.id.save)
        saveButton.setOnClickListener {
            val days = slider.value.toInt()
            if (days == 0) {
                Toast.makeText(this, "Error: Please choose a day", Toast.LENGTH_SHORT).show()
            } else {
                val dueBackDate = LocalDate.now().plusDays(days.toLong())
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val formattedDate = dueBackDate.format(formatter)

                boot?.dueBackDate = formattedDate

                val intent = Intent().apply {
                    putExtra("boot", boot)
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
                Toast.makeText(this, "Good choice", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onBackPressed() {
        Toast.makeText(this, "Keep exploring", Toast.LENGTH_SHORT).show()
        super.onBackPressed()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the current boot
        outState.putParcelable("currentBoot", boot)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Restore the current boot
        boot = savedInstanceState.getParcelable("currentBoot")
        updateUI()
    }

    private fun updateUI() {
        val vPrice = findViewById<TextView>(R.id.price)
        boot?.let {
            vPrice.text = "$${it.price}"
            if (it.dueBackDate != null) {
                val dueBackDate = findViewById<Button>(R.id.borrow)
                dueBackDate.text = it.dueBackDate
            }
        }
    }
}