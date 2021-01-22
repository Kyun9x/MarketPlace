package com.modul.marketplace.model.marketplace

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ArticlesModel(
        @SerializedName("id") var id: String? = null,
        @SerializedName("image_urls") var mImage_urls: ArrayList<ArticlesImageModel>? = null,
        @SerializedName("title") var mTitle: String? = null,
        @SerializedName("status") var status: String? = null,
        @SerializedName("active") var active: Int? = null,
        @SerializedName("created_at") var created_at: String? = null,
        @SerializedName("brand_ids") var brand_ids: String? = null,
        @SerializedName("data_type") var data_type: String? = null,
        @SerializedName("expire_time") var expire_time: Long? = null,
        @SerializedName("type") var type: String? = null,
        @SerializedName("author_id") var author_id: String? = null,
        @SerializedName("author_name") var mAuthor_name: String? =null,
        @SerializedName("author_phone") var mAuthor_phone: String? = null,
        @SerializedName("city_uid") var city_uid: String? = null,
        @SerializedName("tags_uid") var tags_uid: ArrayList<String>? = null,
        @SerializedName("tags") var listTag: ArrayList<TagsModel>? = null,
        @SerializedName("brand_id") var brand_id: String? =null,
        @SerializedName("company_id") var company_id: String? = null,
        @SerializedName("dateName") var dateName: String? = null,
        @SerializedName("city") var city: ArticlesCityModel? = null,
        @SerializedName("price") var mPrice: Double? = 0.0,
        @SerializedName("view_count") var view_count: Int? = 0,
        @SerializedName("content") var mContent: String? = null
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class ArticlesImageModel(
        @SerializedName("url") var url: String? = "",
        @SerializedName("url_thumb") var url_thumb: String? = ""
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class UploadImageModel(
        @SerializedName("img_url") var img_url: String? = "",
        @SerializedName("img_url_thumb") var img_url_thumb: String? = ""
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class ArticlesImageModelData(
        @SerializedName("data") var data: UploadImageModel? = null
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class ArticlesCityModel(
        @SerializedName("city_name") var city_name: String? = ""
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class ArticlesModelData(
        @SerializedName("data") var data: ArrayList<ArticlesModel> = ArrayList()
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class ArticlesModelDataObject(
        @SerializedName("data") var data: ArticlesModel? = null
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class ArticlesCountModelData(
        @SerializedName("data") var data: ArticlesCountModel? = null
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class ArticlesCountModel(
        @SerializedName("confirmed") var confirmed: Int? = null,
        @SerializedName("canceled") var canceled: Int? = null,
        @SerializedName("sold") var sold: Int? = null,
        @SerializedName("pending") var pending: Int? = null,
        @SerializedName("expired") var expired: Int? = null
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}