package com.hubofallthings.datatrader.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.*
import com.hubofallthings.datatrader.R
import com.hubofallthings.datatrader.`object`.DrawerModel
import com.hubofallthings.datatrader.activity.OnboardingActivity
import com.hubofallthings.datatrader.adapter.DrawerAdapter
import java.util.ArrayList

class DrawerFragment : Fragment() , View.OnClickListener {

    private var views: View? = null
    private val TAG = DrawerFragment::class.java.simpleName
    private var mDrawerToggle: ActionBarDrawerToggle? = null
    private var mDrawerLayout: DrawerLayout? = null
    private var drawerAdapter: DrawerAdapter? = null
    private var containerView: View? = null
    private var recyclerView: RecyclerView? = null
    private var currentActivity : Int = 9
    private val names = arrayOf("Browse offers", "My offers" ,"Settings")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Log.i(TAG,"OnCreateView..")
        views = inflater.inflate(R.layout.fragment_drawer, container, false)
        recyclerView = views!!.findViewById<View>(R.id.listview) as RecyclerView
        val closeNav = views!!.findViewById<LinearLayout>(R.id.close_navigation)
        val aboutNav = views!!.findViewById<LinearLayout>(R.id.about_navigation)


        closeNav.setOnClickListener(this)
        aboutNav.setOnClickListener(this)

        drawerAdapter = DrawerAdapter(activity!!, populateList())
        recyclerView!!.adapter = drawerAdapter
        recyclerView!!.layoutManager = LinearLayoutManager(activity)
        recyclerView!!.addOnItemTouchListener(RecyclerTouchListener(activity!!, recyclerView!!, object : ClickListener {
            override fun onClick(view: View, position: Int) {
                //view.background = resources.getDrawable(R.drawable.dataplug_button_connect)
                openFragment(position)
                mDrawerLayout!!.closeDrawer(containerView!!)
//                if(recyclerView != null) {
//                    for (i in 0 until recyclerView !!.childCount) {
//                        if (position == i) {
//                            recyclerView !!.getChildAt(i)
//                                .setBackgroundColor(ContextCompat.getColor(context!!, R.color.menu_selected_item))
//                        } else {
//                            recyclerView !!.getChildAt(i).setBackgroundColor(Color.TRANSPARENT)
//                        }
//                    }
//                }
            }

            override fun onLongClick(view: View?, position: Int) {

            }
        }))
        openFragment(0)
        return views
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.close_navigation->{
                mDrawerLayout!!.closeDrawer(containerView!!)

            }
            R.id.about_navigation->{
                startActivity(Intent(activity,OnboardingActivity::class.java))
            }
        }
    }
    private fun openFragment(position: Int) {
        Log.i(TAG,"openFragment..")

        val toolbarTitle: TextView? = activity?.findViewById(R.id.toolbar_title)
        val toolbarDrawer : Toolbar? = activity?.findViewById(R.id.toolbar)
//        val toolbarHeader : LinearLayout? = activity?.findViewById(R.id.dataPlugsHeader)

        when (position) {
            0 -> {
                if(currentActivity != position) {
                    currentActivity = position
                    removeAllFragment(BrowseOffersFragment())
                    if (toolbarTitle != null) {
                        toolbarTitle.text = getString(R.string.browse_offers)
                        toolbarDrawer?.setBackgroundColor(ContextCompat.getColor(context!!, R.color.toolbar_color))
                    }
                }
            }
            1 -> {
                if(currentActivity != position) {
                    currentActivity = position
                    removeAllFragment(MyOfferFragment())
                    if (toolbarTitle != null) {
                        toolbarTitle.text = getString(R.string.my_offers)
                        toolbarDrawer?.setBackgroundColor(ContextCompat.getColor(context!!, R.color.toolbar_color))
                    }
                }
            }
            2 -> {
                //settings
                if(currentActivity != position) {
                    currentActivity = position
                    removeAllFragment(SettingsFragment())
                    toolbarTitle!!.text = getString(R.string.hat_settings)
                    toolbarDrawer?.setBackgroundColor(ContextCompat.getColor(context!!, R.color.toolbar_color))
                }

            }
            else -> {
            }
        }
    }

    private fun removeAllFragment(replaceFragment: Fragment) {
        val manager = activity?.supportFragmentManager
        val ft = manager?.beginTransaction()
        manager?.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        ft?.replace(R.id.container_body, replaceFragment)
        ft?.commitAllowingStateLoss()
    }
    fun setUpDrawer(fragmentId: Int, drawerLayout: DrawerLayout, toolbar: Toolbar) {
        containerView = activity?.findViewById(fragmentId)
        mDrawerLayout = drawerLayout
        mDrawerToggle = object : ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                loadImageFromPreference()
                activity?.invalidateOptionsMenu()
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                activity?.invalidateOptionsMenu()
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                toolbar.alpha = 1 - slideOffset / 2
            }
        }
        mDrawerLayout!!.addDrawerListener(mDrawerToggle!!)
        // mDrawerLayout!!.setDrawerListener(mDrawerToggle)
        mDrawerLayout!!.post { mDrawerToggle!!.syncState() }

    }
    private fun loadImageFromPreference(){
        Log.i("DrawerFragment" , "load image from preference")
//        if(context != null){
//            val imageAvatar = activity?.findViewById<ImageView>(R.id.profileImageMenu)
////            val profileImage = Preference(context!!).getProfileImage()
//            if(!profileImage.isNullOrEmpty()){
//                Log.i("DrawerFragment" , "load image is not null")
////                Glide.with(activity!!).load(profileImage).apply(RequestOptions.circleCropTransform().placeholder(R.drawable.hat_logo_small)).into(imageAvatar!!)
//            }
//        }
    }

//    private fun getProfileImage(){
//        if(activity!=null)
//            SystemStatusServices(activity!!).networkManagerProfile()
//    }
    private fun populateList(): ArrayList<DrawerModel> {
        val list = ArrayList<DrawerModel>()

        for (i in names.indices) {
            val drawerModel = DrawerModel()
            drawerModel.name = names[i]
            list.add(drawerModel)
        }
        return list
    }

    interface ClickListener {
        fun onClick(view: View, position: Int)

        fun onLongClick(view: View?, position: Int)
    }

    internal class RecyclerTouchListener(context: Context, recyclerView: RecyclerView, private val clickListener: ClickListener?) : RecyclerView.OnItemTouchListener {

        private val gestureDetector: GestureDetector

        init {
            gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    return true
                }

                override fun onLongPress(e: MotionEvent) {
                    val child = recyclerView.findChildViewUnder(e.x, e.y)
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child))
                    }
                }
            })
        }

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {

            val child = rv.findChildViewUnder(e.x, e.y)
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child))
            }
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

        }


    }

}
