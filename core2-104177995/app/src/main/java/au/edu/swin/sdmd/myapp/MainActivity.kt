package au.edu.swin.sdmd.myapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {
    lateinit var boots: MutableList<Boot>
    var currentIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")
        setContentView(R.layout.activity_main)

        boots = mutableListOf(
            Boot("Nike Mercurial Superfly 4 Black", 4.0, "su4", 800, "mercurial_black"),
            Boot("Nike Mercurial Superfly 4 Grey", 4.5, "su4", 850, "mercurial_grey"),
            Boot("Nike Mercurial Superfly 5 Campeoes", 5.0, "su5", 900, "mercurial_campeoes"),
            Boot("Nike Mercurial Superfly 6 Edicao Especial", 5.0, "su6", 1000, "mercurial_edicao_especial"),
            Boot("Nike Mercurial Superfly 4 Diamond", 3.5, "su4", 700, "mercurial_diamond")
        )

        updateUI()

        val next = findViewById<Button>(R.id.next)
        next.setOnClickListener {
            currentIndex = (currentIndex + 1) % boots.size
            Log.d("MainActivity", "Next button clicked, new index: $currentIndex")
            updateUI()
        }

        val borrow = findViewById<Button>(R.id.borrow)
        borrow.setOnClickListener {
            Log.d("MainActivity", "Borrow button clicked")
            val intent = Intent(this, DetailActivity::class.java)

            intent.apply {
                putExtra("boot", boots[currentIndex])
            }

            startForResult.launch(intent)
        }
    }

    private fun updateUI() {
        Log.d("MainActivity", "updateUI called")
        val boot = boots[currentIndex]
        val nameText = findViewById<TextView>(R.id.name)
        nameText?.text = boot.name
        val rate = findViewById<RatingBar>(R.id.ratingBar)
        rate.rating = boot.rating.toFloat()
        val fee = findViewById<TextView>(R.id.price)
        fee.text = "$${boot.price}"

        // Update the category buttons
        val su4Button = findViewById<Button>(R.id.su4)
        val su5Button = findViewById<Button>(R.id.su5)
        val su6Button = findViewById<Button>(R.id.su6)

        // Reset all buttons to faded state
        su4Button.alpha = 0.4f
        su5Button.alpha = 0.4f
        su6Button.alpha = 0.4f

        // Highlight the button that matches the current boot's category
        when (boot.cate) {
            "su4" -> su4Button.alpha = 1.0f
            "su5" -> su5Button.alpha = 1.0f
            "su6" -> su6Button.alpha = 1.0f
        }
        findViewById<ImageView>(R.id.imageView).setImageResource(resources.getIdentifier(boot.image, "drawable", packageName))

        // Update the text of the "Borrow" button
        val borrow = findViewById<Button>(R.id.borrow)
        val layoutParams = borrow.layoutParams
        if (boot.dueBackDate != null) {
            borrow.text = "Due Back: ${boot.dueBackDate}"
            layoutParams.width = (240 * resources.displayMetrics.density).toInt()
            borrow.layoutParams = layoutParams
        } else {
            borrow.text = getString(R.string.borrow)
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            borrow.layoutParams = layoutParams
        }
    }

    var startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d("MainActivity", "Activity result received, result code: ${result.resultCode}")
        when(result.resultCode) {
            RESULT_OK -> {
                val data = result.data
                val boot = data?.getParcelableExtra<Boot>("boot")
                boot?.let{
                    boots[currentIndex] = boot
                    updateUI()
                }
                val dueBackDate = data?.getStringExtra("dueBackDate")
                dueBackDate?.let {
                    val borrow = findViewById<Button>(R.id.borrow)
                    borrow.text = "Due Back: $it"
                }
            }
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the current boot
        outState.putParcelable("currentBoot", boots[currentIndex])
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Restore the current boot
        boots[currentIndex] = savedInstanceState.getParcelable("currentBoot")!!
        updateUI()
    }
}