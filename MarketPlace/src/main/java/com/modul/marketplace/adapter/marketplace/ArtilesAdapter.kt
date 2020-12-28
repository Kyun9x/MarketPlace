package com.modul.marketplace.adapter.marketplace

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.modul.marketplace.R
import com.modul.marketplace.adapter.BaseRecycleAdapter
import com.modul.marketplace.extension.gone
import com.modul.marketplace.model.marketplace.ArticlesModel
import com.modul.marketplace.util.FormatNumberUtil
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.adapter_service_list.*

class ArtilesAdapter(
        private val context: Context,
        private val data: ArrayList<ArticlesModel>,
        val itemClickListener: (ArticlesModel) -> Unit
) :
        RecyclerView.Adapter<ArtilesAdapter.ViewHolder>() {

    private var isLoaderVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
                LayoutInflater.from(parent.context).inflate(R.layout.adapter_service_list, parent, false)
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
                if (mImage_urls != null && mImage_urls.size > 0) {
                    Glide.with(context).load(mImage_urls[0].url_thumb).apply(
                            RequestOptions().placeholder(R.drawable.icon_default).override(100, 100)
                    ).into(image)
                }else{
                    Glide.with(context).load(R.drawable.icon_default).into(image)
                }

                total_price.text = FormatNumberUtil.formatCurrency(mPrice)
                title.text = mTitle
                adress.text = mContent
                plus.gone()

                itemView.setOnClickListener {
                    itemClickListener(model)
                }
            }
        }
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    fun addItems(item: ArrayList<ArticlesModel>) {
        data.addAll(item)
        notifyDataSetChanged()
    }

    fun addLoading() {
        isLoaderVisible = true
        data.add(ArticlesModel())
        notifyItemInserted(data.size - 1)
    }

    fun removeLoading() {
        isLoaderVisible = false
        val position = data.size - 1
        val item: ArticlesModel = getItem(position)
        if (item != null) {
            data.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun getItem(position: Int): ArticlesModel {
        return data[position]
    }

}