package au.edu.swin.sdmd.myapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Exercise(
    var title: String = "",
    var date: String = "",
    var duration: String = "",
    var id: Long = 1L
) : Parcelable