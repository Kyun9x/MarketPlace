package com.modul.marketplace.adapter.marketplace

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.modul.marketplace.activity.marketplace.ArticleFragment
import com.modul.marketplace.activity.marketplace.NvlFragment
import com.modul.marketplace.activity.order_online.PurchaseFragment
import com.modul.marketplace.model.marketplace.NotificationModel

class MarketPlaceAdapter(fa: FragmentActivity,pushNotify : NotificationModel?) : FragmentStateAdapter(fa) {

    private val roleSale by lazy {
        arrayListOf(
                PurchaseFragment.newInstance(pushNotify),
                NvlFragment.newInstance(pushNotify),
                ArticleFragment.newInstance(pushNotify)
        )
    }

    override fun getItemCount(): Int {
        return roleSale.size
    }

    override fun createFragment(position: Int): Fragment {
        return roleSale[position]
    }
}