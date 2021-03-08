package com.modul.marketplace.bussiness

import com.google.gson.Gson
import com.modul.marketplace.activity.marketplace.MarketPlaceActivity
import com.modul.marketplace.app.ApplicationMarketPlace
import com.modul.marketplace.app.Constants
import com.modul.marketplace.extension.StringExt
import com.modul.marketplace.model.marketplace.CartLocateModel
import com.modul.marketplace.model.marketplace.NvlModel
import com.modul.marketplace.model.marketplace.NvlOnlineModel
import com.modul.marketplace.model.orderonline.*
import com.modul.marketplace.util.ConverUtil
import com.modul.marketplace.util.Log
import com.modul.marketplace.util.Utilities

class CartBussiness {
    private var mOrderModel = DmOrderOnline()
    private var dmCartLocate = CartLocateModel()
    var mListBrands = ArrayList<DmBrand>()

    //Adding Data
    var appType = ""
    var companyId = ""
    var userId = ""
    var country_uid = ""

    fun getListBrandId(): String {
        var content = ""
        mListBrands.forEach {
            content = content + it.brandId + ","
        }
        if(content.endsWith(","))
        {
            content = content.substring(0,content.length - 1);
        }
        return content
    }

    fun addBrand(data: ArrayList<DmBrand>) {
        mListBrands.clear()
        data?.run {
            mListBrands.addAll(this)
        }
    }

    //Order Online
    fun getOrder(): DmOrderOnline {
        return mOrderModel
    }

    fun setOder(order: DmOrderOnline) {
        mOrderModel = DmOrderOnline()
        mOrderModel = order
    }

    fun convertOrderToJson(): DmOrderOnline {
        var orderOnline = DmOrderOnline()

        orderOnline.isHaveAutoPromotion = null
        orderOnline.originDetails = null
        orderOnline.companyId = companyId
        orderOnline.contactCompany = mOrderModel.contactCompany

        orderOnline.amount = mOrderModel.amount
        orderOnline.discountAmount = mOrderModel.discountAmount

        orderOnline.customerName = mOrderModel.customerName
        orderOnline.customerNote = mOrderModel.customerNote
        orderOnline.companyTaxCode = mOrderModel.companyTaxCode
        orderOnline.companyTaxEmail = mOrderModel.companyTaxEmail
        orderOnline.companyFullAddress = mOrderModel.companyFullAddress

        orderOnline.requestInvoice = mOrderModel.requestInvoice
        orderOnline.dmVoucher = mOrderModel.dmVoucher

        orderOnline.storeId = mOrderModel.storeId
        orderOnline.storeName = mOrderModel.storeName
        orderOnline.brandId = mOrderModel.brandId
        orderOnline.dmDeliveryInfo = mOrderModel.dmDeliveryInfo

        if(appType == Constants.FABI){
            orderOnline.productCode = Constants.FABI
            orderOnline.contactEmail = userId
        }else{
            orderOnline.productCode = Constants.POSPC
            orderOnline.contactName = userId
        }

        orderOnline.details.addAll(mOrderModel.details)
        Log.e("json", "data: " + Gson().toJson(orderOnline))
        Utilities.sendBoardCounlyLib(ApplicationMarketPlace.context, Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.DATA_ORDER, Gson().toJson(orderOnline))
        return orderOnline
    }

    fun convertNvlToJson(): NvlOnlineModel {
        var nvlOnline = NvlOnlineModel()

        nvlOnline.address = mOrderModel.dmDeliveryInfo.address
        nvlOnline.original_amount = mOrderModel.amount
        mOrderModel.discountAmount?.run {
            nvlOnline.discount_amount = this
        }
        mOrderModel.dmVoucher?.voucherCode?.run {
            nvlOnline.voucher_code = this
        }
        mOrderModel.dmVoucher?.usedDiscountAmount?.run {
            nvlOnline.voucherAmount = this
        }
        nvlOnline.final_amount = mOrderModel.amount
        nvlOnline.is_red_invoice = mOrderModel.requestInvoice

        nvlOnline.customer_id = userId
        nvlOnline.customer_name = userId
        nvlOnline.recipient_name = mOrderModel.dmDeliveryInfo?.receiverName
        nvlOnline.recipient_id = mOrderModel.dmDeliveryInfo?.receiverPhone
        nvlOnline.company_id = companyId

        mOrderModel.companyTaxCode?.run {
            nvlOnline.tax_code = this
        }
        mOrderModel.companyTaxEmail?.run {
            nvlOnline.legal_email = this
        }
        mOrderModel.companyFullAddress?.run {
            nvlOnline.legal_address = this
        }
        mOrderModel.customerNote?.run {
            nvlOnline.note = this
        }
        nvlOnline.brand_id = mOrderModel.brandId
        nvlOnline.store_id = mOrderModel.storeId
        nvlOnline.location_uid = mOrderModel.dmDeliveryInfo?.locationUid
        nvlOnline.supplier_uid = mOrderModel.details[0]?.supplierUid
        nvlOnline.legal_person = mOrderModel.customerName

        nvlOnline.invoice_origin = null
        mOrderModel.details.forEach {
            var nvlModel = NvlModel(quantity = it.quantity.toInt(), product_uid = it.productUid, description = null, id = null, image_thumb = null, image_urls = null, name = null, price = null, supplier_uid = null, unit = null)
            nvlOnline.invoice_details.add(nvlModel)
        }
        Log.e("json", "data: " + Gson().toJson(nvlOnline))
        Utilities.sendBoardCounlyLib(ApplicationMarketPlace.context, Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.DATA_ORDER, Gson().toJson(nvlOnline))

        return nvlOnline
    }

