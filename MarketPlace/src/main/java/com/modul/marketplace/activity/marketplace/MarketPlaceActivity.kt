package com.modul.marketplace.activity.marketplace

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.MenuItem
import androidx.appcompat.widget.PopupMenu
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.tabs.TabLayout
import com.modul.marketplace.R
import com.modul.marketplace.activity.BaseActivity
import com.modul.marketplace.activity.order_online.PurchaseDetailActivity
import com.modul.marketplace.adapter.marketplace.MarketPlaceAdapter
import com.modul.marketplace.app.Constants
import com.modul.marketplace.app.Constants.BROADCAST.BACK
import com.modul.marketplace.app.Constants.BROADCAST.BROAD_MAKETPLACE
import com.modul.marketplace.app.Constants.Product.HERMES
import com.modul.marketplace.extension.*
import com.modul.marketplace.model.marketplace.*
import com.modul.marketplace.model.orderonline.DmOrderOnline
import com.modul.marketplace.model.orderonline.DmServiceListOrigin
import com.modul.marketplace.paser.orderonline.RestAllDmServiceListOrigin
import com.modul.marketplace.restful.ApiRequest
import com.modul.marketplace.util.ToastUtil
import com.modul.marketplace.util.Utilities
import kotlinx.android.synthetic.main.activity_marketplace.*
import kotlinx.android.synthetic.main.include_header2.*
import timber.log.Timber
import java.util.*


class MarketPlaceActivity : BaseActivity() {

