package com.modul.marketplace.activity.order_online

import android.app.Activity
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
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.modul.marketplace.R
import com.modul.marketplace.activity.BaseFragment
import com.modul.marketplace.activity.CateActivity
import com.modul.marketplace.activity.marketplace.ArticleCreateActivity
import com.modul.marketplace.activity.marketplace.ArticleDetailActivity
import com.modul.marketplace.adapter.orderonline.ServiceListRecyleAdapter
import com.modul.marketplace.app.ApplicationMarketPlace
import com.modul.marketplace.app.Constants
import com.modul.marketplace.extension.*
import com.modul.marketplace.holder.orderonline.ServicelistRecycleHolder
import com.modul.marketplace.model.marketplace.AddressModelData
import com.modul.marketplace.model.marketplace.NotificationModel
import com.modul.marketplace.model.orderonline.DmServiceListOrigin
import com.modul.marketplace.paser.orderonline.RestAllDmServiceListOrigin
import com.modul.marketplace.restful.ApiRequest
import com.modul.marketplace.restful.WSRestFull
import com.modul.marketplace.util.Log
import com.modul.marketplace.util.ToastUtil
import com.modul.marketplace.util.Utilities
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import kotlinx.android.synthetic.main.activity_marketplace.*
import kotlinx.android.synthetic.main.fragment_nvl.*
import timber.log.Timber
import java.util.*

class PurchaseFragment : BaseFragment() {

