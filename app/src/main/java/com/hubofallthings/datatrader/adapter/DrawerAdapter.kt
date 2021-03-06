package com.hubofallthings.datatrader.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.hubofallthings.datatrader.R
import com.hubofallthings.datatrader.`object`.DrawerModel

class DrawerAdapter(private val context: Context, arrayList: ArrayList<DrawerModel>) : RecyclerView.Adapter<DrawerAdapter.ViewHolder>() {

    internal var arrayList = ArrayList<DrawerModel>()
    private val inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
        this.arrayList = arrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.navigation_menu_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.setText(arrayList[position].getNames())
        if(position==0){
            holder.iconMenu.setImageResource(R.drawable.data_trader_logo_sign)
            holder.iconMenu.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.name) as TextView
        var iconMenu: ImageView = itemView.findViewById(R.id.menu_icon) as ImageView

        var menuLayout : LinearLayout = itemView.findViewById(R.id.menu_layout) as LinearLayout

    }
}