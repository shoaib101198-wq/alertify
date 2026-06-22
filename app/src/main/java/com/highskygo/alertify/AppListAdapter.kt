package com.highskygo.alertify

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.materialswitch.MaterialSwitch
import com.highskygo.alertify.model.AppInfo

class AppListAdapter(
    private val onToggle: (AppInfo, Boolean) -> Unit
) : RecyclerView.Adapter<AppListAdapter.VH>() {

    private val items = ArrayList<AppInfo>()

    fun submit(list: List<AppInfo>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val icon: ImageView = v.findViewById(R.id.imgIcon)
        val label: TextView = v.findViewById(R.id.tvLabel)
        val pkg: TextView = v.findViewById(R.id.tvPkg)
        val sw: MaterialSwitch = v.findViewById(R.id.switchApp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.icon.setImageDrawable(item.icon)
        holder.label.text = item.label
        holder.pkg.text = item.packageName
        holder.sw.setOnCheckedChangeListener(null)
        holder.sw.isChecked = item.monitored
        holder.sw.setOnCheckedChangeListener { _, checked ->
            item.monitored = checked
            onToggle(item, checked)
        }
    }

    override fun getItemCount(): Int = items.size
}
