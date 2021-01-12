package com.modul.marketplace.adapter.marketplace

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.modul.marketplace.R
import com.modul.marketplace.adapter.BaseRecycleAdapter
import com.modul.marketplace.app.Constants
import com.modul.marketplace.extension.StringExt
import com.modul.marketplace.extension.ctx
import com.modul.marketplace.extension.initAvatarCompany
import com.modul.marketplace.model.marketplace.ArticlesModel
import com.modul.marketplace.util.DateTimeUtil.Companion.convertTimeStampToDate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.holder_my_articles.*

class MyArticlesListAdapter(
        private val context: Context,
        private val data: List<ArticlesModel>,
        val itemClickListener: (ArticlesModel) -> Unit,
) :
        RecyclerView.Adapter<MyArticlesListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.ctx).inflate(R.layout.holder_my_articles, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (itemCount > 0) {
            val item = data[position]
            holder.bind(item)
        }
    }

    inner class ViewHolder(itemView: View) : BaseRecycleAdapter(context, itemView), LayoutContainer {
        override val containerView: View?
            get() = itemView

        fun bind(model: ArticlesModel) {
            with(model) {
                mLbTitle.text = StringExt.isTextEmpty(mTitle)
                mLbContent.text = StringExt.isTextEmpty(mContent)
                mLbPrice.text = StringExt.convertToMoney(mPrice)
                dateName?.let{
                    mTime.text = it
                }
                if(mImage_urls != null && mImage_urls?.size!! > 0){
                    initAvatarCompany(context,mImage,mImage_urls!![0].url_thumb.toString(),mContext.getDrawable(R.drawable.icon_default))
                }else{
                    initAvatarCompany(context,mImage,"",mContext.getDrawable(R.drawable.icon_default))
                }

                convertView.setOnClickListener {
                    itemClickListener(this)
                }

            }
        }
    }
}