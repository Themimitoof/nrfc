package fr.mvieira.nrfc

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import fr.mvieira.nrfc.models.Scan

class ScanResultActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_result)
        setTitle(R.string.scan_result_title)

        val scanData: Scan? = intent.getParcelableExtra("scan")

        if (scanData == null) {
            Toast.makeText(
                this,
                "No information came to us, unable to show this page.",
                Toast.LENGTH_SHORT
            ).show()
            return this.finish()
        }

        // Fill tag information card
        val tagInfoLayout = findViewById<LinearLayout>(R.id.scan_basic_infos_layout)
        val uidTextView = generateTextView()
        uidTextView.text = getString(R.string.result_tag_id) + " " + scanData.uid

        val techTextView = generateTextView()
        val tagType = scanData.tagType
        techTextView.text = getString(R.string.result_tag_technology) + " " + tagType

        val techListView = generateTextView()
        techListView.text = "Tech list: " + scanData.stringifyTechnologies()

        val protocolsListView = generateTextView()
        protocolsListView.text = "Protocols: " + scanData.stringifyProtocols()

        tagInfoLayout.addView(uidTextView)
        tagInfoLayout.addView(techTextView)
        tagInfoLayout.addView(techListView)
        tagInfoLayout.addView(protocolsListView)

        // Fill techInfos informations in information card
        if (scanData.techInfos != null) {
            for (info in scanData.techInfos) {
                val textView = generateTextView()
                textView.text =
                    getStringTranslation("techInfos", info.key) + " " + info.value

                tagInfoLayout.addView(textView)
            }
        }

        // Fill NDEF card
        val ndefInfoLayout = findViewById<LinearLayout>(R.id.scan_ndef_infos_layout)

        if (scanData.ndefData == null) {
            val noNDEFTextView = generateTextView()
            noNDEFTextView.text = getString(R.string.no_ndef_message)
            ndefInfoLayout.addView(noNDEFTextView)
        } else {
            val ndefNotImplemented = generateTextView()
            ndefNotImplemented.text = "Contain NDEF messages but this part is not implemented."
            ndefInfoLayout.addView(ndefNotImplemented)
        }
    }

    private fun generateTextView(): TextView {
        val textView = TextView(this)

        textView.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )

        return textView
    }

    private fun getStringTranslation(namespace: String, key: String): String {
        val resource: Int =
            this.resources.getIdentifier(
                String.format("scan_result_%s_%s", namespace, key),
                "string",
                packageName
            )

        return if (resource != 0) getString(resource) else "$key:"
    }
}
