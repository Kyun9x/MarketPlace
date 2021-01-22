package com.modul.marketplace.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.modul.marketplace.activity.order_online.CartFragment;
import com.modul.marketplace.activity.order_online.HistoryOrderDetailFragment;
import com.modul.marketplace.activity.order_online.InformationFragment;
import com.modul.marketplace.activity.order_online.OrderDetailFragment;
import com.modul.marketplace.activity.order_online.PurchaseFragment;
import com.modul.marketplace.app.Constants;
import com.modul.marketplace.R;

import static com.modul.marketplace.extension.ActivityExtKt.showStatusBar;

/**
 * Created by mrone on 1/9/18.
 */

public class CateActivity extends BaseActivity {

    public static final int TYPE_CHARTOTHER = 1;
    public static final int TYPE_ORDER_ONLINE = 2;
    public static final int TYPE_CART = 3;
    public static final int TYPE_INFORMATION_FRAGMENT = 4;
    public static final int TYPE_ORDER_DETAIL_FRAGMENT = 5;
    public static final int TYPE_HISTORY_ORDER_DETAIL = 6;

    public int mType = 0;
    public Fragment fragment;
    public InformationFragment informationFragment;
    public OrderDetailFragment orderDetailFragment;

    public static String keyTitle = "keyTitle";
    public static String keyPhone = "keyPhone";
    public static String keySoccialId = "keySoccialId";
    public static String keyType = "keyType";
    public static String keySession = "keySession";
    public static String keyObject = "keyObject";
    public static String keyObject2 = "keyObject2";
    public static String keyTotal = "keyTotal";
    public static String keyLink = "keyLink";
    public static String keyObjectDetail = "keyObjectDetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_no_homebar);

        mType = getIntent().getIntExtra(Constants.KEY_TYPE, 0);
        if (mType == TYPE_ORDER_ONLINE) {
            displayOrderOnline();
        } else if (mType == TYPE_CART) {
            showStatusBar(this, R.color.grayF8, null, true);
            dispLayCart();
        } else if (mType == TYPE_INFORMATION_FRAGMENT) {
            showStatusBar(this, R.color.grayF8, null, true);
            displayInformation();
        } else if (mType == TYPE_ORDER_DETAIL_FRAGMENT) {
            displayOrderDetail((String) getIntent().getSerializableExtra(keyObject), (String) getIntent().getSerializableExtra(keyObjectDetail));
        } else if (mType == TYPE_HISTORY_ORDER_DETAIL) {
            showStatusBar(this, R.color.grayF8, null, true);
            displayHistoryOrderDetail();
        }
    }

    private void displayHistoryOrderDetail() {
        if (fragment == null) {
            fragment = HistoryOrderDetailFragment.newInstance();
        }
        executeFragmentTransaction(fragment, R.id.content, false, false);
    }

    private void displayOrderDetail(String orderCode, String type) {
        if (orderDetailFragment == null) {
            orderDetailFragment = OrderDetailFragment.newInstance(orderCode, type);
        }
        executeFragmentTransaction(orderDetailFragment, R.id.content, false, false);
    }

    private void displayInformation() {
        if (informationFragment == null) {
            informationFragment = InformationFragment.newInstance();
        }
        executeFragmentTransaction(informationFragment, R.id.content, false, false);
    }

    private void dispLayCart() {
        if (fragment == null) {
            fragment = CartFragment.newInstance();
        }
        executeFragmentTransaction(fragment, R.id.content, false, false);
    }


    protected void displayOrderOnline() {
//        if (fragment == null) {
//            fragment = PurchaseFragment.newInstance();
//        }
//        executeFragmentTransaction(fragment, R.id.content, false, false);
    }

    //=====================
    public static void gotoHistoryOrderDetail(Context mContext) {
        Intent i = new Intent(mContext, CateActivity.class);
        i.putExtra(Constants.KEY_TYPE, TYPE_HISTORY_ORDER_DETAIL);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
    }


    public static void gotoOrderOnline(Context mContext) {
        Intent i = new Intent(mContext, CateActivity.class);
        i.putExtra(Constants.KEY_TYPE, TYPE_ORDER_ONLINE);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
    }

    public static void gotoCartFragment(Context mContext) {
        Intent i = new Intent(mContext, CateActivity.class);
        i.putExtra(Constants.KEY_TYPE, TYPE_CART);
        mContext.startActivity(i);
    }

    public static void gotoInformationFragment(Context mContext) {
        Intent i = new Intent(mContext, CateActivity.class);
        i.putExtra(Constants.KEY_TYPE, TYPE_INFORMATION_FRAGMENT);
        mContext.startActivity(i);
    }


    public static void gotoOrderDetailFragment(Context mContext, String orderCode, String type) {
        Intent i = new Intent(mContext, CateActivity.class);
        i.putExtra(Constants.KEY_TYPE, TYPE_ORDER_DETAIL_FRAGMENT);
        i.putExtra(keyObject, orderCode);
        i.putExtra(keyObjectDetail, type);
        mContext.startActivity(i);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (informationFragment != null) {
            informationFragment.onActivityResult(requestCode, resultCode, data);
        }
        if (orderDetailFragment != null) {
            orderDetailFragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
