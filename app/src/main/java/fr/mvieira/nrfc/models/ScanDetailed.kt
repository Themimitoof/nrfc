package fr.mvieira.nrfc.models

import com.couchbase.lite.Document
import kotlinx.android.parcel.Parcelize

@Parcelize
class ScanDetailed(
    override val uid: String,
    override val date: Long,
    override val tagType: String?,
    val technologies: ArrayList<String>,
    val ndef: String?
) : Scan(uid, date, tagType) {
    companion object {
        @JvmStatic
        fun InitFromDocument(doc: Document): Scan {
            if(!doc.contains("uid"))
                throw Error("`uid` field not present in the document.")

            if(!doc.contains("date"))
                throw Error("`date` field not present in the document.")

            if(!doc.contains("tagType"))
                throw Error("`tagType` field not present in the document.")

            if(!doc.contains("technologies"))
                throw Error("`technologies` field not present in the document.")

            var techs = ArrayList<String>()
            for(tech in doc.getArray("technologies")!!.toList() as List<String>) {
                techs.add(tech)
            }

            return ScanDetailed(
                doc.getString("uid")!!,
                doc.getDate("date")!!.time,
                doc.getString("tagType"),
                techs,
                null
            )
        }
    }
}