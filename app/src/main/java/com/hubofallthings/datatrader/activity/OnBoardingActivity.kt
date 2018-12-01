package com.hubofallthings.datatrader.activity

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.hubofallthings.datatrader.R
import kotlinx.android.synthetic.main.activity_onboarding.*

class OnboardingActivity : AppCompatActivity(){
    private var viewPager: ViewPager? = null
    private var dotsLayout: LinearLayout? = null
    private var layouts: IntArray? = null
    private var btnSkip: Button? = null
    private var btnNext: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById<ViewPager>(R.id.view_pager)
        dotsLayout = findViewById<LinearLayout>(R.id.layoutDots)
        btnSkip = findViewById<Button>(R.id.btn_skip)
        btnNext = findViewById<Button>(R.id.btn_next)

        layouts =
                intArrayOf(R.layout.onboarding_exchange, R.layout.onboarding_monetise,R.layout.onboarding_donate)

        // adding bottom dots
        addBottomDots(0)
        // making notification bar transparent

        val myViewPagerAdapter = MyViewPagerAdapter()
        viewPager?.adapter = myViewPagerAdapter
        viewPager?.addOnPageChangeListener(viewPagerPageChangeListener)

        btnSkip!!.setOnClickListener { launchHomeScreen() }
        btnNext!!.setOnClickListener {
            // checking for last page
            // if last page home screen will be launched
            val current = getItem(+ 1)
            if (current < layouts!!.size) {
                // move to next screen
                viewPager?.currentItem = current
            } else {
                launchHomeScreen()
            }
        }
    }

    private fun addBottomDots(currentPage: Int) {
        val dots : Array<TextView?> = arrayOfNulls(layouts!!.size)

        val colorsActive = resources.getIntArray(R.array.array_dot_active)
        val colorsInactive = resources.getIntArray(R.array.array_dot_inactive)

        dotsLayout?.removeAllViews()
        for (i in 0 until dots.size) {
            dots[i] = TextView(this)
            dots[i]?.text = Html.fromHtml("&#8226;")
            dots[i]?.textSize = 35f
            dots[i]?.setTextColor(resources.getColor(R.color.dot_dark_screen))
            dotsLayout?.addView(dots[i])
        }

        if (dots.isNotEmpty()){
            dots[currentPage]?.setTextColor(resources.getColor(R.color.dot_light_screen))
        }
    }

    private fun getItem(i: Int): Int {
        return viewPager?.getCurrentItem()!!.plus(i)
    }

    private fun launchHomeScreen() {
        val sharedPreferencesEditor = PreferenceManager.getDefaultSharedPreferences(this).edit()
        sharedPreferencesEditor.putBoolean(
            "onboarding", true)
        sharedPreferencesEditor.apply()
        finish()
    }

    //  viewpager change listener
    var viewPagerPageChangeListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {

        override fun onPageSelected(position: Int) {
            addBottomDots(position)

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts?.size!! - 1) {
                // last page. make button text to GOT IT
                btnNext?.text = getString(R.string.done)
                next_arrow.visibility = View.GONE
                btnSkip?.visibility = View.GONE
            } else {
                // still pages are left
                btnNext?.text = ""
                next_arrow.visibility = View.VISIBLE
                btnSkip?.visibility = View.VISIBLE
            }
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
        }

        override fun onPageScrollStateChanged(arg0: Int) {
        }
    }

    /**
     * View pager adapter
     */
    inner class MyViewPagerAdapter : PagerAdapter() {
        private var layoutInflater: LayoutInflater? = null

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val view = layoutInflater !!.inflate(layouts!![position], container, false)
            container.addView(view)

            return view
        }

        override fun getCount(): Int {
            return layouts!!.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
    }
}