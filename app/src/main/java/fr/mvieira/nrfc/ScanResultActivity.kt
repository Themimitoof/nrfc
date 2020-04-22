package fr.mvieira.nrfc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ScanResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_result)
        setTitle(R.string.scan_result_title)
    }
}
