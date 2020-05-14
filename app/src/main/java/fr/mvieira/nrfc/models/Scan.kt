package fr.mvieira.nrfc.models

import android.os.Parcelable
import com.couchbase.lite.Dictionary
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Parcelize
class Scan(
    val uid: String,
    val date: Long,
    val tagType: String?,
    val technologies: ArrayList<String>,
    val protocols: ArrayList<String>,
    val techInfos: HashMap<String, String>?,
    val memoryMap: ArrayList<ArrayList<ByteArray>>?,
    val ndefData: ArrayList<ArrayList<ByteArray>>?
) :
    Parcelable {
    companion object {
        @JvmStatic
        fun InitFromDictionary(doc: Dictionary): Scan {
            val technologies = ArrayList<String>()
            val protocols = ArrayList<String>()
            var techInfos = HashMap<String, String>()

            if (!doc.contains("uid"))
                throw Error("`uid` field not present in the document.")

            if (!doc.contains("date"))
                throw Error("`date` field not present in the document.")

            if (!doc.contains("tagType"))
                throw Error("`tagType` field not present in the document.")

            if (!doc.contains("technologies"))
                throw Error("`technologies` field not present in the document.")
            else
                technologies.addAll(
                    doc.getArray("technologies")!!.toList() as ArrayList<String>
                )

            if (!doc.contains("protocols"))
                throw Error("`protocols` field not present in the document.")
            else
                protocols.addAll(doc.getArray("protocols")!!.toList() as ArrayList<String>)

            if (doc.contains("techInfos")) {
                val docContent = doc.getValue("techInfos") as Dictionary

                if (docContent != null)
                    techInfos.putAll(docContent.toMap() as Map<String, String>)
            }

            return Scan(
                doc.getString("uid")!!,
                doc.getDate("date")!!.time,
                doc.getString("tagType"),
                technologies,
                protocols,
                techInfos,
                // memoryMap and ndefData not implemented yet.
                null,
                null
            )
        }

        @JvmStatic
        fun InitFromMap(map: Map<String, Any>): Scan {
            var date: Long

            if (!map.contains("tagId"))
                throw Error("`tagId` field not present in the document.")

            if (!map.contains("date"))
                throw Error("`date` field not present in the document.")
            else {
                if (map["date"] is Date) {
                    val dateObj = map["date"] as Date
                    date = dateObj.time
                } else if (map["date"] is Long)
                    date = map["date"] as Long
                else
                    throw Error("`date` field is not a Date or a timestamp.")
            }

            if (!map.contains("tagType"))
                throw Error("`tagType` field not present in the document.")

            if (!map.contains("techList"))
                throw Error("`techList` field not present in the document.")

            if (!map.contains("protocols"))
                throw Error("`protocols` field not present in the document.")

            return Scan(
                map["tagId"] as String,
                date,
                map["tagType"] as String?,
                map["techList"] as ArrayList<String>,
                map["protocols"] as ArrayList<String>,
                map["techInfos"] as HashMap<String, String>?,
                map["memoryMap"] as ArrayList<ArrayList<ByteArray>>?,
                map["ndefData"] as ArrayList<ArrayList<ByteArray>>?
            )
        }
    }

    fun stringifyTechnologies(): String {
        var techStr: String = "["

        for (tech in technologies)
            techStr += tech.split(".").last() + ", "

        if (techStr.substring(techStr.length - 2, techStr.length) == ", ") {
            techStr = techStr.substring(0, techStr.length - 2) + "]"
        }

        return techStr
    }

    fun stringifyProtocols(): String {
        var protocolsStr: String = "["

        for (protocol in technologies)
            protocolsStr += "$protocol, "

        if (protocolsStr.substring(protocolsStr.length - 2, protocolsStr.length) == ", ") {
            protocolsStr = protocolsStr.substring(0, protocolsStr.length - 2) + "]"
        }

        return protocolsStr
    }
}