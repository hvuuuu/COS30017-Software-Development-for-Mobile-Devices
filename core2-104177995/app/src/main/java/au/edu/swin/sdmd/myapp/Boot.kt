package au.edu.swin.sdmd.myapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Boot(val name: String, val rating: Double, val cate: String, val price: Int, val image: String, var dueBackDate: String? = null) : Parcelable {
}