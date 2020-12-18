package com.modul.marketplace.model.orderonline

import java.io.Serializable

data class RowItemModel(
        var title: String? = null,
        var content: String? = null,
        var contentColor: Int? = null,
        var contentStyle: Int? = null,
        var isOnlyTitle: Boolean? = null
) : Serializable