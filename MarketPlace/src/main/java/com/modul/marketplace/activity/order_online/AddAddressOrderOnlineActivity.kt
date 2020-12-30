package com.modul.marketplace.activity.order_online

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.Gravity
import android.view.MenuItem
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.modul.marketplace.R
import com.modul.marketplace.activity.BaseActivity
import com.modul.marketplace.app.Constants
import com.modul.marketplace.extension.StringExt
import com.modul.marketplace.extension.gone
import com.modul.marketplace.extension.showStatusBar
import com.modul.marketplace.model.orderonline.*
import com.modul.marketplace.paser.orderonline.RestAllDmLocate
import com.modul.marketplace.restful.ApiRequest
import com.modul.marketplace.util.ToastUtil
import com.modul.marketplace.util.Utilities
import kotlinx.android.synthetic.main.activity_add_address.*
import kotlinx.android.synthetic.main.include_header2.*
import java.util.*

class AddAddressOrderOnlineActivity : BaseActivity() {

    private val mCitys = ArrayList<DmCityOd>()
    private val mDistricts = ArrayList<DmDistrict>()
    private var cityCode = ""
    private var districtCode = ""
    private var phuongxa = ""
    private var lat = 0.0
    private var lng = 0.0
    private var suggest: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)
        initData()
        initClick()
    }

    private fun initClick() {
        mIconLeft.setOnClickListener { onBackPressed() }
        mCancel.setOnClickListener { onBackPressed() }
        mCity.setOnClickListener { city() }
        mDistrict.setOnClickListener { district() }
        mPhuongXa.setOnClickListener { phuongxa() }
        mApply.setOnClickListener { save() }
    }

    private fun save() {
        var adressdl = mAddress.text.toString()

        if (TextUtils.isEmpty(adressdl)) {
            ToastUtil.makeText(this, getString(R.string.mess_delivery_info))
            return
        }
        if (TextUtils.isEmpty(cityCode)) {
            ToastUtil.makeText(this, getString(R.string.mess_delivery_info))
            return
        }
        if (TextUtils.isEmpty(districtCode)) {
            ToastUtil.makeText(this, getString(R.string.mess_delivery_info))
            return
        }
//        if (TextUtils.isEmpty(phuongxa)) {
//            ToastUtil.makeText(this, getString(R.string.mess_delivery_info))
//            return
//        }

        val dmDeliveryInfo = DmDeliveryInfo()
        dmDeliveryInfo.address = adressdl
        dmDeliveryInfo.city = cityCode
        dmDeliveryInfo.cityName = mCity.text.toString()
        dmDeliveryInfo.district = districtCode
        dmDeliveryInfo.districtName = mDistrict.text.toString()
        dmDeliveryInfo.lat = lat
        dmDeliveryInfo.lng = lng

        mCartBussiness.getOrder().dmDeliveryInfo = dmDeliveryInfo

        val i = Intent()
        setResult(RESULT_OK, i)
        onBackPressed()
    }


    private fun city() {
        if (mCitys.size > 0) {
            showDialogCity()
        } else {
            getLocate()
        }
    }

    private fun district() {
        showDialogDistrict()
    }

    private fun phuongxa() {

    }

    private fun showDialogCity() {
        var popupMenu: PopupMenu? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            popupMenu = PopupMenu(this, mCity, Gravity.END)
        }
        for (dmCityOd in mCitys) {
            popupMenu!!.menu.add(dmCityOd.name)
        }
        popupMenu!!.setOnMenuItemClickListener { item: MenuItem ->
            val cityName = item.title.toString()
            mCity.setText(cityName)
            selectCodeCity(cityName)
            false
        }
        popupMenu.show()
    }

    private fun selectCodeCity(title: String) {
        for (dmCityOd in mCitys) {
            if (title == dmCityOd.name) {
                cityCode = dmCityOd.code
                mDistricts.clear()
                mDistricts.addAll(dmCityOd.dmDistricts)
                mDistrict.setText("")
                districtCode = ""
                break
            }
        }
    }

    private fun showDialogDistrict() {
        var popupMenu: PopupMenu? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            popupMenu = PopupMenu(this, mDistrict, Gravity.END)
        }
        for (dmDistrict in mDistricts) {
            popupMenu!!.menu.add(dmDistrict.name)
        }
        popupMenu!!.setOnMenuItemClickListener { item: MenuItem ->
            val cityName = item.title.toString()
            mDistrict.setText(cityName)
            selectCodeDistrict(cityName)
            false
        }
        popupMenu.show()
    }

    private fun selectCodeDistrict(title: String) {
        for (dmDistrict in mDistricts) {
            if (title == dmDistrict.name) {
                districtCode = dmDistrict.code
                break
            }
        }
    }

    private fun GPS() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                Utilities.showMessageOKCancel(this, getString(R.string.mess_acces_permission_location)
                ) { dialog: DialogInterface?, which: Int ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
            } else {
                ActivityCompat.requestPermissions(this, Utilities.PERMISSIONS_LOCATION, 1)
            }
        } else {
            val i = Intent(this, MapActivity::class.java)
            startActivityForResult(i, InformationFragment.REQUEST_CODE_PICK_LOCATION)
        }
    }

    private fun getLocate() {
        showProgressHub(this)

        val callback: ApiRequest<RestAllDmLocate> = ApiRequest()
        callback.setCallBack(mApiHermes?.apiLocate(),
                { response ->  dismissProgressHub()
                    onresponseLocate(response.datas) }) { error ->
            dismissProgressHub()
            onresponseLocate(null)
            error.printStackTrace()
            ToastUtil.makeText(this, getString(R.string.error_network2))
        }
    }

    private fun onresponseLocate(response: ArrayList<DmLocate>?) {
        if (response != null) {
            mCitys.clear()
            for (dmLocate in response) {
                mCitys.addAll(dmLocate.dmCities)
            }
        }
    }


    private fun initData() {
        showStatusBar(color = R.color.white, statusColor = true)
        include2.background.setTint(ContextCompat.getColor(this, R.color.white))
        mlbTitle.text = getString(R.string.edit_address)
        getLocate()
        mCartBussiness.getOrder().dmDeliveryInfo?.run {
            mAddress.setText(StringExt.isTextEmpty(address))
            mCity.setText(StringExt.isTextEmpty(cityName))
            mDistrict.setText(StringExt.isTextEmpty(districtName))
        }
        mTIPPhuong.gone()


//        mAddress.addTextChangedListener(object : TextWatcher {
//
//            val handler = Handler(Looper.getMainLooper())
//            var workRunnable: Runnable = Runnable { }
//
//            override fun onTextChanged(
//                    s: CharSequence,
//                    start: Int,
//                    before: Int,
//                    count: Int
//            ) {
//
//            }
//
//            override fun beforeTextChanged(
//                    s: CharSequence,
//                    start: Int,
//                    count: Int,
//                    after: Int
//            ) {
//            }
//
//            override fun afterTextChanged(s: Editable) {
//                handler.removeCallbacks(workRunnable)
//                workRunnable = Runnable {
//                    apiSearch(s.toString())
//                }
//                handler.postDelayed(workRunnable, 1000)
//            }
//        })
    }

