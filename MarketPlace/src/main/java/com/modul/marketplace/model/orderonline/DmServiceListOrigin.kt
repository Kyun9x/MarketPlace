package com.modul.marketplace.model.orderonline

import com.google.gson.annotations.SerializedName
import com.modul.marketplace.model.marketplace.NvlImageModel
import com.modul.marketplace.model.marketplace.TrademarkModel
import java.io.Serializable

data class DmServiceListOrigin(
        @SerializedName("code") var code: String? = "",
        @SerializedName("productCode") var productCode: String? = "",
        @SerializedName("name") var name: String? = "",
        @SerializedName("desc") var desc: String? = "",
        @SerializedName("type") var type: String? = "",
        @SerializedName("image") var image: String? = "",
        @SerializedName("isPrePaid") var isPrePaid: Int? = 0,
        @SerializedName("marketPrice") var marketPrice: Double? = null,
        @SerializedName("unitPrice") var unitPrice: Double = 0.0,
        @SerializedName("price_sale") var priceSale: Double = 0.0,
        @SerializedName("unitScope") var unitScope: String? = "",
        @SerializedName("unitName") var unitName: String? = "",
        @SerializedName("minChoice") var minChoice: Double = 0.0,
        @SerializedName("maxChoice") var maxChoice: Double = 0.0,
        @SerializedName("quantity") var quantity: Double = 0.0,
        var baseQuantity: Double = 0.0,
        @SerializedName("trialDay") var trialDay: Int? = 0,
        @SerializedName("isOnline") var isOnline: Int? = 0,
        @SerializedName("_id") var uId: String? = "",
        @SerializedName("supplier_uid") var supplierUid: String? = "",
        @SerializedName("productUid") var productUid: String? = "",
        @SerializedName("brand_name") var brand_name: String? = "",
        @SerializedName("status") var status: String? = "",
        @SerializedName("createdBy") var createdBy: String? = "",
        @SerializedName("createdTime") var createdTime: String? = "",
        @SerializedName("supplier_name") var supplier_name: String? = "",
        @SerializedName("supplier_address") var supplier_address: String? = "",
        @SerializedName("updatedBy") var updatedBy: String? = "",
        @SerializedName("updatedTime") var updatedTime: String? = "",
        @SerializedName("details") var details: ArrayList<DmServiceListOrigin>? = ArrayList(),
        @SerializedName("SKU") var sKU: String? = "",
        @SerializedName("position") var position: Int? = 0,
        @SerializedName("origin_amount") var orgAmount: Double = 0.0,
        @SerializedName("discount_amount") var discountAmount: Double? = 0.0,
        @SerializedName("promotion_name") var promotionName: String? = "",
        @SerializedName("reduced_price") var reducedPrice: Double? = 0.0,
        @SerializedName("comboId") var comboId: String? = "",
        @SerializedName("comboDesc") var comboDesc: String? = "",
        @SerializedName("amountCombo") var amountCombo: Double? = 0.0,
        @SerializedName("trademark") var trademark: TrademarkModel? = null,
        @SerializedName("image_urls") var imageUrls: ArrayList<NvlImageModel>? = ArrayList()
) : Serializable {
    companion object {
        const val TYPE_SUB = "sub"
        const val TYPE_HARDWARE = "hardware"
        const val TYPE_COMBO = "combo"
    }

    fun getOriginAmount(): Double {
        orgAmount = getPrice() * quantity
        return orgAmount
    }

    fun getPrice():Double{
        var price = 0.0
        if(marketPrice != null){
            price = marketPrice!!
        }else{
            price = unitPrice
        }
        return price
    }
}

