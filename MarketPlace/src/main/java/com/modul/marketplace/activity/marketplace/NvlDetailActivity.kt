package com.modul.marketplace.activity.marketplace

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.modul.marketplace.extension.StringExt
import com.modul.marketplace.R
import com.modul.marketplace.activity.BaseActivity
import com.modul.marketplace.adapter.marketplace.ImageSlideAdapter
import com.modul.marketplace.adapter.orderonline.RowItemAdapter
import com.modul.marketplace.app.ApplicationMarketPlace
import com.modul.marketplace.app.Constants
import com.modul.marketplace.extension.showStatusBar
import com.modul.marketplace.model.orderonline.DmServiceListOrigin
import com.modul.marketplace.model.orderonline.RowItemModel
import com.modul.marketplace.util.Utilities
import kotlinx.android.synthetic.main.activity_nvl_detail.*

class NvlDetailActivity : BaseActivity() {

    private lateinit var mAdapter: RowItemAdapter
    private var mResult: ArrayList<RowItemModel> = ArrayList()
    private var nvlModel: DmServiceListOrigin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nvl_detail)
        initAdapter()
        initExtraValue()
        initData()
        initClick()
    }

    private fun initExtraValue() {
        nvlModel = intent?.extras?.let {
            if (it.containsKey(Constants.OBJECT)) {
                it.getSerializable(Constants.OBJECT) as DmServiceListOrigin?
            } else {
                null
            }
        }?.copy()

        nvlModel?.run {
//            initAvatarCompany(mImg,image_thumb,getDrawable(R.drawable.icon_default))
            if(quantity == 0.0){
                mQuantity.text = "1"
                nvlModel?.quantity = 1.0
            }else{
                mQuantity.text = "" + StringExt.convertNumberToString(quantity)
            }
            mResult.add(RowItemModel(title = name, isOnlyTitle = true))
            mResult.add(RowItemModel(title = getString(R.string.gia), content = unitPrice.let { StringExt.convertToMoney(it) } + "/ " + unitName, contentColor = R.color.mainColor, contentStyle = R.style.TextView_SemiBold))
            if (marketPrice != null && unitPrice != marketPrice) {
                mResult.add(RowItemModel(title = getString(R.string.gia_ban), content = marketPrice?.let { StringExt.convertToMoney(it) } + "/ " + unitName, contentColor = R.color.mainColor, contentStyle = R.style.TextView_SemiBold))
            }
            mResult.add(RowItemModel(title = getString(R.string.khu_vuc2), content = supplier_address))
            trademark?.run{
                trademark_name?.run{
                    mResult.add(RowItemModel(title = getString(R.string.brand), content = this))
                }
            }
            mResult.add(RowItemModel(title = getString(R.string.nha_cung_cap), content = supplier_name))
            if(quantity > 0){
                mResult.add(RowItemModel(title = getString(R.string.status), content = getString(R.string.conhang)))
            }else{
                mResult.add(RowItemModel(title = getString(R.string.status), content = getString(R.string.hethang)))
            }
            mResult.add(RowItemModel(title = getString(R.string.description), content = desc))
            initMenu()
        }
        mAdapter.notifyDataSetChanged()
    }

    private fun initMenu() {
        nvlModel?.run {
            val imagesRes: ArrayList<String> = arrayListOf()
            imageUrls?.forEach {
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

    private fun initAdapter() {
        mRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            mAdapter = RowItemAdapter(context, mResult) {

            }
            adapter = mAdapter
        }
    }


    private fun initClick() {
        mClose.setOnClickListener { onBackPressed() }
        mOrder.setOnClickListener { order() }
        mMinus.setOnClickListener { minus() }
        mPlus.setOnClickListener { plus() }
    }

    private fun order() {
        Utilities.sendBoardCounlyLib(ApplicationMarketPlace.context, Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.ADD_SCM_PRODUCT_TO_CART)
        Intent().apply {
            putExtra(Constants.OBJECT, nvlModel)
            setResult(Activity.RESULT_OK, this)
            onBackPressed()
        }
    }

    private fun plus() {
        var quantity = mQuantity.text.toString().toInt()
        mQuantity.text = "" + quantity.plus(1)
        nvlModel?.quantity = mQuantity.text.toString().toDouble()
    }

    private fun minus() {
        var quantity = mQuantity.text.toString().toInt()
        if (quantity > 0) {
            mQuantity.text = "" + quantity.minus(1)
        }
        nvlModel?.quantity = mQuantity.text.toString().toDouble()
    }

    private fun initData() {
        showStatusBar(statusColor = true, color = R.color.white)
        Utilities.sendBoardCounlyLib(ApplicationMarketPlace.context, Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.VIEW_SCM_PRODUCT_DETAIL)
    }
}