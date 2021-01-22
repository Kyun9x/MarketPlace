package com.modul.marketplace.activity.marketplace

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.modul.marketplace.R
import com.modul.marketplace.activity.BaseFragment
import com.modul.marketplace.adapter.marketplace.ArtilesAdapter
import com.modul.marketplace.app.Constants
import com.modul.marketplace.extension.DialogUtil
import com.modul.marketplace.extension.gone
import com.modul.marketplace.extension.openActivity
import com.modul.marketplace.extension.visible
import com.modul.marketplace.model.marketplace.ArticlesModel
import com.modul.marketplace.model.marketplace.ArticlesModelData
import com.modul.marketplace.model.marketplace.NotificationModel
import com.modul.marketplace.restful.ApiRequest
import com.modul.marketplace.util.PaginationListener
import com.modul.marketplace.util.ToastUtil
import com.modul.marketplace.util.Utilities
import kotlinx.android.synthetic.main.activity_marketplace.*
import kotlinx.android.synthetic.main.fragment_article.*
import timber.log.Timber
import java.util.*

class ArticleFragment : BaseFragment() {
    private var mAdapter: ArtilesAdapter? = null
    private val mDatas = ArrayList<ArticlesModel>()

    private var isLoading = false
    private var currentPage: Int = 1
    private var isLastPage = false
    private val totalPage = 99

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_article, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        context?.let {
            LocalBroadcastManager.getInstance(it)
                    .registerReceiver(onNotice, IntentFilter(Constants.BROADCAST.BROAD_ARTICLES))
        }
        initAdapter()

        currentPage = 1
        isLastPage = false
        initData()
        initClick()
        initExtraItem()
    }

    private fun initExtraItem() {
        var item = mActivity.intent?.extras?.let {
            if (it.containsKey(Constants.KEY_DATA)) {
                it.getSerializable(Constants.KEY_DATA) as NotificationModel?
            } else {
                null
            }
        }

        item?.run {
            if (notify_type == Constants.NotifyStatus.SCM_ARTICLE) {
                Handler().postDelayed({
                    var bundle = Bundle()
                    bundle.putString(Constants.OBJECT, partner_notify_id)
                    openActivity(ArticleDetailActivity::class.java, bundle)
                }, 500)
            }
        }
    }

    private fun initClick() {
        mCreate.setOnClickListener { view: View? ->
            if (TextUtils.isEmpty(mCartBussiness.companyId)) {
                context?.let {
                    DialogUtil.showAlert(it, textTitle = R.string.thongbao, textMessage = R.string.article_companyId_valid, textCancel = R.string.desau, textOk = R.string.lien_he, okListener = {
                       Utilities.callPhone(activity,"19004766")
                    })
                }
            } else {
                openActivity(ArticleCreateActivity::class.java)
            }
        }
    }

    private fun initData() {
        Utilities.sendBoardCounlyLib(context, Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.BROWSER_ARTICLE)
        callServiceList()
    }

    private fun initAdapter() {
        listRecyle?.apply {
            val llm = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            layoutManager = llm
            mAdapter = ArtilesAdapter(context, mDatas) {
                val bundle = Bundle()
                bundle.putSerializable(Constants.OBJECT, it.id)
                openActivity(
                        ArticleDetailActivity::class.java,
                        bundle = bundle
                )
            }
            adapter = mAdapter
            addOnScrollListener(object : PaginationListener(llm) {
                override fun isLastPage(): Boolean {
                    return this@ArticleFragment.isLastPage
                }

                override fun loadMoreItems() {
                    this@ArticleFragment.isLoading = true
                    currentPage++
                    callServiceList()
                }

                override fun isLoading(): Boolean {
                    return this@ArticleFragment.isLoading
                }
            })
        }
    }

    private fun callServiceList() {
        showProgressHub(mActivity)
        val callback: ApiRequest<ArticlesModelData> = ApiRequest()
        callback.setCallBack(mApiSCM?.apiSCMArticles(mCartBussiness.getCartLocate().locateId, mCartBussiness.companyId, mCartBussiness.getListBrandId(),"news",mCartBussiness.userId,currentPage,20),
                { response -> onResponseServiceList(response.data) }) { error ->
            onResponseServiceList(null)
            error.printStackTrace()
            ToastUtil.makeText(mActivity, getString(R.string.error_network2))
        }
    }

    private fun onResponseServiceList(data: ArrayList<ArticlesModel>?) {
        dismissProgressHub()
        try {
            mLoi?.gone()
            data?.run {
                if (currentPage != PaginationListener.PAGE_START) mAdapter?.removeLoading()

                mAdapter?.addItems(this)

                if (currentPage < totalPage) {
                    mAdapter?.addLoading()
                } else {
                    isLastPage = true
                }
                isLoading = false
                if (size < 20) {
                    isLastPage = true
                    mAdapter?.removeLoading()
                }
                mAdapter?.notifyDataSetChanged()
            } ?: run {
//            mAdapter.removeLoading();
                Timber.e("currentPage rong: " + currentPage)
                if (currentPage == 1) {
                    mLoi?.visible()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.let {
            LocalBroadcastManager.getInstance(it).unregisterReceiver(onNotice)
        }
    }

    var onNotice: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            isLastPage = false
            currentPage = 1
            mAdapter?.clear()
            callServiceList()
        }
    }

    companion object {
        fun newInstance(pushNotify : NotificationModel?): ArticleFragment {
            val args = Bundle()
            args.putSerializable("pushNotify", pushNotify)
            var f = ArticleFragment()
            f.arguments = args
            return f
        }
    }
}