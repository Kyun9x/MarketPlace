package com.modul.marketplace.holder.orderonline;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.view.SimpleDraweeView;
import com.modul.marketplace.R;
import com.modul.marketplace.holder.AbsRecyleHolder;
import com.modul.marketplace.model.orderonline.DmServiceListOrigin;
import com.modul.marketplace.util.FormatNumberUtil;

import java.util.ArrayList;

public class ServicelistRecycleHolder extends AbsRecyleHolder {

    private ImageView mImage;
    private TextView mName;
    private TextView mQuantity;
    private TextView price_origin;
    private ImageView mPlus;
    private ImageView mSub;
    private TextView mPrice;
    private TextView mSubTitle;
    private ImageView mAdd;
    private View mView;

    private DmServiceListOrigin mDmServiceListOrigin;
    private ServicelistRecycleHolder.OnItemClickRycle mOnItemClickRycle;


    public ServicelistRecycleHolder(Context mContext, View view, ServicelistRecycleHolder.OnItemClickRycle mOnItemClickRycle) {
        super(mContext, view);
        this.mOnItemClickRycle = mOnItemClickRycle;
        findViewd(getConvertView());

    }

    public static ServicelistRecycleHolder newInstance(Context mContext, LayoutInflater inflater, ServicelistRecycleHolder.OnItemClickRycle mOnItemClickRycle) {
        View convertView = inflater.inflate(getItemLayout(), null);
        ServicelistRecycleHolder mMenuFoodItemRecycleHolder = new ServicelistRecycleHolder(mContext, convertView, mOnItemClickRycle);
        return mMenuFoodItemRecycleHolder;
    }

    private static int getItemLayout() {
        return R.layout.adapter_service_list;
    }

    private void findViewd(View convertView) {
        mImage = convertView.findViewById(R.id.image);
        mName = convertView.findViewById(R.id.title);
        mQuantity = convertView.findViewById(R.id.quantity);
        mPlus = convertView.findViewById(R.id.up);
        mSub = convertView.findViewById(R.id.down);
        mPrice = convertView.findViewById(R.id.total_price);
        mSubTitle = convertView.findViewById(R.id.adress);
        mAdd = convertView.findViewById(R.id.plus);
        mView = convertView.findViewById(R.id.mView);
        price_origin = convertView.findViewById(R.id.price_origin);

        mView.setOnClickListener(v -> {
            mOnItemClickRycle.onClickDes(mDmServiceListOrigin);
        });

        mAdd.setOnClickListener(view -> {
            mOnItemClickRycle.onAdd(mDmServiceListOrigin);
        });


        mPlus.setOnClickListener(v -> {
            mOnItemClickRycle.onPlus(mDmServiceListOrigin);
        });

        mSub.setOnClickListener(v -> {
            mOnItemClickRycle.onMinus(mDmServiceListOrigin);
        });
    }

    @Override
    public void setElement(Object obj) {
        setData((DmServiceListOrigin) obj);
    }

    private void setData(DmServiceListOrigin item) {
        this.mDmServiceListOrigin = item;
        Glide.with(mContext).load(item.getImage()).apply(
                new RequestOptions().placeholder(R.drawable.icon_default)
        ).into(mImage);

        price_origin.setPaintFlags(price_origin.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        price_origin.setVisibility(View.GONE);
         if (DmServiceListOrigin.TYPE_COMBO.equals(item.getType())) {
            String details = "";
            double saleAmount = 0.0;
            double unitAmount = 0.0;
            ArrayList<DmServiceListOrigin> mDetails = item.getDetails();
            boolean showPriceSale = false;
            if (mDetails != null) {
                for (DmServiceListOrigin dmServiceListOrigin : mDetails) {
                    details += "+ " + FormatNumberUtil.fmt(dmServiceListOrigin.getQuantity()) + " " + dmServiceListOrigin.getUnitName() + " " + dmServiceListOrigin.getName() + "\n";
                    saleAmount += dmServiceListOrigin.getPrice() * dmServiceListOrigin.getQuantity();
                    unitAmount += dmServiceListOrigin.getUnitPrice() * dmServiceListOrigin.getQuantity();
                    if(dmServiceListOrigin.getMarketPrice() != null && dmServiceListOrigin.getUnitPrice() !=  dmServiceListOrigin.getMarketPrice()){
                        showPriceSale = true;
                    }
                }
            }
             if(showPriceSale){
                 price_origin.setVisibility(View.VISIBLE);
             }
            mSubTitle.setText(details.substring(0, details.length() - 1));
            mPrice.setText(FormatNumberUtil.formatCurrency(saleAmount) + "/" + item.getUnitName());
             price_origin.setText(FormatNumberUtil.formatCurrency(unitAmount));
        } else {
            if(item.getMarketPrice() != null && item.getUnitPrice() !=  item.getMarketPrice()){
                price_origin.setVisibility(View.VISIBLE);
            }
            mPrice.setText(FormatNumberUtil.formatCurrency(item.getPrice()) + "/" + item.getUnitName());
            price_origin.setText(FormatNumberUtil.formatCurrency(item.getUnitPrice()));
        }
        mName.setText(item.getName());
        mSubTitle.setText(item.getDesc());
        if (item.getQuantity() > 0) {
            hideLayoutQuantity();
        } else {
            hidenLayoutQuantity();
        }
        mQuantity.setText(FormatNumberUtil.fmt(item.getQuantity()));
    }

    private void hideLayoutQuantity() {
        mAdd.setVisibility(View.GONE);
        mSub.setVisibility(View.VISIBLE);
        mQuantity.setVisibility(View.VISIBLE);
        mPlus.setVisibility(View.VISIBLE);
        mSub.setEnabled(true);
        mQuantity.setEnabled(true);
        mPlus.setEnabled(true);
    }

    private void hidenLayoutQuantity() {
        mAdd.setVisibility(View.VISIBLE);
        mSub.setVisibility(View.INVISIBLE);
        mQuantity.setVisibility(View.INVISIBLE);
        mPlus.setVisibility(View.INVISIBLE);
        mSub.setEnabled(false);
        mQuantity.setEnabled(false);
        mPlus.setEnabled(false);
    }

    public interface OnItemClickRycle {
        void onClickDes(DmServiceListOrigin dmServiceListOrigin);

        void onPlus(DmServiceListOrigin dmServiceListOrigin);

        void onMinus(DmServiceListOrigin dmServiceListOrigin);

        void onAdd(DmServiceListOrigin dmServiceListOrigin);
    }
}

