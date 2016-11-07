package com.uziarnami.searchmaterialview.interfaces

import java.util.*

/**
 * Created by Fauzi Arnami on 11/7/16.
 */

interface OnSearchActionListener {
    fun onItemClicked(item: String)
    fun showProgress(show: Boolean)
    fun findItem(query: String, page: Int): ArrayList<String>
    fun listEmpty()
    fun onScroll()
    fun error(localizedMessage: String)
}