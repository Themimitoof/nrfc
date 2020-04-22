package fr.mvieira.nrfc

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import fr.mvieira.nrfc.models.Scan
import java.sql.Timestamp


class ScanHistoryListAdapter(context: Context, items: ArrayList<Scan>) :
    ArrayAdapter<Scan>(context, R.layout.row_scan_history, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View
        var viewHolder: ScanHolder

        if (convertView == null || convertView?.tag == null) {
            view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.row_scan_history, parent, false)
            viewHolder = ScanHolder()

            viewHolder.uid = view.findViewById(R.id.row_scan_uid) as TextView
            viewHolder.date = view.findViewById(R.id.row_scan_date) as TextView

        } else {
            view = convertView
            viewHolder = convertView?.tag as ScanHolder
        }

        val scan: Scan? = getItem(position)
        viewHolder.uid.setText(String.format("%s %s", context.getString(R.string.scan), scan!!.uid))
        viewHolder.date.setText(
            String.format(
                "%s: %s",
                context.getString(R.string.scanned_at),
                Timestamp(scan!!.date).toString()
            )
        )

        // Add onClick event
        view.setOnClickListener {
            val intent = Intent(context, ScanResultActivity::class.java).apply {
                putExtra("uid", scan!!.uid)
            }
            startActivity(context, intent, null)
        }

        return view
    }


    internal class ScanHolder {
        lateinit var uid: TextView
        lateinit var date: TextView
    }
}