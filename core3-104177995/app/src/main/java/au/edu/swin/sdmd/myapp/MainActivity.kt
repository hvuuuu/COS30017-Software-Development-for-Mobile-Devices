package au.edu.swin.sdmd.myapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import parseCsv

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: MeetingAdapter
    private var isFiltered = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.list)
        val meetings = parseCsv(this) // Assuming parseCsv is a function that parses the CSV and returns a list of Meeting.kt objects

        adapter = MeetingAdapter(meetings)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filter -> {
                if (isFiltered) {
                    adapter.resetFilter()
                    isFiltered = false
                } else {
                    adapter.filterByType("Sport")
                    isFiltered = true
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}