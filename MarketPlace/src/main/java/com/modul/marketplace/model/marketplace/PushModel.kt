package com.modul.marketplace.model.marketplace

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PushModel(
        @SerializedName("user_id") var user_id: String? = null,
        @SerializedName("device_id") var deviceId: String? = null,
        @SerializedName("brand_id") var brand_id: String? = null,
        @SerializedName("company_id") var company_id: String? = null,
        @SerializedName("osType") var osType: String? = null,
        @SerializedName("device_type") var device_type: String? = null,
        @SerializedName("appVersion") var appVersion: String? = null,
        @SerializedName("registration_id") var pushId: String? = null,
        @SerializedName("model") var model: String? = null,
        @SerializedName("language") var language: String? = null,
        @SerializedName("macId") var macId: String? = null,
        @SerializedName("osVersion") var osVersion: String? = null
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}


data class GetPushData(
        @SerializedName("message") var getPushData: GetPushModel? = null
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}


data class NotifyData(
        @SerializedName("data") var data: NotifyListData? = null
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class NotifyListData(
        @SerializedName("data") var data: ArrayList<GetPushModel>? = ArrayList()
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class GetPushModel(
        @SerializedName("data") var data: Any? = null,
        @SerializedName("employeeEmail") var employeeEmail: Any? = null,
        @SerializedName("title") var title: Any? = null,
        @SerializedName("error") var error: Error? = null,
        @SerializedName("pushId") var pushId: Any? = null,
        @SerializedName("type") var type: Any? = null,
        @SerializedName("image") var image: Any? = null,
        @SerializedName("isRead") var isRead: Any? = null,
        @SerializedName("companyId") var companyId: Any? = null,
        @SerializedName("receiptCode") var receiptCode: Any? = null,
        @SerializedName("leadId") var leadId: Any? = null,
        @SerializedName("_id") var _id: Any? = null,
        @SerializedName("isToday") var isToday: Boolean? = null,
        @SerializedName("roCode") var roCode: Any? = null,
        @SerializedName("leadCareLogId") var leadCareLogId: Any? = null,
        @SerializedName("createdTime") var createdTime: Any? = null
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class PushFilter(
        @SerializedName("page") var page: Any? = null,
        @SerializedName("results_per_page") var results_per_page: Any? = null
) : Serializable {
    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class InBoxModelData(
        @SerializedName("data") var data: ArrayList<InboxModel>? = ArrayList(),
        @SerializedName("unread_message") var unread_message: Int? = null
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class InBoxModelDataObject(
        @SerializedName("data") var data: InboxModel? = null
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class InboxModel(
        @SerializedName("id") var id: String? = null,
        @SerializedName("page") var page: Int? = null,
        @SerializedName("results_per_page") var results_per_page: Int? = null,
        @SerializedName("user_id") var user_id: String? = null,
        @SerializedName("brand_id") var brand_id: String? = null,
        @SerializedName("company_id") var company_id: String? = null,
        @SerializedName("notification_uid") var notification_uid: String? = null,
        @SerializedName("read_at") var read_at: Long? = null,
        @SerializedName("checked_at") var checked_at: Any? = null,
        @SerializedName("notification") var notification: NotificationModel? = null,
        @SerializedName("created_at") var created_at: String? = null
) : Serializable {
    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class NotificationModel(
        @SerializedName("source") var source: String? = null,
        @SerializedName("id") var id: String? = null,
        @SerializedName("notify_type") var notify_type: String? = null,
        @SerializedName("notify_link") var notify_link: String? = null,
        @SerializedName("partner_notify_id") var partner_notify_id: String? = null,
        @SerializedName("notify_image_path") var notify_image_path: String? = null,
        @SerializedName("notify_title_html") var notify_title_html: String? = null,
        @SerializedName("notify_messages_html") var notify_messages_html: String? = null,
        @SerializedName("notify_messages") var notify_messages: String? = null,
        @SerializedName("notify_id") var notify_id: String? = null,
        @SerializedName("notify_detail") var notify_detail: NotificationDetailModel? = null,
        @SerializedName("notify_title") var notify_title: String? = null
) : Serializable {
    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class NotificationDetailModel(
        @SerializedName("product_type") var product_type: String? = null,
        @SerializedName("product_id") var product_id: String? = null,
        @SerializedName("id") var id: String? = null
) : Serializable {
    fun toJson(): String {
        return Gson().toJson(this)
    }
}