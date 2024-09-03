package au.edu.swin.sdmd.myapp

import Meeting
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.format.DateTimeFormatter

class MeetingAdapter(private var meetings: List<Meeting>) : RecyclerView.Adapter<MeetingAdapter.MeetingViewHolder>() {
    private var allMeetings: List<Meeting> = meetings.toList()

    class MeetingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val group: TextView = view.findViewById(R.id.group)
        val datetime: TextView = view.findViewById(R.id.datetime)
        val location: TextView = view.findViewById(R.id.location)
        val icon: ImageView = view.findViewById(R.id.icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.meeting_row, parent, false)
        return MeetingViewHolder(view)
    }

    override fun onBindViewHolder(holder: MeetingViewHolder, position: Int) {
        val meeting = meetings[position]
        holder.group.text = meeting.group
        holder.location.text = meeting.location
        holder.datetime.text = meeting.datetime.format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"))
        // Variation based on meeting type or location
        if (meeting.location == "Online") {
            holder.icon.visibility = View.GONE
        } else {
            holder.icon.visibility = View.VISIBLE
        }
        Log.d("MeetingAdapter", "Binding view holder at position $position")
    }

    override fun getItemCount() = meetings.size

    fun filterByType(type: String) {
        meetings = allMeetings.filter { it.type == type }
        notifyDataSetChanged()
        Log.d("MeetingAdapter", "Filtering by type: $type. Filtered list size: ${meetings.size}")
    }

    fun resetFilter() {
        meetings = allMeetings.toList()
        notifyDataSetChanged()
        Log.d("MeetingAdapter", "Reset filter. List size: ${meetings.size}")
    }
}