package com.uziarnami.searchmaterialview.widget

import android.content.Context
import android.os.AsyncTask
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.uziarnami.searchmaterialview.interfaces.OnSearchActionListener
import java.util.*

/**
 * Created by Fauzi Arnami on 11/7/16.
 */

class SearchViewResult(context: Context, searchQuery: String) : AdapterView.OnItemClickListener, AbsListView.OnScrollListener {
    private val TAG = "SearchViewResult"
    private val TRIGGER_SEARCH = 1
    private val SEARCH_TRIGGER_DELAY_IN_MS = 400L

    var mPage = 0
    val MAX_ITEM_COUNT = 5
    private var isLoadMore = false

    private lateinit var sequence: String
    private var searchList = ArrayList<String>()
    private lateinit var mHandler: Handler
    private var mSearch: SearchTask? = null

    private lateinit var mListener: OnSearchActionListener

    private lateinit var mListView: ListView

    private var mAdapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, searchList)

    init {
        sequence = searchQuery
        searchList = ArrayList()
        mAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, searchList)

        mHandler = Handler(Handler.Callback { msg ->
            if (msg.what == TRIGGER_SEARCH) {
                clearAdapter()
                val sequence = msg.obj as String
                Log.e("msg", sequence)
                mSearch = SearchTask()
                mSearch!!.execute(sequence)
            }
            false
        })
    }

    fun setListView(listView: ListView) {
        mListView = listView
        mListView.setOnItemClickListener(this)
        mListView.setOnScrollListener(this)
        mListView.setAdapter(mAdapter)
        updateSequence()
    }

    fun updateSequence(s: String) {
        sequence = s
        updateSequence()
    }

    private fun updateSequence() {
        mPage = 0
        isLoadMore = true

        if (mSearch != null) {
            mSearch!!.cancel(false)
        }
        if (mHandler != null) {
            mHandler.removeMessages(TRIGGER_SEARCH)
        }
        if (!sequence.isEmpty()) {
            val searchMessage = Message()
            searchMessage.what = TRIGGER_SEARCH
            searchMessage.obj = sequence
            mHandler.sendMessageDelayed(searchMessage, SEARCH_TRIGGER_DELAY_IN_MS)
        } else {
            isLoadMore = false
            clearAdapter()
        }
    }

    private fun clearAdapter() {
        mAdapter.clear()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        mListener.onItemClicked(mAdapter.getItem(position) as String)
    }

    override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        val loadMore = totalItemCount > MAX_ITEM_COUNT && firstVisibleItem + visibleItemCount >= totalItemCount
        if (loadMore && isLoadMore) {
            mPage++
            isLoadMore = false
            mSearch = SearchTask()
            mSearch!!.execute(sequence)
        }
    }

    override fun onScrollStateChanged(absListView: AbsListView?, scrollState: Int) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL || scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
            mListener.onScroll()
        }
    }

    fun setSearchProvidersListener(listener: OnSearchActionListener) {
        this.mListener = listener
    }

    /*
    * Search for the item asynchronously
    */
    private inner class SearchTask : AsyncTask<String, Void, ArrayList<String>>() {
        override fun onPreExecute() {
            super.onPreExecute()
            mListener.showProgress(true)
        }

        override fun doInBackground(vararg params: String): ArrayList<String> {
            val query = params[0]
            val result = mListener.findItem(query, mPage)
            return result
        }

        override fun onPostExecute(result: ArrayList<String>) {
            super.onPostExecute(result)
            if (!isCancelled) {
                mListener.showProgress(false)
                if (mPage == 0 && result.isEmpty()) {
                    mListener.listEmpty()
                } else {
                    mAdapter.notifyDataSetInvalidated()
                    mAdapter.addAll(result)
                    mAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}