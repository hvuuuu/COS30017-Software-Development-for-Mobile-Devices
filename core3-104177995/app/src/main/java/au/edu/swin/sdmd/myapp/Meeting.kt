import android.content.Context
import android.util.Log
import au.edu.swin.sdmd.myapp.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Meeting(
    val id: Int,
    val group: String,
    val location: String,
    val type: String,
    var datetime: LocalDateTime
)

fun parseCsv(context: Context): List<Meeting> {
    val inputStream = context.resources.openRawResource(R.raw.groups)
    val reader = inputStream.bufferedReader()
    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")
    return reader.useLines { lines ->
        lines.drop(1).map { line ->
            line.split(",").let {
                Meeting(it[0].toInt(), it[1], it[2], it[3], LocalDateTime.parse(it[4], dateTimeFormatter))
            }
        }.toList().sortedBy { it.datetime }
    }
    Log.d("MeetingParser", "Parsing CSV file")
}