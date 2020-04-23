package fr.mvieira.nrfc

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.provider.Settings
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import fr.mvieira.nrfc.models.Scan
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var listview: ListView
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var pendingIntent: PendingIntent
    private var scans: ArrayList<Scan> = ArrayList()
    private var nfcAdapterAttempts: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scans.add(Scan(uid = "00:11:22:33:44", date = System.currentTimeMillis()))
        scans.add(Scan(uid = "00:22:33:44:55", date = System.currentTimeMillis()))
        scans.add(Scan(uid = "00:11:55:66:77", date = System.currentTimeMillis()))

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
                Toast.makeText(this, R.string.toast_nfc_not_enabled_app_stopped, Toast.LENGTH_LONG)
                    .show()

                this.finish()
                exitProcess(1)
            }

            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null)
        }
    }

    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val tagFromIntent: Tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        val action = intent.action

        Toast.makeText(this, "Intent handled!", Toast.LENGTH_SHORT).show()
        //do something with tagFromIntent
//        val intentt = Intent(this, this::class.java)
//        startActivity(intentt)
    }
}
