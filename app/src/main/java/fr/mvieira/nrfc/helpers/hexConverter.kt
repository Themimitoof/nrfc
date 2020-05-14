package fr.mvieira.nrfc.helpers

class hexConverter {
    companion object {
        @JvmStatic
        fun bytesToMacFormat(byteArray: ByteArray): String {
            var finalStr: String = ""

            for (b in byteArray) {
                finalStr += String.format("%02X", b)
                finalStr += ":"
            }

            return finalStr.dropLast(1)
        }

        @JvmStatic
        fun bytesToHexString(byteArray: ByteArray): String {
            var finalStr: String = "0x"

            for (b in byteArray) {
                finalStr += String.format("%02X", b)
            }

            return finalStr
        }
    }
}