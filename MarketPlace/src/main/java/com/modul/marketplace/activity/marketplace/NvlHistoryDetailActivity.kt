package com.modul.marketplace.activity.marketplace

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import com.modul.marketplace.R
import com.modul.marketplace.activity.BaseActivity
import com.modul.marketplace.activity.order_online.OrderDetailFragment
import com.modul.marketplace.adapter.orderonline.OrderDetailRecyleAdapter
import com.modul.marketplace.adapter.orderonline.StatusOrderRecyleAdapter
import com.modul.marketplace.app.Constants
import com.modul.marketplace.app.Constants.BROADCAST
import com.modul.marketplace.extension.showStatusBar
import com.modul.marketplace.model.marketplace.ArticlesModelData
import com.modul.marketplace.model.marketplace.NvlOnlineModel
import com.modul.marketplace.model.marketplace.NvlOnlineModelData
import com.modul.marketplace.model.orderonline.DmService
import com.modul.marketplace.model.orderonline.DmStatusOrder
import com.modul.marketplace.restful.ApiRequest
import com.modul.marketplace.restful.WSRestFull
import com.modul.marketplace.util.FormatNumberUtil
import com.modul.marketplace.util.ToastUtil
import com.modul.marketplace.util.Utilities
import kotlinx.android.synthetic.main.fragment_orderonline_detail.*
import kotlinx.android.synthetic.main.include_header_order_detail.*
import timber.log.Timber
import java.util.*

class NvlHistoryDetailActivity : BaseActivity() {

    val TYPE_ORDER_HISTORY = "TYPE_ORDER_HISTORY"
    val TYPE_CREATE_ORDER = "TYPE_CREATE_ORDER"

