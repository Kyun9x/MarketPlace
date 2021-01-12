package com.modul.marketplace.activity.marketplace

import android.os.Bundle
import android.text.TextUtils
import android.widget.RatingBar
import com.modul.marketplace.R
import com.modul.marketplace.activity.BaseActivity
import com.modul.marketplace.app.Constants
import com.modul.marketplace.extension.initIcon
import com.modul.marketplace.extension.showStatusBar
import com.modul.marketplace.extension.visible
import com.modul.marketplace.model.marketplace.FeedbackModel
import com.modul.marketplace.model.marketplace.FeedbackModelData
import com.modul.marketplace.restful.ApiRequest
import com.modul.marketplace.util.ToastUtil
import kotlinx.android.synthetic.main.activity_feedback.*
import kotlinx.android.synthetic.main.include_header2.*

class FeedbackActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        initData()
        initClick()
    }

    private fun initClick() {
        mIconLeft.setOnClickListener { onBackPressed() }
        mOrder.setOnClickListener { feedback()}
        success.setOnClickListener { onBackPressed() }
    }

    private fun feedback() {
        if (TextUtils.isEmpty(mSDT.text.toString())) {
            ToastUtil.makeText(this, getString(R.string.phone_valid))
            return
        }
        var feedbackModel = FeedbackModel(brand_id = mCartBussiness.getListBrandId(),company_id = mCartBussiness.companyId, feedback_title = mLb.text.toString(), feedback_content = mNote.text.toString(),customer_email = "", customer_name = mCartBussiness.userId, customer_phone = mSDT.text.toString())
        val callback: ApiRequest<FeedbackModelData> = ApiRequest()
        callback.setCallBack(mApiSCM?.apiSCMFeedback(feedbackModel),
                { response ->
                    response?.run {
                        data?.run {
                            layout_success.visible()
                        }
                    }
                }) { error ->
            error.printStackTrace()
            ToastUtil.makeText(this, getString(R.string.error_network2))
        }
    }

    private fun initData() {
        showStatusBar(statusColor = true, color = R.color.white)
        mlbTitle.text = getString(R.string.feedback)
        mRating.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { p0, p1, p2 ->
                when(p1){
                    1.0.toFloat() -> {
                        initIcon(baseContext, imageView4, R.drawable.icon_fb_1)
                        mLb.text = getString(R.string.rate_lb_1)
                    }
                    2.0.toFloat() -> {

                        initIcon(baseContext, imageView4, R.drawable.icon_fb_2)
                        mLb.text = getString(R.string.rate_lb_2)
                    }
                    3.0.toFloat() -> {

                        initIcon(baseContext, imageView4, R.drawable.icon_fb_3)
                        mLb.text = getString(R.string.rate_lb_3)
                    }
                    4.0.toFloat() -> {

                        initIcon(baseContext, imageView4, R.drawable.icon_fb_4)
                        mLb.text = getString(R.string.rate_lb_4)
                    }
                    5.0.toFloat() -> {
                        initIcon(baseContext, imageView4, R.drawable.icon_fb_5)
                        mLb.text = getString(R.string.rate_lb_5)
                    }
                }
            }

    }

}