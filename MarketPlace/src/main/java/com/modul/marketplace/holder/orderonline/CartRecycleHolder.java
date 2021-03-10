package com.modul.marketplace.holder.orderonline;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.modul.marketplace.R;
import com.modul.marketplace.holder.AbsRecyleHolder;
import com.modul.marketplace.model.orderonline.DmService;
import com.modul.marketplace.model.orderonline.DmServiceListOrigin;
import com.modul.marketplace.util.FormatNumberUtil;


public class CartRecycleHolder extends AbsRecyleHolder {

    private TextView mName;
    private TextView mQuantity;
    private TextView mPrice;
    private TextView mReducedPrice;
    private TextView mAdress;
    //    private TextView mPosition;
    private TextView mPromotionName;
    private ImageView mImage;
    private DmService dmService;
    private CartRecycleHolder.OnItemClickRycle mOnItemClickRycle;


    public CartRecycleHolder(Context mContext, View view, CartRecycleHolder.OnItemClickRycle mOnItemClickRycle) {
        super(mContext, view);
        this.mOnItemClickRycle = mOnItemClickRycle;
        findViewd(getConvertView());

    }

    public static CartRecycleHolder newInstance(Context mContext, LayoutInflater inflater, CartRecycleHolder.OnItemClickRycle mOnItemClickRycle) {
        View convertView = inflater.inflate(getItemLayout(), null);
        CartRecycleHolder mMenuFoodItemRecycleHolder = new CartRecycleHolder(mContext, convertView, mOnItemClickRycle);
        return mMenuFoodItemRecycleHolder;
    }

    private static int getItemLayout() {
        return R.layout.adapter_cart;
    }

    private void findViewd(View convertView) {
        mName = convertView.findViewById(R.id.title);
        mQuantity = convertView.findViewById(R.id.quantity);
        mPrice = convertView.findViewById(R.id.price);
        mAdress = convertView.findViewById(R.id.adress);
        mImage = convertView.findViewById(R.id.image);
        mReducedPrice = convertView.findViewById(R.id.total_price);
        mPromotionName = convertView.findViewById(R.id.promotion_name);

        getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickRycle.onItemChoice(dmService);
            }
        });
    }

    @Override
    public void setElement(Object obj) {

        setData((DmService) obj);

    }

    private void setData(DmService item) {
        this.dmService = item;

        Glide.with(mContext).load(item.getServiceImage()).into(mImage);

        mQuantity.setText("SL: " + FormatNumberUtil.fmt(dmService.getQuantity()));
        if (DmServiceListOrigin.TYPE_COMBO.equals(dmService.getServiceType())) {
            mPrice.setText(FormatNumberUtil.formatCurrency(dmService.getAmountCombo() *  dmService.getQuantity()));
            mAdress.setText(dmService.getComboDesc());
            mName.setText(dmService.getServiceName());
        } else {
            if (!dmService.isCombo()) {
                mAdress.setVisibility(View.GONE);
                mPrice.setVisibility(View.VISIBLE);
//                mAdress.setText(FormatNumberUtil.formatCurrency(dmService.getOrgPrice()) + "/" + dmService.getServiceUnit());
                if (dmService.getDmPromotion() != null) {
                    mReducedPrice.setVisibility(View.VISIBLE);
                    mReducedPrice.setText(FormatNumberUtil.formatCurrency(dmService.getAmount()));
                    mPrice.setText(FormatNumberUtil.formatCurrency(dmService.getOrgPrice() * dmService.getQuantity()));
                    mPrice.setPaintFlags(mPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    mPrice.setAlpha(0.5f);
                    mPromotionName.setVisibility(View.VISIBLE);
                    mPromotionName.setText("KM: " + dmService.getPromotionName());
                } else {
                    mPrice.setAlpha(1f);
                    mPrice.setPaintFlags(0);
                    mPromotionName.setVisibility(View.GONE);
                    mReducedPrice.setVisibility(View.GONE);
                    mPrice.setText(FormatNumberUtil.formatCurrency(dmService.getAmount()));
                }
                mName.setText(dmService.getServiceName());
            } else {
                mAdress.setVisibility(View.GONE);
                mPrice.setVisibility(View.GONE);
                mName.setText("   + " + dmService.getServiceName());
            }

        }
        isGitfs(dmService);
    }

    private void isGitfs(DmService dmService) {
        if (dmService.getIsGift() == 1) {
            mName.setTextColor(mResources.getColor(R.color.color_2FBC47));
            mAdress.setTextColor(mResources.getColor(R.color.color_2FBC47));
            mQuantity.setTextColor(mResources.getColor(R.color.color_2FBC47));
            mPrice.setTextColor(mResources.getColor(R.color.color_2FBC47));
            mReducedPrice.setTextColor(mResources.getColor(R.color.color_2FBC47));
            mPromotionName.setTextColor(mResources.getColor(R.color.color_2FBC47));
        } else {
            mName.setTextColor(mResources.getColor(R.color.text_36));
            mAdress.setTextColor(mResources.getColor(R.color.color_808080));
            mQuantity.setTextColor(mResources.getColor(R.color.mainColor));
            mPrice.setTextColor(mResources.getColor(R.color.text_36));
            mReducedPrice.setTextColor(mResources.getColor(R.color.text_36));
            mPromotionName.setTextColor(mResources.getColor(R.color.text_36));
        }

    }


    public interface OnItemClickRycle {
        void onItemChoice(DmService dmService);
    }


}

