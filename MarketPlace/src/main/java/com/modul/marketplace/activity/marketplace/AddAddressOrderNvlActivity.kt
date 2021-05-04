package com.modul.marketplace.activity.marketplace

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.VolleyError
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.modul.marketplace.R
import com.modul.marketplace.activity.BaseActivity
import com.modul.marketplace.activity.order_online.InformationFragment
import com.modul.marketplace.activity.order_online.MapActivity
import com.modul.marketplace.app.Constants
import com.modul.marketplace.extension.StringExt
import com.modul.marketplace.extension.gone
import com.modul.marketplace.extension.showStatusBar
import com.modul.marketplace.extension.showToast
import com.modul.marketplace.model.marketplace.*
import com.modul.marketplace.model.orderonline.DmDeliveryInfo
import com.modul.marketplace.model.orderonline.DmLocation
import com.modul.marketplace.restful.ApiRequest
import com.modul.marketplace.restful.WSRestFull
import com.modul.marketplace.util.Log
import com.modul.marketplace.util.ToastUtil
import com.modul.marketplace.util.Utilities
import kotlinx.android.synthetic.main.activity_add_address.*
import kotlinx.android.synthetic.main.include_header2.*
import java.util.*

class AddAddressOrderNvlActivity : BaseActivity() {

    private var cityCode = ""
    private var cityFbId = ""
    private var districtFbId = ""
    private var pricintCode = ""
    private var districtCode = ""
    private var lat = 0.0
    private var lng = 0.0
    private var localModel : LocationModel? = null
    private var suggest: MutableList<String> = ArrayList()
    private val AUTOCOMPLETE_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)
        initExtra()
        initData()
        initClick()
    }

    private fun initExtra() {
        localModel = intent?.extras?.let {
            if (it.containsKey(Constants.OBJECT)) {
                it.getSerializable(Constants.OBJECT) as LocationModel?
            } else {
                null
            }
        }


        localModel?.run{
            city_uid?.run{
                cityCode = this
            }
            city?.run{
                fb_id?.run{
                    cityFbId = this
                }
            }
            district_uid?.run{
                districtCode = this
            }
            district?.run{
                fb_id?.run{
                    districtFbId = this
                }
            }
            precinct_uid?.run{
                pricintCode = this
            }
            mAddress.setText(StringExt.isTextEmpty(location_name))
            mCity.setText(StringExt.isTextEmpty(city?.city_name))
            mDistrict.setText(StringExt.isTextEmpty(district?.district_name))
            mPhuongXa.setText(StringExt.isTextEmpty(precinct?.precinct_name))
            mlbTitle.text = getString(R.string.edit_address)
        }?:run{
            mlbTitle.text = getString(R.string.add_address)
        }
    }

    private fun initClick() {
        mIconLeft.setOnClickListener { onBackPressed() }
        mCancel.setOnClickListener { onBackPressed() }
        mCity.setOnClickListener { choiceCity() }
        mDistrict.setOnClickListener { choiceDistric() }
        mPhuongXa.setOnClickListener { choicePrecinct() }
        mApply.setOnClickListener { save() }
        mAddress.setOnClickListener { openGoogleMap() }
    }

    private fun openGoogleMap() {
        val fields = listOf(Place.Field.ID, Place.Field.NAME)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }


    private fun choiceCity() {
        showProgressHub(this)
        val callback: ApiRequest<AddressModelData> = ApiRequest()
        callback.setCallBack(mApiSCM?.apiSCMCity(1000),
                { response ->  areaDone(response.data) }) { error ->
            areaDone(null)
            error.printStackTrace()
            ToastUtil.makeText(this, getString(R.string.error_network2))
        }
    }

    private fun areaDone(data: ArrayList<AddressModel>?) {
        dismissProgressHub()
        data?.run {
            val popupMenu = PopupMenu(this@AddAddressOrderNvlActivity, mCity, Gravity.RIGHT)

            forEachIndexed { index, team ->
                popupMenu.menu.add(0, index.plus(1), index.plus(1), team.city_name)
            }

            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                val cityName = item.title.toString()
                mCity.setText(cityName)
                data[item.itemId.minus(1)].id?.run{
                    cityCode = this
                }
                data[item.itemId.minus(1)].fb_id?.run{
                    cityFbId = this
                }
                false
            }
            popupMenu.show()
        }
    }

    private fun choicePrecinct() {
        if (districtCode == "") {
            showToast(getString(R.string.district_valid))
            return
        }
        showProgressHub(this)
        var addressModel = AddressModel()
        addressModel.city_id = cityCode
        addressModel.district_id = districtCode
        val callback: ApiRequest<AddressModelData> = ApiRequest()
        callback.setCallBack(mApiSCM?.apiSCMPrecincts(addressModel.city_id,addressModel.district_id),
                { response ->  precinctDone(response.data) }) { error ->
            precinctDone(null)
            error.printStackTrace()
            ToastUtil.makeText(this, getString(R.string.error_network2))
        }
    }

    private fun precinctDone(data: ArrayList<AddressModel>?) {
        dismissProgressHub()
        data?.run {
            val popupMenu = PopupMenu(this@AddAddressOrderNvlActivity, mPhuongXa, Gravity.RIGHT)

            forEachIndexed { index, team ->
                popupMenu.menu.add(0, index.plus(1), index.plus(1), team.precinct_name)
            }

            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                val cityName = item.title.toString()
                mPhuongXa.setText(cityName)
                pricintCode = data[item.itemId.minus(1)].id.toString()
                false
            }
            popupMenu.show()
        }
    }

    private fun choiceDistric() {
        if (cityCode == "") {
            showToast(getString(R.string.city_valid))
            return
        }
        showProgressHub(this)
        var addressModel = AddressModel()
        addressModel.city_id = cityCode
        val callback: ApiRequest<AddressModelData> = ApiRequest()
        callback.setCallBack(mApiSCM?.apiSCMDistricts(addressModel.city_id),
                { response ->  districDone(response.data) }) { error ->
            districDone(null)
            error.printStackTrace()
            ToastUtil.makeText(this, getString(R.string.error_network2))
        }
    }

    private fun districDone(data: ArrayList<AddressModel>?) {
        dismissProgressHub()
        data?.run {
            val popupMenu = PopupMenu(this@AddAddressOrderNvlActivity, mDistrict, Gravity.RIGHT)

            forEachIndexed { index, team ->
                popupMenu.menu.add(0, index.plus(1), index.plus(1), team.district_name)
            }

            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                val cityName = item.title.toString()
                mDistrict.setText(cityName)
                data[item.itemId.minus(1)].id?.run{
                    districtCode = this
                }
                data[item.itemId.minus(1)].fb_id?.run{
                    districtFbId = this
                }
                false
            }
            popupMenu.show()
        }
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
        if (TextUtils.isEmpty(pricintCode)) {
            ToastUtil.makeText(this, getString(R.string.mess_delivery_info))
            return
        }

        val dmDeliveryInfo = DmDeliveryInfo()
        dmDeliveryInfo.address = adressdl
        dmDeliveryInfo.city = cityFbId
        dmDeliveryInfo.district = districtFbId
        dmDeliveryInfo.lat = lat
        dmDeliveryInfo.lng = lng

        mCartBussiness.getOrder().dmDeliveryInfo = dmDeliveryInfo

        var locateModel = LocationModel()
        locateModel.selected = null
        locateModel.location_name = adressdl
        locateModel.company_id = mCartBussiness.companyId
        locateModel.user_id = mCartBussiness.userId
        locateModel.precinct_uid = pricintCode
        locateModel.district_uid = districtCode
        locateModel.city_uid = cityCode
        locateModel.country_uid = mCartBussiness.country_uid
