package com.modul.marketplace.activity.marketplace

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
import com.modul.marketplace.adapter.orderonline.ServiceListRecyleAdapter
import com.modul.marketplace.app.Constants
import com.modul.marketplace.extension.DialogUtil
import com.modul.marketplace.extension.gone
import com.modul.marketplace.extension.openActivityForResult
import com.modul.marketplace.extension.visible
import com.modul.marketplace.holder.orderonline.ServicelistRecycleHolder
import com.modul.marketplace.model.marketplace.AddressModelData
import com.modul.marketplace.model.marketplace.NvlModel
import com.modul.marketplace.model.marketplace.NvlModelData
import com.modul.marketplace.model.orderonline.DmServiceListOrigin
import com.modul.marketplace.restful.ApiRequest
import com.modul.marketplace.restful.WSRestFull
import com.modul.marketplace.util.Log
import com.modul.marketplace.util.ToastUtil
import com.modul.marketplace.util.Utilities
import kotlinx.android.synthetic.main.fragment_history_order_detail.*
import kotlinx.android.synthetic.main.fragment_nvl.*
import kotlinx.android.synthetic.main.fragment_nvl.mRecyclerView
import java.util.*

class NvlFragment : BaseFragment() {
    private var mAdapter: ServiceListRecyleAdapter? = null
    private val mDatas = ArrayList<DmServiceListOrigin>()
    private val RC_DETAL_CALLBACK = 261

    private var page = 1
    private var isLoading = false
    lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_nvl, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        context?.let {
            LocalBroadcastManager.getInstance(it)
                    .registerReceiver(onNotice, IntentFilter(Constants.BROADCAST.BROAD_NVL))
        }
        initAdapter()
        initDataLoad()
        initData()
        initClick()
    }

    private fun initDataLoad() {
        layoutManager = LinearLayoutManager(context)
        mRecyclerView.layoutManager = layoutManager
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val visibleItemCount: Int = layoutManager.childCount
                    val pastVisibleItem: Int =
                            layoutManager.findFirstCompletelyVisibleItemPosition()
                    val total: Int? = mAdapter?.itemCount

                    if (!isLoading) {
                        if ((visibleItemCount + pastVisibleItem) > total!!) {
                            page += 1
                            getPage()
                        }
                    }
                }
            }
        })
    }

    private fun getPage() {
        isLoading = true
        pbLoading.visibility = View.VISIBLE
        Handler().postDelayed({
            callServiceList(page)
            isLoading = false
            pbLoading.visibility = View.GONE
        }, 500)
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
        Utilities.sendBoardCounlyLib(context, Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.BROWSER_SCM_PRODUCT)
        callServiceList(page)
    }

    private fun initAdapter() {
        mRecyclerView?.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            mAdapter = ServiceListRecyleAdapter(mActivity, mDatas, object : ServicelistRecycleHolder.OnItemClickRycle {
                override fun onClickDes(dmServiceListOrigin: DmServiceListOrigin) {
                    val bundle = Bundle()
                    bundle.putSerializable(Constants.OBJECT, dmServiceListOrigin)
                    openActivityForResult(
                            NvlDetailActivity::class.java,
                            RC_DETAL_CALLBACK,
                            bundle = bundle
                    )
                }

                override fun onPlus(dmServiceListOrigin: DmServiceListOrigin) {
                    checkOrderType {
                        var quantity = dmServiceListOrigin.quantity
                        quantity++
                        dmServiceListOrigin.quantity = quantity
                        listDetails()
                        mAdapter?.notifyDataSetChanged()
                    }
                }

                override fun onMinus(dmServiceListOrigin: DmServiceListOrigin) {
                    checkOrderType {
                        var quantity = dmServiceListOrigin.quantity
                        quantity--
                        dmServiceListOrigin.quantity = quantity
                        listDetails()
                        mAdapter?.notifyDataSetChanged()
                    }
                }

                override fun onAdd(dmServiceListOrigin: DmServiceListOrigin) {
                    checkOrderType {
                        Utilities.sendBoardCounlyLib(context, Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.ADD_SCM_PRODUCT_TO_CART)
                        dmServiceListOrigin.quantity = 1.0
                        listDetails()
                        mAdapter?.notifyDataSetChanged()
                    }
                }
            })
            mRecyclerView.adapter = mAdapter
        }
    }

    private fun callServiceList(page: Int) {
        showProgressHub(mActivity)
        val callback: ApiRequest<NvlModelData> = ApiRequest()
        callback.setCallBack(mApiSCM?.apiSCMProducts(1,mCartBussiness.getCartLocate().locateId,page,20),
                { response ->  onResponseServiceList(response.data) }) { error ->
            onResponseServiceList(null)
            error.printStackTrace()
            ToastUtil.makeText(mActivity, getString(R.string.error_network2))
        }
    }

    private fun onResponseServiceList(response: ArrayList<NvlModel>?) {
        dismissProgressHub()
        response?.forEach {
            val dmServiceListOrigin = DmServiceListOrigin()
            dmServiceListOrigin.quantity = 0.0
            it.image_urls?.let { imageUrl ->
                if (imageUrl.size > 0) {
                    imageUrl[0].url_thumb?.run {
                        dmServiceListOrigin.image = this
                    }
                }
            }
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
            mDatas.add(dmServiceListOrigin)
        }

        if (mLoi != null) {
            if (mDatas.size == 0) {
                mLoi.visible()
            } else {
                mLoi.gone()
            }
        }
        mAdapter?.notifyDataSetChanged()
    }

    private fun checkOrderType(confirm: (() -> Unit)? = null) {
        if (mCartBussiness.getOrder().orderType == Constants.OrderType.OrderOnline) {
            context?.let {
                DialogUtil.showAlert(it, textTitle = R.string.thongbao, textMessage = R.string.valid_cart_order_online, textCancel = R.string.btn_cancel, okListener = {
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

    private fun listDetails() {
        mCartBussiness.OrderOnlineConvertItemToOrigin(mDatas)
        mQuantity.text = mCartBussiness.OrderOnlineQuantity()
        refreshView()
    }

    private fun refreshView() {
        if (mCartBussiness.getOrder().originDetails.size > 0) {
            relativeLayout_cart.visibility = View.VISIBLE
            mCartBussiness.setOrderOnline(Constants.OrderType.OrderNvl)
        } else {
            relativeLayout_cart.visibility = View.GONE
            mCartBussiness.setOrderOnline("")
        }
    }

    override fun onResume() {
        super.onResume()
        if (mCartBussiness.getOrder().orderType == Constants.OrderType.OrderOnline) {
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
                    item.run {
                        mDatas.forEach {
                            if (it.productUid == productUid) {
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
        context?.let {
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
            } else {
                page = 1
                mDatas.clear()
                callServiceList(page)
            }

        }
    }

    companion object {
        fun newInstance(): NvlFragment {
            return NvlFragment()
        }
    }
}