    private val mListStatus = java.util.ArrayList<DmStatusOrder>()
    private var mAdapterStatus: StatusOrderRecyleAdapter? = null
    private var details = ArrayList<DmService>()
    private var mAdapter: OrderDetailRecyleAdapter? = null
    private var type = ""
    private var uId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_orderonline_detail)
        registerReceiver(onNotice, IntentFilter(BROADCAST.BROAD_MARKET_ORDER_SCM))
        initExtra()
        initAdapter()
        initData()
        initClick()
    }

    private fun initExtra() {
        type = intent?.extras?.let {
            if (it.containsKey(Constants.DATA)) {
                it.getSerializable(Constants.DATA) as String?
            } else {
                null
            }
        }.toString()

        uId = intent?.extras?.let {
            if (it.containsKey(Constants.OBJECT)) {
                it.getSerializable(Constants.OBJECT) as String?
            } else {
                null
            }
        }.toString()

        uId.run{
            api(this)
        }
    }

    private fun api(uId: String) {
        mSwipRefreshLayout?.apply {
            isRefreshing = true
        }
        val callback: ApiRequest<NvlOnlineModelData> = ApiRequest()
        callback.setCallBack(mApiSCM?.apiSCMInvoicesHistoryDetail(mCartBussiness.companyId, uId),
                { response ->  apiDone(response.data) }) { error ->
            apiDone(null)
            error.printStackTrace()
            ToastUtil.makeText(this, getString(R.string.error_network2))
        }
    }

    private fun apiDone(data: NvlOnlineModel?) {
        mSwipRefreshLayout?.apply {
            isRefreshing = false
        }
        data?.run{
            loadDataStatus(this)
            header_text.text = resources.getString(R.string.don_hang) + " #" + invoice_id
            mPaymentmethod.text = getString(R.string.cod_e_wallet)
            mPaymentmethod.isEnabled = false
            mPaymentmethod.setTextColor(ContextCompat.getColor(this@NvlHistoryDetailActivity, R.color.gray80))
            mPaymentmethod.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_payment_unselect, 0)

            var position = 0
            for (i in mListStatus.indices) {
//                var textStatus = ""
//                if(status.equals(Constants.OrderNvlStatus.CANCELED)){
//                    textStatus = getString(R.string.da_huy)
//                }else if (status.equals(Constants.OrderNvlStatus.COMPLETED)) {
//                    textStatus = getString(R.string.hoan_thanh)
//                }else if (status.equals(Constants.OrderNvlStatus.PENDING)) {
//                    textStatus = getString(R.string.cho_xy_ly)
//                }else if (status.equals(Constants.OrderNvlStatus.CONFIRMED)) {
//                    textStatus = getString(R.string.da_xac_nhan)
//                }

                if (status.equals(mListStatus[i].type)) {
                    position = i
                    mListStatus[i].isCorectPosition = true
                } else {
                    mListStatus[i].isCorectPosition = false
                }
            }

            for (i in 0..position) {
                mListStatus[i].isCheckedStatus = true
            }
            mAdapterStatus?.notifyDataSetChanged()

            provisional.text = voucherAmount?.let { original_amount?.plus(it) }?.let { FormatNumberUtil.formatCurrency(it) }
            voucher_amount.text = "-" + voucherAmount?.let { FormatNumberUtil.formatCurrency(it) }
            total_amount.text = final_amount?.let { FormatNumberUtil.formatCurrency(it) }

            details.clear()
            invoice_details.forEach {
                it.product?.run{
                    var dmService = DmService()
                    dmService.serviceName = name
                    price_sale?.run{
                        dmService.orgPrice = this
                    }
                    it.quantity?.run{
                        dmService.quantity = this.toDouble()
                    }
                    dmService.amount = dmService.orgPrice * dmService.quantity
                    details.add(dmService)
                }
            }
            mAdapter?.notifyDataSetChanged()

        }
    }

    private fun initAdapter() {
        list_status?.apply {
            layoutManager = LinearLayoutManager(this@NvlHistoryDetailActivity, RecyclerView.VERTICAL, false)
            mAdapterStatus = StatusOrderRecyleAdapter(this@NvlHistoryDetailActivity, mListStatus)
            adapter = mAdapterStatus
        }
        list_detail?.apply {
            layoutManager = LinearLayoutManager(this@NvlHistoryDetailActivity, RecyclerView.VERTICAL, false)
            mAdapter = OrderDetailRecyleAdapter(this@NvlHistoryDetailActivity, details)
            adapter = mAdapter
        }
    }

    private fun loadDataStatus(data: NvlOnlineModel?) {
        mListStatus.clear()
        mListStatus.add(DmStatusOrder(Constants.OrderNvlStatus.PENDING, getString(R.string.cho_xy_ly), true, true, false, true))
        if(data?.status.equals(Constants.OrderNvlStatus.CANCELED)){
            mListStatus.add(DmStatusOrder(Constants.OrderNvlStatus.CANCELED, getString(R.string.da_huy), false, false, true, false))
        }else{
            mListStatus.add(DmStatusOrder(Constants.OrderNvlStatus.CONFIRMED, getString(R.string.da_xac_nhan), false, false, false, false))
            mListStatus.add(DmStatusOrder(Constants.OrderNvlStatus.COMPLETED, getString(R.string.hoan_thanh), false, false, true, false))
        }
    }

    private fun initClick() {
        back.setOnClickListener {onBackPressed()}
        mSwipRefreshLayout.setOnRefreshListener {
            uId?.run{
                api(this)
            }
        }
    }

    override fun onBackPressed() {
        if (OrderDetailFragment.TYPE_CREATE_ORDER == type) {
            mCartBussiness.OrderOnlineCartClear()
            Utilities.sendBoard(this, BROADCAST.BROAD_MAKETPLACE, BROADCAST.BACK)
            Utilities.sendBoard(this, BROADCAST.BROAD_CART, BROADCAST.BACK)
            Utilities.sendBoard(this, BROADCAST.BROAD_INFOMATION, BROADCAST.BACK)
            val intent = Intent(this@NvlHistoryDetailActivity, MarketPlaceActivity::class.java)
            startActivity(intent)
//            Utilities.sendBoardLib(baseContext, BROADCAST.BROAD_MANAGER_HOME_CALLBACK, BROADCAST.NVL_ORDER_CALLBACK)
            finish()
        } else {
            super.onBackPressed()
        }
    }

    private fun initData() {
        showStatusBar(color = R.color.grayF8, statusColor = true)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onNotice)
    }

    var onNotice: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Timber.e("Nvl history detail refresh")
            uId?.run{
                api(this)
            }
        }
    }
}