package com.uziarnami.kotlinmaterialsearch

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import com.uziarnami.searchmaterialview.interfaces.OnSearchActionListener
import com.uziarnami.searchmaterialview.interfaces.OnSearchListener
import com.uziarnami.searchmaterialview.widget.MaterialSearchView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), OnSearchActionListener, OnSearchListener {
    private val TAG = "MainActivity"

    private var mSearchViewAdded = false
    private var searchActive = false

    lateinit var mWindowManager: WindowManager
    lateinit var mSearchView: MaterialSearchView

    private lateinit var searchItem: MenuItem


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        Log.e(TAG, "onCreate: masuk")
        setupToolbar()
        mSearchView = MaterialSearchView(this, null)
        mSearchView.setOnSearchListener(this)
//        mSearchView.setSearchResultsListener(this)
        mSearchView.setHintText("Search")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        if (menu != null) {
            searchItem = menu.findItem(R.id.search)
            searchItem.setOnMenuItemClickListener {
                Log.e(TAG, "onCreateOptionsMenu: masuk")
                mSearchView.display()
                openKeyboard()
                true
            }
        }
        if (searchActive) {
            mSearchView.display()
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)
    }

    private fun openKeyboard() {
        Handler().postDelayed({
            mSearchView.getSearchView().dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0f, 0f, 0))
            mSearchView.getSearchView().dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0f, 0f, 0))
        }, 200)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)

        if (toolbar != null) {
            // Delay adding SearchView until Toolbar has finished loading
            Log.e(TAG, "setupToolbar: not null")
            toolbar.post(Runnable {
                if (!mSearchViewAdded && mWindowManager != null) {
                    mWindowManager.addView(mSearchView,
                            MaterialSearchView(this, null).getSearchViewLayoutParams(this@MainActivity))
                    mSearchViewAdded = true
                }
            })
        }
    }

    override fun onItemClicked(item: String) {

    }

    override fun onScroll() {

    }

    override fun error(localizedMessage: String) {

    }

    override fun showProgress(show: Boolean) {
        if (show) {
            mSearchView.getProgressFL().visibility = View.VISIBLE
            mSearchView.getSearchPB().visibility = View.VISIBLE
            mSearchView.getNoResultTV().visibility = View.GONE
        } else {
            mSearchView.getProgressFL().visibility = View.GONE
        }
    }

    override fun findItem(query: String, page: Int): ArrayList<String> {
        val result = ArrayList<String>()
        result.add(query)
        return result
    }

    override fun listEmpty() {
        mSearchView.getProgressFL().visibility = View.VISIBLE
        mSearchView.getNoResultTV().visibility = View.VISIBLE
        mSearchView.getSearchPB().visibility = View.GONE
    }

    override fun onSearch(query: String) {
        Log.e(TAG, "onSearch: $query")
    }

    override fun searchViewOpened() {

    }

    override fun searchViewClosed() {

    }

    override fun onCancelSearch() {
        Log.e(TAG, "onCancelSearch: masuk")
        searchActive = false
        mSearchView.hide()
    }
}
