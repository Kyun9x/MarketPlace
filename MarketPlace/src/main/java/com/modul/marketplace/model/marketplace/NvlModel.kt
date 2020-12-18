package com.modul.marketplace.model.marketplace

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NvlModel(
        @SerializedName("id") var id: String? = "",
        @SerializedName("name") var name: String? = "",
        @SerializedName("price") var price: Double? = 0.0,
        @SerializedName("price_sale") var price_sale: Double? = 0.0,
        @SerializedName("supplier_uid") var supplier_uid: String? = "",
        @SerializedName("supplier") var supplier: SupplierModel? = null,
        @SerializedName("brand") var brand: BrandModel? = null,
        @SerializedName("image_thumb") var image_thumb: String? = "",
        @SerializedName("image_urls") var image_urls: ArrayList<NvlImageModel>? = ArrayList(),
        @SerializedName("quantity") var quantity: Int? = 0,
        @SerializedName("product_uid") var product_uid: String? = "",
        @SerializedName("product") var product: NvlModel? = null,
        @SerializedName("trademark") var trademark: TrademarkModel? = null,
        @SerializedName("unit") var unit: NvlUnitModel? = NvlUnitModel(),
        @SerializedName("description") var description: String? = "",
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class TrademarkModel(
        @SerializedName("active") var active: Int? = null,
        @SerializedName("trademark_id") var trademark_id: String? = null,
        @SerializedName("trademark_name") var trademark_name: String? = null,
        @SerializedName("trademark_type") var trademark_type: String? = null
)

data class NvlImageModel(
        @SerializedName("url") var url: String? = "",
        @SerializedName("url_thumb") var url_thumb: String? = ""
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class BrandModel(
        @SerializedName("brand_name") var brand_name: String? = ""
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class SupplierModel(
        @SerializedName("supplier_address") var supplier_address: String? = "",
        @SerializedName("supplier_name") var supplier_name: String? = ""
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class NvlUnitModel(
        @SerializedName("unit_name") var unit_name: String? = ""
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class NvlModelData(
        @SerializedName("data") var data: ArrayList<NvlModel>? = null
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class NvlOnlineModelData(
        @SerializedName("data") var data: NvlOnlineModel? = null
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class NvlOnlineModelDataList(
        @SerializedName("data") var data: ArrayList<NvlOnlineModel> = ArrayList()
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class NvlLocateModel(
        @SerializedName("location_name") var location_name: String? = ""
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class NvlOnlineModel(
        @SerializedName("id") var id: String? = null,
        @SerializedName("invoice_id") var invoice_id: String? = null,
        @SerializedName("created_at") var created_at: String? = null,
        @SerializedName("address") var address: String? = null,
        @SerializedName("recipient_id") var recipient_id: String? = null,
        @SerializedName("recipient_name") var recipient_name: String? = null,
        @SerializedName("status") var status: String? = null,
        @SerializedName("voucher_code") var voucher_code: String? = null,
        @SerializedName("original_amount") var original_amount: Double? = null,
        @SerializedName("discount_amount") var discount_amount: Double? = null,
        @SerializedName("voucher_amount") var voucherAmount: Double? = null,
        @SerializedName("final_amount") var final_amount: Double? = null,
        @SerializedName("is_red_invoice") var is_red_invoice: Int? = null,
        @SerializedName("customer_name") var customer_name: String? = null,
        @SerializedName("customer_id") var customer_id: String? = null,
        @SerializedName("tax_code") var tax_code: String? = null,
        @SerializedName("company_id") var company_id: String? = null,
        @SerializedName("legal_email") var legal_email: String? = null,
        @SerializedName("legal_address") var legal_address: String? = null,
        @SerializedName("note") var note: String? = null,
        @SerializedName("location_uid") var location_uid: String? = null,
        @SerializedName("brand_id") var brand_id: String? = null,
        @SerializedName("location") var location: NvlLocateModel? = null,
        @SerializedName("store_id") var store_id: String? = null,
        @SerializedName("supplier_uid") var supplier_uid: String? = null,
        @SerializedName("invoice_details") var invoice_details: ArrayList<NvlModel> = ArrayList(),
        var invoice_origin: ArrayList<NvlModel>? = ArrayList(),
        @SerializedName("legal_person") var legal_person: String? = null
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}