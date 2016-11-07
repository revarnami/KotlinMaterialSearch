package com.uziarnami.searchmaterialview.widget

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.Rect
import android.os.Build
import android.support.v7.widget.CardView
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.uziarnami.searchmaterialview.R
import com.uziarnami.searchmaterialview.interfaces.OnSearchActionListener
import com.uziarnami.searchmaterialview.interfaces.OnSearchListener
import com.uziarnami.searchmaterialview.interfaces.OnSimpleSearchActionListener
import com.uziarnami.searchmaterialview.utils.Util
import kotlinx.android.synthetic.main.toolbar_searchview.view.*
import java.util.*

/**
 * Created by Fauzi Arnami on 11/7/16.
 */

class MaterialSearchView constructor(context: Context, attrs: AttributeSet?, defStyle: Int = -1) : FrameLayout(context, attrs, defStyle), View.OnClickListener, OnSearchActionListener {
    private val TAG = "MaterialSearchView"

    private lateinit var view: View
    private lateinit var mContext: Context
    private lateinit var fadeIn: Animation
    private lateinit var fadeOut: Animation
    private lateinit var mOnSearchListener: OnSearchListener
    private lateinit var searchListener: OnSimpleSearchActionListener

    init {
        val factory = LayoutInflater.from(context)

        view = factory.inflate(R.layout.toolbar_searchview, this)
        mContext = context

        view.searchET.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        fadeIn = AnimationUtils.loadAnimation(getContext().applicationContext, android.R.anim.fade_in)
        fadeOut = AnimationUtils.loadAnimation(getContext().applicationContext, android.R.anim.fade_out)

        view.clearSearchIV.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP)
        view.searchBackIV.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP)

        view.searchET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (mOnSearchListener != null) {
                    mOnSearchListener.onSearch(getSearchQuery())
                    onQuery(getSearchQuery())
                }
                toggleClearSearchButton(s)
            }
        })

        view.searchET.setOnKeyListener { view, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_SEARCH)) {
                val query = getSearchQuery()
                if (!TextUtils.isEmpty(query) && mOnSearchListener != null) {
                    mOnSearchListener.onSearch(query)
                }
                true
            }
            false
        }

        view.clearSearchIV.setOnClickListener(this)
        view.searchBackIV.setOnClickListener(this)
        visibility = View.GONE
        clearAnimation()
    }

    private fun toggleClearSearchButton(s: CharSequence?) {
        view.clearSearchIV.visibility = if (!TextUtils.isEmpty(s)) View.VISIBLE else View.INVISIBLE
    }

    private var searchViewResults: SearchViewResult? = null

    private fun onQuery(searchQuery: String) {
        val trim = searchQuery.trim({ it <= ' ' })
        if (TextUtils.isEmpty(trim)) {
            view.searchPB.visibility = View.GONE
        }
        if (searchViewResults != null) {
            searchViewResults!!.updateSequence(trim)
        } else {
            searchViewResults = SearchViewResult(mContext, trim)
            searchViewResults!!.setListView(materialSearchLV)
            searchViewResults!!.setSearchProvidersListener(this)
        }
    }

    private fun getSearchQuery(): String {
        return if (view.searchET.text != null) {
            view.searchET.text.toString()
        } else {
            ""
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_UP && event.keyCode == KeyEvent.KEYCODE_BACK) {
            onCancelSearch()
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onClick(v: View?) {
        val id = if (v != null) v.getId() else null
        Log.e(TAG, "onClick: id = $id searchbackid = ${view.searchBackIV.id}")
        if (id == view.searchBackIV.id) {
            Log.e(TAG, "onClick: masuk back")
            onCancelSearch()
        } else if (id == view.clearSearchIV.id) {
            Log.e(TAG, "onClick: masuk clear")
            clearSearch()
        }
    }

    private fun onCancelSearch() {
        if (mOnSearchListener != null) {
            Log.e(TAG, "onCancelSearch: masuk")
            mOnSearchListener.onCancelSearch()
        } else {
            hide()
        }
    }

    fun display() {
        if (isSearchViewVisible()) return
        visibility = View.VISIBLE
        mOnSearchListener.searchViewOpened()
        Log.e(TAG, "display: masuk display")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val animator = ViewAnimationUtils.createCircularReveal(view.cardSearch,
                    view.cardSearch.getWidth() - Util().dpToPx(context, 56f),
                    Util().dpToPx(context, 23f),
                    0f,
                    Math.hypot(view.cardSearch.getWidth().toDouble(), view.cardSearch.getHeight().toDouble()).toFloat())
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    view.viewSearchRL.setVisibility(View.VISIBLE)
                    view.viewSearchRL.startAnimation(fadeIn)
                    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }
            })
            view.cardSearch.visibility = View.VISIBLE
            if (view.cardSearch.getVisibility() == View.VISIBLE) {
                animator.duration = 300
                animator.start()
                view.cardSearch.setEnabled(true)
            }
            fadeIn.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {

                }

                override fun onAnimationEnd(animation: Animation) {

                    view.materialSearchLV.setVisibility(View.VISIBLE)
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
        } else {
            view.cardSearch.visibility = View.VISIBLE
            view.cardSearch.setEnabled(true)

            view.materialSearchLV.setVisibility(View.VISIBLE)
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }

    }

    fun hide() {
        Log.e(TAG, "hide: masuk")
        if (!isSearchViewVisible()) return
        mOnSearchListener.searchViewClosed()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val animatorHide = ViewAnimationUtils.createCircularReveal(view.cardSearch,
                    view.cardSearch.getWidth() - Util().dpToPx(context, 56f),
                    Util().dpToPx(context, 23f),
                    Math.hypot(view.cardSearch.getWidth().toDouble(), view.cardSearch.getHeight().toDouble()).toFloat(),
                    0f)
            animatorHide.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {

                }

                override fun onAnimationEnd(animation: Animator) {
                    view.viewSearchRL.startAnimation(fadeOut)
                    view.viewSearchRL.setVisibility(View.INVISIBLE)
                    view.cardSearch.setVisibility(View.GONE)
                    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(view.viewSearchRL.getWindowToken(), 0)

                    view.materialSearchLV.setVisibility(View.GONE)
                    clearSearch()
                    visibility = View.GONE
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }
            })
            animatorHide.duration = 300
            animatorHide.start()
        } else {
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(view.viewSearchRL.getWindowToken(), 0)
            view.viewSearchRL.startAnimation(fadeOut)
            view.viewSearchRL.setVisibility(View.INVISIBLE)
            view.cardSearch.setVisibility(View.GONE)
            clearSearch()
            visibility = View.GONE
        }
    }

    private fun clearSearch() {
        view.searchET.setText("")
        view.clearSearchIV.visibility = View.INVISIBLE
    }

    private fun isSearchViewVisible(): Boolean {
        return visibility == View.VISIBLE
    }

    override fun onItemClicked(item: String) {
        this.searchListener.onItemClicked(item)
    }

    override fun showProgress(show: Boolean) {
        if (show) {
            view.progressFL.visibility = View.VISIBLE
            view.searchPB.visibility = View.VISIBLE
            view.noResultTV.visibility = View.GONE
        } else {
            view.progressFL.visibility = View.GONE
        }
    }

    override fun listEmpty() {
        view.progressFL.visibility = View.VISIBLE
        view.noResultTV.visibility = View.VISIBLE
        view.searchPB.visibility = View.GONE
    }

    override fun onScroll() {
        this.searchListener.onScroll()
    }

    override fun error(localizedMessage: String) {
        this.searchListener.error(localizedMessage)
    }

    /*
    * Implement the Core search functionality here
    * Could be any local or remote
    */
    override fun findItem(query: String, page: Int): ArrayList<String> {
        val result = ArrayList<String>()
        result.add(query)
        return result
    }

    fun setOnSearchListener(l: OnSearchListener) {
        mOnSearchListener = l
    }

    fun setSearchQuery(query: String) {
        view.searchET.setText(query)
        toggleClearSearchButton(query)
    }

    fun setSearchResultsListener(listener: OnSimpleSearchActionListener) {
        this.searchListener = listener
    }

    fun setHintText(hint: String) {
        view.searchET.hint = hint
    }

    fun getSearchView(): EditText {
        return view.searchET
    }

    fun getCardLayout(): CardView {
        return view.cardSearch
    }

    fun getListView(): ListView {
        return view.materialSearchLV
    }

    fun getLineDivider(): View {
        return view.lineDividerView
    }

    fun getProgressFL(): FrameLayout {
        return view.progressFL
    }

    fun getSearchPB(): ProgressBar {
        return view.searchPB
    }

    fun getNoResultTV(): TextView {
        return view.noResultTV
    }

    fun getSearchViewLayoutParams(activity: Activity): WindowManager.LayoutParams {
        val rect = Rect()
        val window = activity.window
        window.decorView.getWindowVisibleDisplayFrame(rect)
        val statusBarHeight = rect.top

        val actionBarSize = activity.theme.obtainStyledAttributes(
                intArrayOf(R.attr.actionBarSize))
        actionBarSize.recycle()
        val params = WindowManager.LayoutParams(
                rect.right /* This ensures we don't go under the navigation bar in landscape */,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT)

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        params.y = statusBarHeight
        return params
    }
}