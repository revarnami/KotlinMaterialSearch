package com.uziarnami.searchmaterialview.interfaces

/**
 * Created by Fauzi Arnami on 11/7/16.
 */

interface OnSearchListener {
    fun onSearch(query: String)
    fun searchViewOpened()
    fun searchViewClosed()
    fun onCancelSearch()
}