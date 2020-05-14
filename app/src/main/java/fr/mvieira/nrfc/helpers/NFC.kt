package fr.mvieira.nrfc.helpers

import android.content.Context
import android.nfc.Tag
import android.nfc.tech.*
import fr.mvieira.nrfc.R

class NFC {
    companion object {
        /**
         * Returns all basic Information of a tag
         *
         * @param tag tag object
         * @return returns a HashMap with all guessed information of the tag
         */
        @JvmStatic
        fun getBasicInformation(context: Context, tag: Tag): HashMap<String, Any> {
            val infos = HashMap<String, Any>()

            val tagId = hexConverter.bytesToMacFormat(tag.id)
            val techList = getTechList(tag)
            val mainTech = guessMainTech(context, techList)
            val protocolsList = guessProtocols(techList)
            var techInfos = getTechInformation(context, tag, techList)

            // Put all data into the returned HashMap
            infos.put("tagId", tagId)
            infos.put("techList", techList)
            infos.put("tagType", mainTech)
            infos.put("protocols", protocolsList)
            infos.put("techInfos", techInfos)

            return infos
        }

        private fun getTechList(tag: Tag): ArrayList<String> {
            var techList = ArrayList<String>()

            for (type in tag.techList) {
                techList.add(type)
            }

            return techList
        }

        private fun guessMainTech(context: Context, techList: ArrayList<String>): String {
            for (type in techList) {
                val tech = type.split('.').last()

                when (tech) {
                    "MifareClassic" -> return "Mifare Classic"
                    "MifareUltralight" -> return "Mifare Ultralight"
                    "IsoDep" -> return "Smart card"
                }
            }

            return context.getString(R.string.tag_technology_unknown)
        }

        /**
         *
         */
        private fun guessProtocols(techList: ArrayList<String>): ArrayList<String> {
            var protocolsList = HashSet<String>()

            for (type in techList) {
                val tech = type.split('.').last()

                when (tech) {
                    "NfcA" -> protocolsList.add("ISO 14443-3A")
                    "NfcB" -> protocolsList.add("ISO 14443-3B")
                    "NfcF" -> protocolsList.add("JIS 6319-4")
                    "NfcV" -> protocolsList.add("ISO 15693")
                }
            }

            return ArrayList(protocolsList)
        }

        private fun getTechInformation(
            context: Context,
            tag: Tag,
            techList: ArrayList<String>
        ): HashMap<String, String> {
            var techInfos = HashMap<String, String>()

            for (tech in techList) {
                val tech = tech.split('.').last()

                when (tech) {
                    "NfcA" -> {
                        val techno = NfcA.get(tag)

                        techInfos["ATQA"] = hexConverter.bytesToHexString(techno.atqa)
                        techInfos["SAK"] = "0x" + techno.sak
                        techInfos["transmitLength"] = techno.maxTransceiveLength.toString()
                    }

                    "NfcB" -> {
                        val techno = NfcB.get(tag)

                        techInfos["transmitLength"] = techno.maxTransceiveLength.toString()
                        techInfos["ATQB"] = hexConverter.bytesToHexString(techno.protocolInfo)
                        techInfos["AppData"] = hexConverter.bytesToHexString(techno.applicationData)
                    }

                    "NfcF" -> {
                        val techno = NfcF.get(tag)

                        techInfos["manufacturer"] =
                            hexConverter.bytesToHexString(techno.manufacturer)
                        techInfos["systemCode"] = hexConverter.bytesToHexString(techno.systemCode)
                        techInfos["transmitLength"] = techno.maxTransceiveLength.toString()
                    }

                    "NfcV" -> {
                        val techno = NfcV.get(tag)

                        techInfos["DSF"] = "0x" + techno.dsfId
                    }

                    "MifareClassic" -> {
                        val techno = MifareClassic.get(tag)

                        techInfos["transmitLength"] = techno.maxTransceiveLength.toString()
                        techInfos["blocksCount"] = techno.blockCount.toString()
                        techInfos["sectorsCount"] = techno.sectorCount.toString()

                        when (techno.size) {
                            MifareClassic.SIZE_1K -> techInfos["mifareSizeType"] =
                                context.getString(R.string.mifare_classic_1k_size)
                            MifareClassic.SIZE_2K -> techInfos["mifareSizeType"] =
                                context.getString(R.string.mifare_classic_2k_size)
                            MifareClassic.SIZE_4K -> techInfos["mifareSizeType"] =
                                context.getString(R.string.mifare_classic_4k_size)
                            MifareClassic.SIZE_MINI -> techInfos["mifareSizeType"] =
                                context.getString(R.string.mifare_classic_mini_size)


                        }

                        when (techno.type) {
                            MifareClassic.TYPE_CLASSIC -> techInfos["mifareType"] = "Mifare Classic"
                            MifareClassic.TYPE_PLUS -> techInfos["mifareType"] =
                                "Mifare Classic Plus"
                            MifareClassic.TYPE_PRO -> techInfos["mifareType"] = "Mifare Classic Pro"
                            MifareClassic.TYPE_UNKNOWN -> techInfos["mifareType"] =
                                context.getString(R.string.mifare_classic_type_unknown)
                        }

                        // TODO: Read and store all sectors and block data
                    }

                    "MifareUltralight" -> {
                        val techno = MifareUltralight.get(tag)

                        techInfos["transmitLength"] = techno.maxTransceiveLength.toString()

                        when (techno.type) {
                            MifareUltralight.TYPE_ULTRALIGHT -> techInfos["ultralightType"] =
                                context.getString(R.string.mifare_ultralight_type_ultralight)
                            MifareUltralight.TYPE_ULTRALIGHT_C -> techInfos["ultralightType"] =
                                context.getString(R.string.mifare_ultralight_type_ultralight_c)
                            MifareUltralight.TYPE_UNKNOWN -> techInfos["ultralightType"] =
                                context.getString(R.string.mifare_ultralight_type_unknown)
                        }

                        // TODO: Read and store all pages
                    }

                    "IsoDep" -> {
                        val techno = IsoDep.get(tag)

                        techInfos["transmitLength"] = techno.maxTransceiveLength.toString()
                        techInfos["extendedLengthAPDU"] =
                            techno.isExtendedLengthApduSupported.toString()
                    }
                }
            }

            return techInfos
        }
    }
}