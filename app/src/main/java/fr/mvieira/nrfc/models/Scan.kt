package fr.mvieira.nrfc.models

import android.os.Parcelable
import com.couchbase.lite.Dictionary
import com.couchbase.lite.Document
import com.couchbase.lite.Result
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Scan(open val uid: String, open val date: Long, open val tagType: String?) :
    Parcelable {
    companion object {
        @JvmStatic
        fun InitFromDictionary(doc: Dictionary): Scan {
            if(!doc.contains("uid"))
                throw Error("`uid` field not present in the document.")

            if(!doc.contains("date"))
                throw Error("`date` field not present in the document.")

            return Scan(
                doc.getString("uid")!!,
                doc.getDate("date")!!.time,
                doc.getString("tagType")
            )
        }
    }
}