    private val mCitys = ArrayList<AddressModel>()
    private var cityCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marketplace)
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(onNotice, IntentFilter(BROAD_MAKETPLACE))
        initData()
        initClick()
        initExtraItem()
    }

    private fun initExtraItem() {
        var item = intent?.extras?.let {
            if (it.containsKey(Constants.KEY_DATA)) {
                it.getSerializable(Constants.KEY_DATA) as NotificationModel?
            } else {
                null
            }
        }

        initMenu(item)

        item?.run {
            if (notify_type == Constants.NotifyStatus.SCM_ARTICLE) {
                pagerMain.currentItem = 2
                Handler().postDelayed({ tab_layout.getTabAt(2)?.select() }, 100)

//                Handler().postDelayed({
//                    var bundle = Bundle()
//                    bundle.putString(Constants.OBJECT, partner_notify_id)
//                    openActivity(ArticleDetailActivity::class.java, bundle)
//                }, 1000)
            } else if (notify_type == Constants.NotifyStatus.PRODUCT) {
                notify_detail?.run {
                    product_type?.run {
                        if (this == HERMES) {
                            pagerMain.currentItem = 0
                            Handler().postDelayed({ tab_layout.getTabAt(0)?.select() }, 100)
//                            id?.run {
//                                apiMenuHermes(this)
//                            }
                        } else {
                            pagerMain.currentItem = 1
                            Handler().postDelayed({ tab_layout.getTabAt(1)?.select() }, 100)
//                            id?.run {
//                                apiMenuNvl(this)
//                            }
                        }
                    }
                }
            } else {

            }
        }
    }

    private fun apiMenuNvl(id: String) {
        val callback: ApiRequest<NvlModelData> = ApiRequest()
        callback.setCallBack(mApiSCM?.apiSCMProducts(1, mCartBussiness.getCartLocate().locateId, 1, 999),
                { response -> onResponseServiceListNvl(response.data, id) }) { error ->
            error.printStackTrace()
        }
    }

    private fun onResponseServiceListNvl(data: ArrayList<NvlModel>?, id: String) {
        data?.forEach {
            if (it.id.equals(id)) {
                val dmServiceListOrigin = DmServiceListOrigin()
                dmServiceListOrigin.quantity = 0.0
                dmServiceListOrigin.image = it.image_url_avatar
                dmServiceListOrigin.imageUrls = it.image_urls
                dmServiceListOrigin.name = it.name
                dmServiceListOrigin.desc = it.description
                dmServiceListOrigin.unitPrice = it.price!!
                dmServiceListOrigin.marketPrice = it.price_sale!!
                dmServiceListOrigin.productUid = it.id
                dmServiceListOrigin.unitName = it.unit?.unit_name
                dmServiceListOrigin.supplier_address = it.supplier?.supplier_address
                dmServiceListOrigin.supplier_name = it.supplier?.supplier_name
                dmServiceListOrigin.supplierUid = it.supplier_uid
                dmServiceListOrigin.code = it.id
                dmServiceListOrigin.brand_name = it.brand?.brand_name
                dmServiceListOrigin.trademark = it.trademark

                Handler().postDelayed({
                    val bundle = Bundle()
                    bundle.putSerializable(Constants.OBJECT, dmServiceListOrigin)
                    openActivity(NvlDetailActivity::class.java, bundle = bundle)
                }, 1000)

            }
        }
    }

    private fun apiMenuHermes(id: String) {
        var productType = ""
        if (mCartBussiness.appType == Constants.FABI) {
            productType = Constants.FABI
        } else {
            productType = Constants.POSPC
        }

        val callback: ApiRequest<RestAllDmServiceListOrigin> = ApiRequest()
        callback.setCallBack(mApiHermes?.apiOrderOnline_ServiceList(productType),
                { response -> onResponseServiceList(response.data, id) }) { error ->
        }
    }

    private fun onResponseServiceList(data: ArrayList<DmServiceListOrigin>?, id: String) {
        data?.forEach {
            if (it.uId.equals(id)) {
                Handler().postDelayed({
                    val bundle = Bundle()
                    bundle.putSerializable(Constants.OBJECT, it)
                    openActivity(PurchaseDetailActivity::class.java, bundle = bundle)
                }, 1000)
            }
        }
    }

    private fun initMenu(pushNotify : NotificationModel?) {
        let {
            val pagerAdapter = MarketPlaceAdapter(it,pushNotify)
            pagerMain.adapter = pagerAdapter
            pagerMain.offscreenPageLimit = 3
            pagerMain.isUserInputEnabled = false
        }

        tab_layout.removeAllTabs()
        tab_layout.addTab(tab_layout.newTab().setText(getString(R.string.phancung_phanmem)))
        tab_layout.addTab(tab_layout.newTab().setText(getString(R.string.nguyenvatlieu)))
        tab_layout.addTab(tab_layout.newTab().setText(getString(R.string.tinraovat)))
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        pagerMain.currentItem = 0
                        Utilities.sendBoardCounlyLib(baseContext, Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.BROWSER_HERMES_PRODUCT)
                    }
                    1 -> {
                        pagerMain.currentItem = 1
                        Utilities.sendBoardCounlyLib(baseContext, Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.BROWSER_SCM_PRODUCT)
                    }
                    2 -> {
                        pagerMain.currentItem = 2
                        Utilities.sendBoardCounlyLib(baseContext, Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.BROWSER_ARTICLE)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun initClick() {
        mIconLeft.setOnClickListener { onBackPressed() }
        mAddress.setOnClickListener { address() }
        mIconSearch.setOnClickListener { search() }
        mIconRight.setOnClickListener { more() }
    }

    private fun more() {
        val popup = PopupMenu(this, mIconRight)
        popup.menuInflater.inflate(R.menu.marketplace_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.navi_order -> {
                    openActivity(MarketHistoryActivity::class.java)
                }
                R.id.navi_article -> {
                    openActivity(MyArticlesActivity::class.java)
                }
                R.id.navi_feedback -> {
                    openActivity(FeedbackActivity::class.java)
                }
            }
            true
        }

        popup.show()
    }

    private fun search() {

    }

    private fun address() {
        if (mCitys.size > 0) {
            showDialogCity()
        } else {
            getLocate()
        }
    }

    private fun showDialogCity() {
        val popupMenu = PopupMenu(this, mAddress, Gravity.RIGHT)

        mCitys.forEachIndexed { index, team ->
            popupMenu.menu.add(0, index.plus(1), index.plus(1), team.city_name)
        }

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            if (mCartBussiness.getOrder().orderType == Constants.OrderType.OrderNvl) {
                let {
                    DialogUtil.showAlert(it, textTitle = R.string.thongbao, textMessage = R.string.valid_cart_order_nvl, textCancel = R.string.btn_cancel, okListener = {
                        mCartBussiness.OrderOnlineCartClear()
                        callbackCity(item)
                    })
                }
            } else {
                callbackCity(item)
            }

            false
        }
        popupMenu.show()
    }

    private fun callbackCity(item: MenuItem) {
        val cityName = item.title.toString()
        mAddress.text = cityName
        selectCodeCity(cityName)
        Utilities.sendBoard(applicationContext, Constants.BROADCAST.BROAD_NVL, Constants.BROADCAST.REFRESH)
        Utilities.sendBoard(applicationContext, Constants.BROADCAST.BROAD_ARTICLES, Constants.BROADCAST.REFRESH)
    }

    private fun selectCodeCity(title: String) {
        for (dmCityOd in mCitys) {
            if (title == dmCityOd.city_name) {
                mCartBussiness.getCartLocate().locateId = dmCityOd.id
                mCartBussiness.getCartLocate().locateName = dmCityOd.city_name
                Utilities.sendBoardLocateLib(applicationContext, Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.ADDLOCATE, dmCityOd.city_id, dmCityOd.id, dmCityOd.city_name)
            }
        }
    }

    private fun getLocate() {
        showProgressHub(this)
        val callback: ApiRequest<AddressModelData> = ApiRequest()
        callback.setCallBack(mApiSCM?.apiSCMCity(1000),
                { response -> areaDone(response.data) }) { error ->
            areaDone(null)
            error.printStackTrace()
            ToastUtil.makeText(this, getString(R.string.error_network2))
        }
    }

    private fun areaDone(data: ArrayList<AddressModel>?) {
        dismissProgressHub()
        mCitys.clear()
        if (data != null) {
            mCitys.addAll(data)
        }
    }

    private fun initData() {
        showStatusBar(statusColor = true, color = R.color.grayF8)
        mlbTitle.text = "Marketplace"
        mIconRight.visible()
        mIconSearch.invisible()

        mCartBussiness.setOder(DmOrderOnline())
        mCartBussiness.getCartLocate().run {
            mAddress.text = locateName
        }
        getLocate()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onNotice)
    }

    var onNotice: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getStringExtra("value") == BACK) {
                finish()
            }
        }
    }

}