//    private fun apiSearch(value: String) {
//        val callback: ApiRequest<AhamoveSearchData> = ApiRequest()
//        callback.setCallBack(mApiSCM?.apiAhamoveSearchLocation(value),
//                { response ->  response?.run {
//                    searchCallbacK(this)
//                } }) { error ->
//            error.printStackTrace()
//            searchCallbacK(null)
//        }
//    }

//    private fun searchCallbacK(data: AhamoveSearchData?) {
//        mLoading.gone()
//        suggest.clear()
//        data?.run {
//            suggest = features?.map {
//                it.properties?.name.toString()
//            } as MutableList<String>
//
//            var adapter =
//                    ArrayAdapter(this@AddAddressOrderOnlineActivity, android.R.layout.simple_list_item_1, suggest)
//            mAddress.threshold = 1
//            mAddress.setAdapter(adapter)
//            mAddress.showDropDown()
//            mAddress.onItemClickListener = object : AdapterView.OnItemClickListener {
//                override fun onItemClick(
//                        parent: AdapterView<*>?,
//                        view: View?,
//                        position: Int,
//                        id: Long
//                ) {
//                    var name = adapter.getItem(position)
//                    name?.run {
//                        var featurers = getFeaturesByName(toString())
//                        //Timber.e("geometry: " + featurers?.geometry?.toJson())
//                        //Timber.e("properties: " + featurers?.properties?.toJson())
//                        var getlng = featurers?.geometry?.coordinates?.get(0).toString().toDouble()
//                        var getlat = featurers?.geometry?.coordinates?.get(1).toString().toDouble()
//
//                        lat = getlat
//                        lng = getlng
//                        mAddress.setText(toString())
//                        var latLng = LatLng(lat, lng)
//                        //Timber.e("latLng:"+latLng)
////                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
//                    }
//                }
//            }
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == InformationFragment.REQUEST_CODE_PICK_LOCATION) {
            if (resultCode == RESULT_OK) {
                val dmLocation = data!!.getSerializableExtra(Constants.KEY_DATA) as DmLocation
                val ad = dmLocation.address
                lat = dmLocation.latitude
                lng = dmLocation.longitude
                mAddress.setText(ad)
            }
        }
    }
}