//        locateModel.country_uid = "88a736a3-747c-4ed8-8426-cfe995691938" //Tést
//        locateModel.country_uid = "1136b905-35b7-4bb0-bdfc-7ab6ce8b1db3" //that

        showProgressHub(this)
        localModel?.run{
            locateModel.id = id
            apiEdit(locateModel)
        }?:run{
            apiCreate(locateModel)
        }
    }

    private fun apiCreate(locateModel: LocationModel){
        val callback: ApiRequest<LocationModelDataObject> = ApiRequest()
        callback.setCallBack(mApiSCM?.apiSCMLocationCreate(locateModel),
                { response ->  createDone(response.data) }) { error ->
            createDone(null)
            error.printStackTrace()
            ToastUtil.makeText(this, getString(R.string.error_network2))
        }
    }

    private fun apiEdit(locateModel: LocationModel){
        val callback: ApiRequest<LocationModelDataObject> = ApiRequest()
        callback.setCallBack(mApiSCM?.apiSCMLocationEdit(locateModel.toJson()),
                { response ->  createDone(response.data) }) { error ->
            createDone(null)
            error.printStackTrace()
            ToastUtil.makeText(this, getString(R.string.error_network2))
        }
    }

    private fun createDone(data: LocationModel?) {
        dismissProgressHub()
        data?.run {
            val i = Intent()
            setResult(RESULT_OK, i)
            onBackPressed()
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

    private fun initData() {
        showStatusBar(color = R.color.white, statusColor = true)
        include2.background.setTint(ContextCompat.getColor(this, R.color.white))

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
//                    ArrayAdapter(this@AddAddressOrderNvlActivity, android.R.layout.simple_list_item_1, suggest)
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
//                        mAddress.clearFocus()
//                        hidenKeyboard()
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
        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i(TAG, "Place: ${place.name}, ${place.id}")
                        place?.run{
                            latLng?.run{
                                lat = latitude
                                lng = longitude
                            }
                            mAddress.setText(name)
                        }
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i(TAG, status.statusMessage)
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
    }
}