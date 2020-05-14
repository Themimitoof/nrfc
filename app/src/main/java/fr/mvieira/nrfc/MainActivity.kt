package fr.mvieira.nrfc

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.couchbase.lite.*
import com.couchbase.lite.Dictionary
import fr.mvieira.nrfc.helpers.NFC
import fr.mvieira.nrfc.models.Scan
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var listview: ListView
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var pendingIntent: PendingIntent
    private lateinit var couchbaseConfig: DatabaseConfiguration
    private lateinit var couchbaseDB: Database
    private var scans: ArrayList<Scan> = ArrayList()
    private var nfcAdapterAttempts: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Couchbase Lite
        CouchbaseLite.init(baseContext)
        couchbaseConfig = DatabaseConfiguration()
        couchbaseDB = Database("nrfc", couchbaseConfig)

        // Get last 10 items from the database
        val dbResults =
            QueryBuilder.select(SelectResult.all()).from(DataSource.database(couchbaseDB)).orderBy(
                Ordering.property("date").descending()
            ).limit(Expression.intValue(10)).execute()

        for (result in dbResults.allResults()) {
            val result = result.getValue("nrfc") as Dictionary

            try {
                scans.add(Scan.InitFromDictionary(result))
            } catch (e: Error) {
                Log.e(
                    "nrfc",
                    String.format(
                        "%s document is not conform for Scan object.",
                        result.getString("uid")
                    )
                )
            }
        }

        // Fill the list with the last results
        listview = findViewById(R.id.scan_history_listview)
        val listAdapter = ScanHistoryListAdapter(this, scans)
        listview.adapter = listAdapter

        // Setup NFC reception handler
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
    }

    override fun onResume() {
        super.onResume()

        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled && nfcAdapterAttempts < 2) {
                nfcAdapterAttempts++

                Toast.makeText(this, R.string.toast_nfc_not_enabled, Toast.LENGTH_LONG).show()

                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(intent)
            } else if (!nfcAdapter.isEnabled && nfcAdapterAttempts > 1) {
                Toast.makeText(
                    this,
                    R.string.toast_nfc_not_enabled_app_stopped,
                    Toast.LENGTH_LONG
                ).show()

                this.finish()
                exitProcess(1)
            }

            nfcAdapter.enableForegroundDispatch(
                this,
                pendingIntent,
                null,
                null
            )
        }
    }

    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val tagFromIntent: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)

        if (tagFromIntent != null) {
            val scannedDate = Date(System.currentTimeMillis())
//            val ndefMessages: ArrayList<NdefRecord>? =
//                intent.getParcelableArrayListExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)

            val tagInfos = NFC.getBasicInformation(this, tagFromIntent)
            tagInfos["date"] = scannedDate.time

            val newScan = MutableDocument()
            newScan.setString("uid", tagInfos["tagId"] as String)
            newScan.setString("tagType", tagInfos["tagType"] as String)
            newScan.setValue("technologies", tagInfos["techList"])
            newScan.setValue("protocols", tagInfos["protocols"])
            newScan.setValue("techInfos", tagInfos["techInfos"])
            newScan.setValue("memoryMap", null)
            newScan.setValue("ndef", null)
            newScan.setDate("date", scannedDate)

            try {
                couchbaseDB.save(newScan)
            } catch (e: CouchbaseLiteException) {
                Log.e("scan event", "Unable to store the current scan in the database.")
                e.printStackTrace()
            }

            val scan = Scan.InitFromMap(tagInfos)

            Toast.makeText(this, R.string.toast_tag_scanned, Toast.LENGTH_SHORT).show()

            val newIntent = Intent(this, ScanResultActivity::class.java)
            newIntent.putExtra("scan", scan)
            startActivity(newIntent)
        }
    }
}
