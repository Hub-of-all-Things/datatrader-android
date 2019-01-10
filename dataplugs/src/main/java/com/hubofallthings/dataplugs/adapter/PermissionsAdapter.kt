package com.hubofallthings.dataplugs.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.hubofallthings.android.hatApi.objects.extrernalapps.HATExternalAppsPermissionsRolesGrantedObject
import com.hubofallthings.dataplugs.R

class PermissionsAdapter(private var activity: Activity, private var items: Array<HATExternalAppsPermissionsRolesGrantedObject>?): BaseAdapter() {
    private val txtTitleArr = arrayListOf<String>("Write access" , "Read access" , "Manage files" , "Datadebit access", "List available applications" , "Manage application")
    private val txtDescr = arrayListOf<String>("The app needs to be able to write data in namespace","The app needs to be able to read data in rumpel namespace","Manage files on rumpel namespace", "This app uses data debit app-notables to download your data","This app needs to be able to list other available applications" , "Manage application facebook" , "Manage application twitter" , "Manage application fitbit", "Manage application notables", "Manage application twitter")


    private class ViewHolder(row: View?) {
        var txtTitle: TextView? = null
        var txtSubtitle: TextView? = null

        init {
            this.txtTitle = row?.findViewById<TextView>(R.id.permissions_title)
            this.txtSubtitle = row?.findViewById<TextView>(R.id.permissions_subtitle)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View?
        val viewHolder: ViewHolder

        if (convertView == null) {
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.permissions_list_row, null)
            viewHolder = ViewHolder(view)
            view?.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        val permission = items!![position]
        when(permission.role){
            "namespacewrite"->{
                viewHolder.txtTitle?.text = txtTitleArr[0]
                viewHolder.txtSubtitle?.text = String.format(activity.resources.getString(R.string.namespace_subtitle), permission.detail)
            }
            "namespaceread"->{
                viewHolder.txtTitle?.text = txtTitleArr[1]
                viewHolder.txtSubtitle?.text = txtDescr[1]
            }
            "managefiles"->{
                viewHolder.txtTitle?.text = txtTitleArr[2]
                viewHolder.txtSubtitle?.text = txtDescr[2]
            }
            "datadebit"->{
                viewHolder.txtTitle?.text = txtTitleArr[3]
                viewHolder.txtSubtitle?.text = txtDescr[3]
            }
            "applicationlist"->{
                viewHolder.txtTitle?.text = txtTitleArr[4]
                viewHolder.txtSubtitle?.text = txtDescr[4]
            }
            "applicationmanage"->{
                viewHolder.txtTitle?.text = txtTitleArr[5]
                viewHolder.txtSubtitle?.text = "Manage application ${permission.detail}"
//                if(permission.detail == "facebook"){
//                    viewHolder.txtTitle?.text = txtTitleArr[5]
//                    viewHolder.txtSubtitle?.text = txtDescr[5]
//                } else if (permission.detail == "twitter"){
//                    viewHolder.txtTitle?.text = txtTitleArr[5]
//                    viewHolder.txtSubtitle?.text = txtDescr[6]
//                }
            }
        }
        return view as View
    }
    override fun getItem(i: Int):Array<HATExternalAppsPermissionsRolesGrantedObject>?{
        return items
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getCount(): Int {
        return items!!.size
    }

}