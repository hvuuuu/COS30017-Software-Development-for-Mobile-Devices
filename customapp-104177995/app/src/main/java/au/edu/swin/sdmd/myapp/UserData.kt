package au.edu.swin.sdmd.myapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserData(
    var name: String = "",
    var occupation: String = "",
    var age: String = "",
    var height: String = "",
    var weight: String = "",
    var imageUrl: String = ""
) : Parcelable