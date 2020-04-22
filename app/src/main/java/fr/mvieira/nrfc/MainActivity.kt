package fr.mvieira.nrfc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import fr.mvieira.nrfc.models.Scan
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var listview: ListView

    private var scans: ArrayList<Scan> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scans.add(Scan(uid = "00:11:22:33:44", date = System.currentTimeMillis()))
        scans.add(Scan(uid = "00:22:33:44:55", date = System.currentTimeMillis()))
        scans.add(Scan(uid = "00:11:55:66:77", date = System.currentTimeMillis()))

        listview = findViewById(R.id.scan_history_listview)
        val listAdapter = ScanHistoryListAdapter(this, scans)
        listview.adapter = listAdapter
    }
}
