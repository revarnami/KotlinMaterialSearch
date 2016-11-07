package com.uziarnami.searchmaterialview.interfaces

/**
 * Created by Fauzi Arnami on 11/7/16.
 */

interface OnSimpleSearchActionListener {
    fun onItemClicked(item: String)
    fun onScroll()
    fun error(localizedMessage: String)
}