package com.modul.marketplace.activity.marketplace

import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import com.android.volley.VolleyError
import com.modul.marketplace.R
import com.modul.marketplace.activity.BaseActivity
import com.modul.marketplace.app.ApplicationMarketPlace
import com.modul.marketplace.app.Constants
import com.modul.marketplace.app.Constants.NotifyStatus.PRODUCT
import com.modul.marketplace.app.Constants.NotifyStatus.SCM_ARTICLE
import com.modul.marketplace.extension.DialogUtil
import com.modul.marketplace.extension.StringExt
import com.modul.marketplace.extension.openActivity
import com.modul.marketplace.extension.showStatusBar
import com.modul.marketplace.model.marketplace.AddressModel
import com.modul.marketplace.model.marketplace.AddressModelData
import com.modul.marketplace.model.marketplace.NotificationModel
import com.modul.marketplace.restful.ApiRequest
import com.modul.marketplace.restful.WSRestFull
import com.modul.marketplace.util.ToastUtil
import com.modul.marketplace.util.Utilities
import kotlinx.android.synthetic.main.activity_marketplace.*
import kotlinx.android.synthetic.main.activity_select_order_address.*
import timber.log.Timber
import java.util.*

class SelectStartAddressActivity : BaseActivity() {

    private val mCitys = ArrayList<AddressModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_order_address)
        initData()
        initExtra()
        initClick()
        initExtraItem()
    }

    private fun initExtraItem() {
        val item: NotificationModel? = intent.getSerializableExtra(Constants.KEY_DATA) as NotificationModel?
        item?.run {

            if(notify_type == SCM_ARTICLE) {
                Handler().postDelayed({
                    var bundle = Bundle()
                    bundle.putString(Constants.OBJECT, partner_notify_id)
                    openActivity(ArticleDetailActivity::class.java,bundle)
                },1000)
            }else if(notify_type == PRODUCT){

            }else{

            }
        }
    }

    private fun initClick() {
        mBack.setOnClickListener { onBackPressed() }
        mStart.setOnClickListener { start() }
    }

    private fun showDialogCity() {
        var provinceList: MutableList<String> = ArrayList()
        Timber.e("mCitys: " + mCitys.size)
        mCitys.forEachIndexed { index, team ->
            provinceList.add(StringExt.isTextEmpty(team.city_name))
        }

        mSelectAddress.item = provinceList

        mSelectAddress.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
            ) {
                var cityName = provinceList[position]
                selectCodeCity(cityName)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun selectCodeCity(title: String) {
        for (dmCityOd in mCitys) {
            if (title == dmCityOd.city_name) {
                mCartBussiness.getCartLocate().locateId = dmCityOd.id
                mCartBussiness.getCartLocate().locateName = dmCityOd.city_name
                Utilities.sendBoardLocateLib(applicationContext, Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.ADDLOCATE,dmCityOd.city_id,dmCityOd.id,dmCityOd.city_name)
                break
            }
        }
    }

    private fun start() {
        if (TextUtils.isEmpty(mCartBussiness.getCartLocate().locateId)) {
            ToastUtil.makeText(this, getString(R.string.khuvuc_valid))
            return
        }
        onBackPressed()
        openActivity(MarketPlaceActivity::class.java)
    }

    private fun initExtra() {

    }

    private fun getLocate() {
        showProgressHub(this)

        val callback: ApiRequest<AddressModelData> = ApiRequest()
        callback.setCallBack(mApiSCM?.apiSCMCity(1000),
                { response ->  areaDone(response.data) }) { error ->
            areaDone(null)
            ToastUtil.makeText(this, getString(R.string.error_network2))
        }
    }

    private fun areaDone(data: ArrayList<AddressModel>?) {
        dismissProgressHub()
        mCitys.clear()
        try {
            if (data != null) {
                mCitys.addAll(data)
                showDialogCity()
            } else {
                DialogUtil.showAlert(this, textTitle = R.string.thongbao, textMessage = R.string.error_network, cancelable = false, okListener = {
                    getLocate()
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initData() {
        showStatusBar(isTranparent = true)
        getLocate()
    }
}