    private var mAdapter: ServiceListRecyleAdapter? = null
    private val mDatas = ArrayList<DmServiceListOrigin>()
    private val RC_DETAL_CALLBACK = 261
    private var idItemInPush : String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_purchase, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        context?.let{
            LocalBroadcastManager.getInstance(it)
                    .registerReceiver(onNotice, IntentFilter(Constants.BROADCAST.BROAD_PURCHASE))
        }
        initAdapter()
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
            if (notify_type == Constants.NotifyStatus.PRODUCT) {
                notify_detail?.run {
                    product_type?.run {
                        if (this == Constants.Product.HERMES) {
                            id?.run {
                                idItemInPush = this
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initClick() {
        relativeLayout_cart.setOnClickListener { view: View? ->
            if (TextUtils.isEmpty(mCartBussiness.companyId)) {
                context?.let {
                    DialogUtil.showAlert(it, textTitle = R.string.thongbao, textMessage = R.string.card_companyId_valid, textCancel = R.string.desau, textOk = R.string.lien_he, okListener = {
                        Utilities.callPhone(activity,"19004766")
                    })
                }
            } else {
                CateActivity.gotoCartFragment(mActivity)
            }
        }
    }

    private fun initData() {
        Utilities.sendBoardCounlyLib(context, Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.BROWSER_HERMES_PRODUCT)
        callServiceList()
    }

    private fun initAdapter() {
        mRecyclerView?.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            mAdapter = ServiceListRecyleAdapter(mActivity, mDatas, object : ServicelistRecycleHolder.OnItemClickRycle {
                override fun onClickDes(dmServiceListOrigin: DmServiceListOrigin) {
                    val bundle = Bundle()
                    bundle.putSerializable(Constants.OBJECT, dmServiceListOrigin)
                    openActivityForResult(
                            PurchaseDetailActivity::class.java,
                            RC_DETAL_CALLBACK,
                            bundle = bundle
                    )
                }

                override fun onPlus(dmServiceListOrigin: DmServiceListOrigin) {
                    checkOrderType {
                        var quantity = dmServiceListOrigin.quantity
                        quantity++
                        if (DmServiceListOrigin.TYPE_SUB == dmServiceListOrigin.type) {
                            if (quantity >= dmServiceListOrigin.maxChoice) {
                                quantity = dmServiceListOrigin.maxChoice
                            }
                        }
                        dmServiceListOrigin.quantity = quantity
                        listDetails()
                        mAdapter?.notifyDataSetChanged()
                    }
                }

                override fun onMinus(dmServiceListOrigin: DmServiceListOrigin) {
                    checkOrderType {
                        var quantity = dmServiceListOrigin.quantity
                        quantity--
                        if (DmServiceListOrigin.TYPE_SUB == dmServiceListOrigin.type) {
                            if (quantity <= dmServiceListOrigin.maxChoice) {
                                quantity = 0.0
                            }
                        }
                        dmServiceListOrigin.quantity = quantity
                        listDetails()
                        mAdapter?.notifyDataSetChanged()
                    }
                }

                override fun onAdd(dmServiceListOrigin: DmServiceListOrigin) {
                    checkOrderType {
                        Utilities.sendBoardCounlyLib(context, Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.ADD_HERMES_PRODUCT_TO_CART)
                        if (DmServiceListOrigin.TYPE_SUB == dmServiceListOrigin.type) {
                            dmServiceListOrigin.quantity = dmServiceListOrigin.minChoice
                        } else {
                            dmServiceListOrigin.quantity = 1.0
                        }
                        listDetails()
                        mAdapter?.notifyDataSetChanged()
                    }
                }
            })
            val headersDecor = StickyRecyclerHeadersDecoration(mAdapter)
            mRecyclerView.addItemDecoration(headersDecor)
            mRecyclerView.adapter = mAdapter
        }
    }

    private fun checkOrderType(confirm: (() -> Unit)? = null) {
        if (mCartBussiness.getOrder().orderType == Constants.OrderType.OrderNvl) {
            context?.let {
                DialogUtil.showAlert(it, textTitle = R.string.thongbao, textMessage = R.string.valid_cart_order_nvl, textCancel = R.string.btn_cancel, okListener = {
                    clearData()
                    confirm?.let { it1 -> it1() }
                })
            }
        } else {
            confirm?.let { it1 -> it1() }
        }
    }

    private fun clearData() {
        mCartBussiness.OrderOnlineCartClear()
        mAdapter?.notifyDataSetChanged()
    }

    private fun callServiceList() {
        showProgressHub(mActivity)
        var productType = ""
        if (mCartBussiness.appType == Constants.FABI) {
            productType = Constants.FABI
        } else {
            productType = Constants.POSPC
        }

        val callback: ApiRequest<RestAllDmServiceListOrigin> = ApiRequest()
        callback.setCallBack(mApiHermes?.apiOrderOnline_ServiceList(productType),
                { response ->  onResponseServiceList(response.data) }) { error ->
            onResponseServiceList(null)
            ToastUtil.makeText(mActivity, getString(R.string.error_network2))
        }
    }

    private fun onResponseServiceList(response: ArrayList<DmServiceListOrigin>?) {
        dismissProgressHub()
        mDatas.clear()
        if (response != null) {
            val sortedList = response.sortedWith(compareBy { it.type })
            sortedList.forEach {
                it.details?.run{
                    forEach{ sub ->
                        sub.baseQuantity = sub.quantity
                    }
                }
            }
            mDatas.addAll(sortedList)
        }
        checkPushOpenDetail()
        if (mLoi != null) {
            if (mDatas.size == 0) {
                mLoi.visible()
            } else {
                mLoi.gone()
            }
        }

        mAdapter?.notifyDataSetChanged()
    }

    private fun checkPushOpenDetail(){
        idItemInPush?.run{
            mDatas.forEach {
                if (it.uId.equals(this)) {
                    Handler().postDelayed({
                        val bundle = Bundle()
                        bundle.putSerializable(Constants.OBJECT, it)
                        openActivityForResult(PurchaseDetailActivity::class.java, RC_DETAL_CALLBACK,bundle = bundle)
                    }, 500)
                }
            }
        }
        idItemInPush = null
    }

    private fun listDetails() {
        mCartBussiness.OrderOnlineConvertItemToOrigin(mDatas)
        mQuantity.text = mCartBussiness.OrderOnlineQuantity()
        refreshView()
    }

    private fun refreshView(){
        if (mCartBussiness.getOrder().originDetails.size > 0) {
            relativeLayout_cart.visibility = View.VISIBLE
            mCartBussiness.setOrderOnline(Constants.OrderType.OrderOnline)
        } else {
            relativeLayout_cart.visibility = View.GONE
            mCartBussiness.setOrderOnline("")
        }
    }

    override fun onResume() {
        super.onResume()
        if (mCartBussiness.getOrder().orderType == Constants.OrderType.OrderNvl) {
            mDatas.forEach {
                it.quantity = 0.0
            }
            mAdapter?.notifyDataSetChanged()
            mQuantity.text = mCartBussiness.OrderOnlineQuantity()
            relativeLayout_cart.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_DETAL_CALLBACK && resultCode == Activity.RESULT_OK) {
            data?.run {
                getSerializableExtra(Constants.OBJECT)?.let {
                    val item = it as DmServiceListOrigin
                    Log.e("datata: ", "aaa: " + Gson().toJson(item))
                    item.run {
                        mDatas.forEach {
                            if (it.code == code) {
                                checkOrderType {
                                    it.quantity = item.quantity
                                    listDetails()
                                    mAdapter?.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        context?.let{
            LocalBroadcastManager.getInstance(it).unregisterReceiver(onNotice)
        }
    }

    var onNotice: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getStringExtra("value") == Constants.BROADCAST.CHANGE_ITEM) {
                var id = intent.getStringExtra("id")
                var quantity = intent.getDoubleExtra("quantity",0.0)
                mDatas.forEach { menu ->
                    if(menu.code.equals(id)){
                        menu.quantity = quantity
                    }
                }
                refreshView()
                mAdapter?.notifyDataSetChanged()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(pushNotify : NotificationModel?): PurchaseFragment {
            val args = Bundle()
            args.putSerializable("pushNotify", pushNotify)
            var f = PurchaseFragment()
            f.arguments = args
            return f
        }
    }
}