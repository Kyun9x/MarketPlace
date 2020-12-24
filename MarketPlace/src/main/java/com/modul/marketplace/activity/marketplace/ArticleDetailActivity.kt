package com.modul.marketplace.activity.marketplace

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.modul.marketplace.adapter.orderonline.RowItemAdapter
import com.modul.marketplace.R
import com.modul.marketplace.activity.BaseActivity
import com.modul.marketplace.adapter.marketplace.ImageSlideAdapter
import com.modul.marketplace.app.Constants
import com.modul.marketplace.extension.StringExt
import com.modul.marketplace.extension.showStatusBar
import com.modul.marketplace.model.marketplace.ArticlesModel
import com.modul.marketplace.model.marketplace.ArticlesModelDataObject
import com.modul.marketplace.model.orderonline.RowItemModel
import com.modul.marketplace.restful.ApiRequest
import com.modul.marketplace.util.ToastUtil
import com.modul.marketplace.util.Utilities
import kotlinx.android.synthetic.main.activity_article_detail.*

class ArticleDetailActivity : BaseActivity() {

    private lateinit var mAdapter: RowItemAdapter
    private var mResult: ArrayList<RowItemModel> = ArrayList()
    private var dataModel: ArticlesModel? = null
    private var idArticles: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)
        initAdapter()
        initExtraValue()
        initData()
        initClick()
    }

    private fun initAdapter() {
        mRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            mAdapter = RowItemAdapter(context, mResult) {

            }
            adapter = mAdapter
        }
    }

    private fun initExtraValue() {
        idArticles = intent?.extras?.let {
            if (it.containsKey(Constants.OBJECT)) {
                it.getSerializable(Constants.OBJECT) as String?
            } else {
                null
            }
        }

        idArticles?.run{
            apiDetail(this)
        }
    }

    private fun apiDetail(id: String?) {
        showProgressHub(this)

        val callback: ApiRequest<ArticlesModelDataObject> = ApiRequest()
        callback.setCallBack(mApiSCM?.apiSCMArticlesDetail(id.toString()),
                { response -> apiDetailDone(response.data) }) { error ->
            apiDetailDone(null)
            error.printStackTrace()
            ToastUtil.makeText(this, getString(R.string.error_network2))
        }
    }

    private fun apiDetailDone(data: ArticlesModel?) {
        dismissProgressHub()
        data?.run{
            dataModel = this
            mOrder.text = getString(R.string.lienhe_nguoiban) + " - " + mAuthor_phone
            mResult.add(RowItemModel(title = mTitle, isOnlyTitle = true))
            mResult.add(RowItemModel(title = getString(R.string.gia), content = mPrice.let { StringExt.convertToMoney(it) }, contentColor = R.color.mainColor, contentStyle = R.style.TextView_SemiBold))
            mResult.add(RowItemModel(title = getString(R.string.nguoi_ban), content = mAuthor_name))
            mResult.add(RowItemModel(title = getString(R.string.khu_vuc2), content = city?.city_name))
            mResult.add(RowItemModel(title = getString(R.string.description), content = mContent))
            initMenu(this)
        }
        mAdapter.notifyDataSetChanged()
    }

    private fun initMenu(dataModel: ArticlesModel?) {
        dataModel?.run {
            val imagesRes: ArrayList<String> = arrayListOf()
            mImage_urls.forEach {
                it.url?.run {
                    imagesRes.add(this)
                }
            }
            val fragments = ArrayList(imagesRes.map {
                IntroImageFragment.newInstance(it)
            })
            pagerMain.adapter = ImageSlideAdapter(supportFragmentManager, fragments)
            dots_indicator.setViewPager(pagerMain)
        }
}

    private fun initClick() {
        mClose.setOnClickListener { onBackPressed() }
        mOrder.setOnClickListener {
            Utilities.sendBoardCounlyLib(baseContext,Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.TOUCH_CONTACT_AUTHOR)
            Utilities.callPhone(this, dataModel?.mAuthor_phone)
        }
    }

    private fun initData() {
        showStatusBar(statusColor = true, color = R.color.white)
        Utilities.sendBoardCounlyLib(baseContext,Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.VIEW_ARTICLE_DETAIL)
    }
}