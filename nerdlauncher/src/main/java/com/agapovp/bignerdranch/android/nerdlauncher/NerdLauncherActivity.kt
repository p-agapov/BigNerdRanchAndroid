package com.agapovp.bignerdranch.android.nerdlauncher

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NerdLauncherActivity : AppCompatActivity() {

    private lateinit var recycledViewApps: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nerd_launcher)

        recycledViewApps =
            findViewById<RecyclerView>(R.id.activity_nerd_launcher_recyclerview_apps).apply {
                layoutManager = LinearLayoutManager(this@NerdLauncherActivity)
            }

        setupAdapter()
    }

    private fun setupAdapter() {
        val startupIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val activities = packageManager.queryIntentActivities(startupIntent, 0).apply {
            sortWith { a, b ->
                String.CASE_INSENSITIVE_ORDER.compare(
                    a.loadLabel(packageManager).toString(),
                    b.loadLabel(packageManager).toString()
                )
            }
        }

        Log.i(TAG, "Found ${activities.size} activities.")
        recycledViewApps.adapter = AppItemAdapter(activities)
    }

    private class AppItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private lateinit var resolveInfo: ResolveInfo

        private val imageApp: ImageView = itemView.findViewById(R.id.item_app_image_app)
        private val textApp: TextView = itemView.findViewById(R.id.item_app_text_app)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val activityInfo = resolveInfo.activityInfo

            view.context.startActivity(
                Intent(Intent.ACTION_MAIN).apply {
                    setClassName(activityInfo.applicationInfo.packageName, activityInfo.name)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        }

        fun bind(resolveInfo: ResolveInfo) {
            this.resolveInfo = resolveInfo
            imageApp.setImageDrawable(resolveInfo.loadIcon(itemView.context.packageManager))
            textApp.text = resolveInfo.loadLabel(itemView.context.packageManager).toString()
        }
    }

    private class AppItemAdapter(val activities: List<ResolveInfo>) :
        RecyclerView.Adapter<AppItemHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppItemHolder =
            AppItemHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_app, parent, false)
            )

        override fun onBindViewHolder(holder: AppItemHolder, position: Int) =
            holder.bind(activities[position])

        override fun getItemCount(): Int = activities.size
    }

    companion object {

        private const val TAG = "NerdLauncherActivity"
    }
}