    fun setOrderOnline(orderType: String) {
        mOrderModel.orderType = orderType
    }

    fun OrderOnlineClearData(){
        mOrderModel.dmVoucher = null
        mOrderModel.discountAmount = 0.0
    }

    fun OrderOnlineAmountItem(): Double {
        var amount = 0.0
        mOrderModel.details?.forEach {
            amount += it.amount
        }
        return amount
    }

    fun OrderOnlineQuantity(): String {
        var total = 0
        mOrderModel.originDetails?.forEach {
            total = total.plus(it.quantity.toInt())
        }

        return "" + total
    }

    fun OrderOnlineTotalAmount(): Double {
        var amount = OrderOnlineAmountItem().minus(mOrderModel.discountAmount)
        mOrderModel.amount = amount

        return amount
    }

    fun OrderOnlineCartClear() {
        mOrderModel.originDetails.clear()
        mOrderModel.details.clear()
        mOrderModel.dmDeliveryInfo = DmDeliveryInfo()
        mOrderModel.orderType = ""
    }

    fun OrderOnlineConvertItemToOrigin(mDatas: ArrayList<DmServiceListOrigin>) {
        mOrderModel.originDetails.clear()
        for (item in mDatas) {
            if (item.quantity > 0) {
                var convertDetail = ArrayList<DmServiceListOrigin>()
                item.details?.forEach {
                    convertDetail.add(it.copy())
                }

                var service = item.copy()
                service.details = convertDetail
                mOrderModel.originDetails.add(service)
            }
        }
    }

    fun OrderOnlineReCheckDataOrigin(dmService: DmService) {
        val it: MutableIterator<DmServiceListOrigin> = mOrderModel.originDetails.iterator()
        while (it.hasNext()) {
            val dmServiceListOrigin: DmServiceListOrigin = it.next()
            if (dmService.serviceName == dmServiceListOrigin.name) {
                if (dmService.quantity == 0.0) {
                    it.remove()
//                    mOrderModel.originDetails.remove(dmServiceListOrigin)
                    if (mOrderModel.orderType === Constants.OrderType.OrderOnline) {
                        Utilities.sendBoardCounlyLib(ApplicationMarketPlace.context, Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.REMOVE_HERMES_PRODUCT_TO_CART)
                    } else {
                        Utilities.sendBoardCounlyLib(ApplicationMarketPlace.context, Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.REMOVE_SCM_PRODUCT_TO_CART)
                    }
                } else {
                    dmServiceListOrigin.quantity = dmService.quantity
                }
            }
        }
    }

    fun OrderOnlineConvertOriginToDetail() {
        mOrderModel.details.clear()
        for (dmServiceListOrigin in mOrderModel.originDetails) {
            if (DmServiceListOrigin.TYPE_COMBO == dmServiceListOrigin.type) {
                val details = dmServiceListOrigin.details
                if (details != null) {
                    var amount = 0.0
                    var comboDesc = ""
                    var timestamp = System.currentTimeMillis()
                    for (item in details) {
                        amount += item.getPrice() * item.quantity
                    }
                    dmServiceListOrigin.amountCombo = amount
                    dmServiceListOrigin.comboId = dmServiceListOrigin.code + "*" + timestamp
                    details.forEachIndexed { index, item ->
                        comboDesc += if (index < details.size.minus(1)) {
                            "x" + StringExt.convertNumberToString(item.quantity * dmServiceListOrigin.quantity) + " " + item.name + "\n"
                        } else {
                            "x" + StringExt.convertNumberToString(item.quantity * dmServiceListOrigin.quantity) + " " + item.name
                        }
                    }
                    dmServiceListOrigin.comboDesc = comboDesc
                    mOrderModel.details.add(ConverUtil.convertServiceListToPromotion(dmServiceListOrigin))
                    for (item in details) {
                        item.comboId = dmServiceListOrigin.code + "*" + timestamp
                        item.quantity = item.baseQuantity * dmServiceListOrigin.quantity
                        mOrderModel.details.add(ConverUtil.convertServiceListToPromotion(item))
                    }
                }
            } else {
                mOrderModel.details.add(ConverUtil.convertServiceListToPromotion(dmServiceListOrigin))
            }
        }
        var count = 1
        for (i in mOrderModel.details.indices) {
            if (DmServiceListOrigin.TYPE_COMBO == mOrderModel.details[i].serviceType || !mOrderModel.details[i].isCombo) {
                mOrderModel.details[i].position = count
                count += 1
            }
        }
    }

    fun OrderOnlineAutoPromotionToDetail(reponse: ArrayList<DmService>) {
        for (i in mOrderModel.details.indices) {
            for (dmService in reponse) {
                if (dmService.serviceCode == mOrderModel.details[i].serviceCode && !dmService.isCombo && !mOrderModel.details[i].isCombo) {
                    mOrderModel.details[i].amount = dmService.amount
                    mOrderModel.details[i].promotionName = dmService.promotionName
                    mOrderModel.details[i].dmPromotion = dmService.dmPromotion
                    mOrderModel.details[i].discountAmount = dmService.discountAmount
                    if (dmService.dmPromotion != null) {
                        mOrderModel.haveAutoPromotion = true
                    }
                }
            }
        }
    }

    //Locate Cart
    fun getCartLocate(): CartLocateModel {
        return dmCartLocate
    }
}