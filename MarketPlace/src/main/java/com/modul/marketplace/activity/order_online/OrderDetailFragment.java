package com.modul.marketplace.activity.order_online;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.modul.marketplace.R;
import com.modul.marketplace.activity.BaseFragment;
import com.modul.marketplace.activity.marketplace.MarketPlaceActivity;
import com.modul.marketplace.adapter.orderonline.OrderDetailRecyleAdapter;
import com.modul.marketplace.adapter.orderonline.StatusOrderRecyleAdapter;
import com.modul.marketplace.app.Constants;
import com.modul.marketplace.model.orderonline.DmCallBackMoMo;
import com.modul.marketplace.model.orderonline.DmChargeZaloPayResponse;
import com.modul.marketplace.model.orderonline.DmDeliveryInfo;
import com.modul.marketplace.model.orderonline.DmLocation;
import com.modul.marketplace.model.orderonline.DmOrderOnline;
import com.modul.marketplace.model.orderonline.DmPaymentInfo;
import com.modul.marketplace.model.orderonline.DmQRCode;
import com.modul.marketplace.model.orderonline.DmService;
import com.modul.marketplace.model.orderonline.DmServiceListOrigin;
import com.modul.marketplace.model.orderonline.DmStatusOrder;
import com.modul.marketplace.paser.orderonline.RestDmOrderOnline;
import com.modul.marketplace.paser.orderonline.RestDmQRCode;
import com.modul.marketplace.restful.ApiRequest;
import com.modul.marketplace.restful.WSRestFull;
import com.modul.marketplace.util.FormatNumberUtil;
import com.modul.marketplace.util.Log;
import com.modul.marketplace.util.ToastUtil;
import com.modul.marketplace.util.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import vn.momo.momo_partner.AppMoMoLib;

import static com.modul.marketplace.app.Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK;
import static com.modul.marketplace.app.Constants.BROADCAST.BROAD_MARKET_ORDER_HERMES;
import static com.modul.marketplace.app.Constants.BROADCAST.HERMES_ORDER_CALLBACK;
import static com.modul.marketplace.app.Constants.BROADCAST.HERMES_ORDER_ZALO_CALLBACK;
import static com.modul.marketplace.app.Constants.BROADCAST.REFRESH;
import static com.modul.marketplace.util.Utilities.sendBoardLib;
import static com.modul.marketplace.util.Utilities.sendBoardLibString;
//import vn.zalopay.listener.ZaloPayListener;
//import vn.zalopay.sdk.ZaloPayErrorCode;
//import vn.zalopay.sdk.ZaloPaySDK;

public class OrderDetailFragment extends BaseFragment {

    public static final String TYPE_ORDER_HISTORY = "TYPE_ORDER_HISTORY";
    public static final String TYPE_CREATE_ORDER = "TYPE_CREATE_ORDER";
    public static final int REQUEST_CODE_PICK_LOCATION = 1001;

    private View v;
    private ImageView btn_back;
    private TextView tvHeader;
    private LinearLayout mLayoutInvoice;
    private TextView mAdress;
    private TextView infoName;
    private TextView infoPhone;
    private TextView nameBussiness;
    private TextView mailBussiness;
    private TextView mPaymentmethod;
    private TextView taxCode;
    private TextView adressBussiness;
    private RecyclerView mListDetail;
    private SwipeRefreshLayout mSwipRefreshLayout;
    private ArrayList<DmService> details = new ArrayList<>();
    private OrderDetailRecyleAdapter mAdapter;
    private TextView mPayment;
    private TextView mProvisional;
    private TextView mTotalAmount;
    private TextView mVoucherAmount;
    private RecyclerView mRecycleStatus;
    private ArrayList<DmStatusOrder> mListStatus = new ArrayList<>();
    private StatusOrderRecyleAdapter mAdapterStatus;
    private String orderCode = "";
    private String type = "";
    private TextView mStatusCancel;
    private LinearLayout mLayoutNoteInven;
    private TextView mEstimateTime;
    private TextView mInvenNote;
    private TextView mCustomerNote;
    private LinearLayout mLayoutPaymentMethod;
    private LinearLayout mLayoutAmountPayment;
    private TextView mAmountPaymentMethod;
    private TextView mTitlePayment;
    private View mLayoutSuccess;
    private DmOrderOnline mDmOrderOnline = null;
    private TextView btnSuccess;
    private boolean checkPayment = false;
    private RadioButton mMomo;
    private RadioButton mZalo;
    private String typePayment = "";
    private String tranID = "";


    protected int getItemLayout() {
        return R.layout.fragment_orderonline_detail;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        btn_back = v.findViewById(R.id.back);
        tvHeader = v.findViewById(R.id.header_text);
        mLayoutInvoice = v.findViewById(R.id.linearlayout_invoice);
        mAdress = v.findViewById(R.id.adress);
        infoName = v.findViewById(R.id.info_name);
        infoPhone = v.findViewById(R.id.info_phone_number);
        nameBussiness = v.findViewById(R.id.name_bussiness);
        mailBussiness = v.findViewById(R.id.email_bussiness);
        taxCode = v.findViewById(R.id.tax);
        adressBussiness = v.findViewById(R.id.adress_bussiness);
        mPayment = v.findViewById(R.id.payment);
        mProvisional = v.findViewById(R.id.provisional);
        mTotalAmount = v.findViewById(R.id.total_amount);
        mRecycleStatus = v.findViewById(R.id.list_status);
        mRecycleStatus.setLayoutManager(new LinearLayoutManager(mActivity));
        mListDetail = v.findViewById(R.id.list_detail);
        mListDetail.setLayoutManager(new LinearLayoutManager(mActivity));
        mVoucherAmount = v.findViewById(R.id.voucher_amount);
        mStatusCancel = v.findViewById(R.id.status_cancel);
        mLayoutNoteInven = v.findViewById(R.id.layout_note_inventory);
        mEstimateTime = v.findViewById(R.id.time_estimate_time);
        mInvenNote = v.findViewById(R.id.inven_note);
        mCustomerNote = v.findViewById(R.id.customer_note);
        mLayoutPaymentMethod = v.findViewById(R.id.layout_payment_method);
        mLayoutAmountPayment = v.findViewById(R.id.layout_amount_payment);
        mAmountPaymentMethod = v.findViewById(R.id.amount_payment_method);
        mTitlePayment = v.findViewById(R.id.title_payment);
        mLayoutSuccess = v.findViewById(R.id.layout_success);
        btnSuccess = v.findViewById(R.id.success);
        mZalo = v.findViewById(R.id.zalopay);
        mMomo = v.findViewById(R.id.momo);
        mPaymentmethod = v.findViewById(R.id.mPaymentmethod);
        mSwipRefreshLayout = v.findViewById(R.id.mSwipRefreshLayout);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity.registerReceiver(onNotice, new IntentFilter(BROAD_MARKET_ORDER_HERMES));
        initAdapterStatus();
        initAdapter();
        initData();
        initClick();
    }

    private void initClick() {
        btn_back.setOnClickListener(v -> {
            if (TYPE_CREATE_ORDER.equals(type)) {
                mCartBussiness.OrderOnlineCartClear();
                Utilities.sendBoard(getContext(),Constants.BROADCAST.BROAD_MAKETPLACE,Constants.BROADCAST.BACK);
                Utilities.sendBoard(getContext(),Constants.BROADCAST.BROAD_CART,Constants.BROADCAST.BACK);
                Utilities.sendBoard(getContext(),Constants.BROADCAST.BROAD_INFOMATION,Constants.BROADCAST.BACK);
                Intent intent = new Intent(getContext(), MarketPlaceActivity.class);
                startActivity(intent);
//                sendBoardLib(getContext(),BROAD_MANAGER_HOME_CALLBACK,HERMES_ORDER_CALLBACK);
                mActivity.finish();
            } else {
                mActivity.onBackPressed();
            }
        });
        mPayment.setOnClickListener(v1 -> {
            if (TextUtils.isEmpty(typePayment)) {
                ToastUtil.makeText(mActivity, getString(R.string.mess_check_payment));
                return;
            }

            Utilities.sendBoardCounlyLib(getContext(), Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.ORDER_HERMES_PRODUCT);
            if (DmOrderOnline.MOMO.equals(typePayment)) {
                tranID = mDmOrderOnline.getOrderCode() + "-" + System.currentTimeMillis();
                requestPaymentMoMo(tranID, mDmOrderOnline.getOrderCode(), (int) mDmOrderOnline.getAmount());
            } else {
                checkOrderPayment(mDmOrderOnline);
            }
        });
        btnSuccess.setOnClickListener(v1 -> {
            mLayoutSuccess.setVisibility(View.GONE);
        });
        mPaymentmethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choicePaymentmethod();
            }
        });
        mSwipRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!TextUtils.isEmpty(orderCode)) {
                    getOrderDetail(orderCode);
                }
            }
        });

//        CompoundButton.OnCheckedChangeListener listenerRadio
//                = (compoundButton, b) -> {
//            if (b) {
//                int id = compoundButton.getId();
//                if (id == R.id.zalopay) {
//                    typePayment = DmOrderOnline.ZALOPAY;
//                } else if (id == R.id.momo) {
//                    typePayment = DmOrderOnline.MOMO;
//                }
//            }
//        };
//        mZalo.setOnCheckedChangeListener(listenerRadio);
//        mMomo.setOnCheckedChangeListener(listenerRadio);
    }

    private void choicePaymentmethod() {
        PopupMenu popupMenu = new PopupMenu(getContext(), mPaymentmethod, Gravity.RIGHT);

//        popupMenu.getMenu().add(0, 0, 0, DmOrderOnline.ZALOPAY);
        popupMenu.getMenu().add(0, 1, 1, DmOrderOnline.MOMO);

        popupMenu.setOnMenuItemClickListener(item -> {
            typePayment = item.getTitle().toString();
            if(item.getTitle().toString() == DmOrderOnline.MOMO){
                mPaymentmethod.setText("Ví Momo");
            }
            return false;
        });
        popupMenu.show();
    }

    private void apiZaloPaymentCreate(DmOrderOnline dmOrderOnline, String orderCode) {
        showProgressHub(mActivity);

        ApiRequest<RestDmQRCode> callback = new ApiRequest<>();
        callback.setCallBack(mApiHermes.apiZaloPaymentCreate(dmOrderOnline.getCompanyId(), dmOrderOnline.getStoreId(), dmOrderOnline.getBrandId(), typePayment, dmOrderOnline.getAmount(), dmOrderOnline.getOrderCode(), ""),
                response -> {
                    onResponseZaloPayment(response.getData());
                }, error -> {
                    onResponseZaloPayment(null);
                    error.printStackTrace();
                    ToastUtil.makeText(mActivity, getString(R.string.error_network2));
                });
    }

    private void onResponseZaloPayment(DmQRCode dmQRCode) {
        if (dmQRCode != null) {
            if (checkPayment == false) {
                checkPayment = true;
            }
            sendBoardLibString(getContext(),BROAD_MANAGER_HOME_CALLBACK,HERMES_ORDER_ZALO_CALLBACK,new Gson().toJson(dmQRCode));

//            ZaloPaySDK.getInstance().payOrder(
//                    getActivity(), dmQRCode.getZptranstoken(), new MyZaloPayListener(new MyZaloPayListener.OnCallBack() {
//                        @Override
//                        public void onSuccess() {
//                            if (mDmOrderOnline != null) {
//                                checkOrderPayment(mDmOrderOnline);
//                            }
//                        }
//
//                        @Override
//                        public void onError() {
//                            ToastUtil.makeText(mActivity, getString(R.string.payment_error));
//                        }
//                    })
//            );
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismissProgressHub();
                }
            }, 2000);
        } else {
            dismissProgressHub();
        }
    }

    private void requestPaymentMoMo(String orderID, String orderCode, int amount) {
        AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.PAYMENT);
        AppMoMoLib.getInstance().setActionType(AppMoMoLib.ACTION_TYPE.GET_TOKEN);
        Map<String, Object> eventValue = new HashMap<>();
        //client Required
        eventValue.put("merchantname", DmOrderOnline.MERCHANTNAME); //Tên đối tác. được đăng ký tại https://business.momo.vn. VD: Google, Apple, Tiki , CGV Cinemas
        eventValue.put("merchantcode", DmOrderOnline.MERCHANTCODE); //Mã đối tác, được cung cấp bởi MoMo tại https://business.momo.vn
        eventValue.put("amount", amount); //Kiểu integer
        eventValue.put("orderId", orderID); //uniqueue id cho BillId, giá trị duy nhất cho mỗi BILL
        eventValue.put("orderLabel", orderCode); //gán nhãn
        eventValue.put("extra", "");
        AppMoMoLib.getInstance().requestMoMoCallBack(mActivity, eventValue);
    }

    private void checkOrderPayment(DmOrderOnline dmOrderOnline) {
        showProgressHub(mActivity);
        ApiRequest<RestDmOrderOnline> callback = new ApiRequest<>();
        callback.setCallBack(mApiHermes.apiOrderCheckPayment(dmOrderOnline.getCompanyId(), dmOrderOnline.getStoreId(), dmOrderOnline.getBrandId(), typePayment, dmOrderOnline.getOrderCode()),
                response -> {
                    onResponseOrderPayment(response.getData());
                }, error -> {
                    onResponseOrderPayment(null);
                    error.printStackTrace();
                    ToastUtil.makeText(mActivity, getString(R.string.error_network2));
                });
    }

    private void onResponseOrderPayment(DmOrderOnline dmOrderOnline) {
        dismissProgressHub();
        if (dmOrderOnline != null) {
            DmChargeZaloPayResponse reponseZalo = dmOrderOnline.getDmChargeZaloPayResponse();
            if (reponseZalo != null) {
                String odCode = "";
                if (DmChargeZaloPayResponse.STATUS_ERROR_ORDER_CODE.equals(reponseZalo.getReturncode())) {
                    odCode = mDmOrderOnline.getOrderCode() + "-" + System.currentTimeMillis();
                } else {
                    odCode = mDmOrderOnline.getOrderCode();
                }
                if (checkPayment == false) {
                    apiZaloPaymentCreate(mDmOrderOnline, odCode);
                } else {
                    if (!TextUtils.isEmpty(orderCode)) {
                        getOrderDetail(orderCode);
                    }
                }
            }
        } else {
            mLayoutSuccess.setVisibility(View.GONE);
        }
    }

    private void initData() {
        tvHeader.setText(getResources().getString(R.string.don_hang) + " #" + orderCode);
        if (!TextUtils.isEmpty(orderCode)) {
            getOrderDetail(orderCode);
        }
    }

    private void getOrderDetail(String orderCode) {
        if(mSwipRefreshLayout != null){
            mSwipRefreshLayout.setRefreshing(true);
        }

        ApiRequest<RestDmOrderOnline> callback = new ApiRequest<>();
        callback.setCallBack(mApiHermes.apiOrderHistory(orderCode),
                response -> {
                    onResponseOrderDetail(response.getData());
                }, error -> {
                    onResponseOrderDetail(null);
                    error.printStackTrace();
                    ToastUtil.makeText(mActivity, getString(R.string.error_network2));
                });
    }

    private void onResponseOrderDetail(DmOrderOnline dmOrderOnline) {
        if(mSwipRefreshLayout != null){
            mSwipRefreshLayout.setRefreshing(false);
        }
        if (dmOrderOnline != null) {
            mDmOrderOnline = dmOrderOnline;
            loadDataStatus();
            String status = dmOrderOnline.getStatus();
            Log.i(TAG, "Status: " + status + " " + mListStatus.size());
//            if (DmStatusOrder.TYPE_CANCELED.equals(status)) {
//                mRecycleStatus.setVisibility(View.GONE);
//            mRecycleStatus.setVisibility(View.GONE);
//            } else {
//                mStatusCancel.setVisibility(View.GONE);
//                mRecycleStatus.setVisibility(View.VISIBLE);
//            }
            mStatusCancel.setText(dmOrderOnline.OrderHistoryHermesStatusInfo(getContext()));
            if (DmStatusOrder.TYPE_PAYING.equals(status)) {
                mPaymentmethod.setEnabled(true);
                mPaymentmethod.setTextColor(ContextCompat.getColor(getContext(), R.color.mainColor));
                mPaymentmethod.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_payment_select, 0);
//                mLayoutPaymentMethod.setVisibility(View.VISIBLE);
//                mLayoutAmountPayment.setVisibility(View.GONE);
                mPayment.setVisibility(View.VISIBLE);
            } else {
                mPayment.setVisibility(View.GONE);
//                mLayoutPaymentMethod.setVisibility(View.GONE);
                mPaymentmethod.setEnabled(false);
                mPaymentmethod.setTextColor(ContextCompat.getColor(getContext(), R.color.gray80));
                mPaymentmethod.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_payment_unselect, 0);
//                mLayoutAmountPayment.setVisibility(View.VISIBLE);
                DmPaymentInfo dmPaymentInfo = dmOrderOnline.getDmPaymentInfo();
                if(dmPaymentInfo != null){
                    mStatusCancel.setVisibility(View.VISIBLE);
                    if (DmOrderOnline.ZALOPAY.equals(dmPaymentInfo.getMethod())) {
                        mPaymentmethod.setText(DmPaymentInfo.NAME_ZALOPAY);
//                    mTitlePayment.setText(getString(R.string.da_thanh_toan) + " (" + DmPaymentInfo.NAME_ZALOPAY + ")");
                    } else {
                        mPaymentmethod.setText("Ví Momo");
//                    mTitlePayment.setText(getString(R.string.da_thanh_toan) + " (" + DmPaymentInfo.NAME_MOMO + ")");
                    }
                    mAmountPaymentMethod.setText(FormatNumberUtil.formatCurrency(dmPaymentInfo.getPayAmount()));
                }
            }
            int position = 0;
            for (int i = 0; i < mListStatus.size(); i++) {
                if (status.equals(mListStatus.get(i).getType())) {
                    position = i;
                    mListStatus.get(i).setCorectPosition(true);
                } else {
                    mListStatus.get(i).setCorectPosition(false);
                }
            }

            for (int i = 0; i <= position; i++) {
                mListStatus.get(i).setCheckedStatus(true);
            }

            details.clear();
            ArrayList<DmService> list = dmOrderOnline.getDetails();
            if (list != null) {
                details.addAll(list);
                setAmountTypeCombo(list, details);
            }
            Log.i(TAG, "List service: " + details.size());

            DmDeliveryInfo dmDeliveryInfo = dmOrderOnline.getDmDeliveryInfo();
            infoName.setText(dmDeliveryInfo.getReceiverName());
            infoPhone.setText(dmDeliveryInfo.getReceiverPhone());
            mAdress.setText(dmDeliveryInfo.getAddress());
            mCustomerNote.setText(dmOrderOnline.getCustomerNote());
            if (dmDeliveryInfo.getEstimateShipped() != null) {
//                mLayoutNoteInven.setVisibility(View.VISIBLE);
                mEstimateTime.setText(""+dmDeliveryInfo.getEstimateShipped() + " " + getString(R.string.date));
                mInvenNote.setText(dmDeliveryInfo.getNote());
            } else {
//                mLayoutNoteInven.setVisibility(View.GONE);
            }

            mProvisional.setText(FormatNumberUtil.formatCurrency(dmOrderOnline.getAmount() + dmOrderOnline.getDiscountAmount()));
            mVoucherAmount.setText("-" + FormatNumberUtil.formatCurrency(dmOrderOnline.getDiscountAmount()));
            mTotalAmount.setText(FormatNumberUtil.formatCurrency(dmOrderOnline.getAmount()));

            checkRequestInvoice(dmOrderOnline);
            mAdapterStatus.notifyDataSetChanged();
            mAdapter.notifyDataSetChanged();
        }
    }

    private void setAmountTypeCombo(ArrayList<DmService> list, ArrayList<DmService> mData) {
        if (list != null) {
            double amount = 0;
            for (DmService dmService : mData) {
                if (DmServiceListOrigin.TYPE_COMBO.equals(dmService.getServiceType())) {
                    for (DmService item : list) {
                        if (dmService.getComboId().equals(item.getComboId())) {
                            amount += item.getOrgPrice() * (item.getQuantity() / dmService.getQuantity());
                        }
                    }
                    dmService.setAmountCombo(amount);
                }

            }
        }
    }

    private void checkRequestInvoice(DmOrderOnline dmOrderOnline) {
        if (dmOrderOnline.getRequestInvoice() == 1) {
            mLayoutInvoice.setVisibility(View.VISIBLE);
            nameBussiness.setText(dmOrderOnline.getContactName());
            mailBussiness.setText(dmOrderOnline.getCompanyTaxEmail());
            taxCode.setText(dmOrderOnline.getCompanyTaxCode());
            adressBussiness.setText(dmOrderOnline.getCompanyFullAddress());
        } else {
            mLayoutInvoice.setVisibility(View.GONE);
        }
    }


    private void initAdapter() {
        mAdapter = new OrderDetailRecyleAdapter(mActivity, details);
        mListDetail.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }


    private void initAdapterStatus() {
        mAdapterStatus = new StatusOrderRecyleAdapter(mActivity, mListStatus);
        mRecycleStatus.setAdapter(mAdapterStatus);
        mAdapterStatus.notifyDataSetChanged();
    }

    private void loadDataStatus() {
        mListStatus.clear();
        mListStatus.add(new DmStatusOrder(DmStatusOrder.TYPE_PAYING, getString(R.string.cho_thanh_toan), true, true, false, true));
        if(mDmOrderOnline.getStatus().equals(DmStatusOrder.TYPE_CANCELED)){
            if(mDmOrderOnline.getDmPaymentInfo() != null){
                mListStatus.add(new DmStatusOrder(DmStatusOrder.TYPE_PENDING, getString(R.string.da_thanh_toan), false, false, false, false));
                mListStatus.add(new DmStatusOrder(DmStatusOrder.TYPE_CANCELED, getString(R.string.da_huy), false, false, true, false));
            }else{
                mListStatus.add(new DmStatusOrder(DmStatusOrder.TYPE_CANCELED, getString(R.string.da_huy), false, false, true, false));
            }
        }else{
            mListStatus.add(new DmStatusOrder(DmStatusOrder.TYPE_PENDING, getString(R.string.da_thanh_toan), false, false, false, false));
            mListStatus.add(new DmStatusOrder(DmStatusOrder.TYPE_RECEIVED, getString(R.string.da_tiep_nhan), false, false, false, false));
            mListStatus.add(new DmStatusOrder(DmStatusOrder.TYPE_PROCESSED, getString(R.string.da_xu_ly_Xong), false, false, false, false));
            mListStatus.add(new DmStatusOrder(DmStatusOrder.TYPE_SHIPPING, getString(R.string.dnag_giao_hang), false, false, false, false));
            mListStatus.add(new DmStatusOrder(DmStatusOrder.TYPE_COMPLETED, getString(R.string.hoan_thanh), false, false, true, false));
        }
        if(mAdapterStatus != null)
        mAdapterStatus.notifyDataSetChanged();
    }


    public static OrderDetailFragment newInstance(String orderCode, String type) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        fragment.orderCode = orderCode;
        fragment.type = type;
        return fragment;

    }

//    private static class MyZaloPayListener implements ZaloPayListener {
//        private final String TAG = "MyZaloPayListener";
//        private MyZaloPayListener.OnCallBack onCallBack;
//
//        public MyZaloPayListener(MyZaloPayListener.OnCallBack onCallBack) {
//            this.onCallBack = onCallBack;
//
//        }
//
//        @Override
//        public void onPaymentSucceeded(String transactionId, String zpTranstoken) {
//            Log.d(TAG, "onSuccess: On successful with transactionId: " + transactionId + "- zpTransToken: " + zpTranstoken);
//            onCallBack.onSuccess();
//        }
//
//        @Override
//        public void onPaymentError(ZaloPayErrorCode errorCode, int paymentErrorCode, String zpTranstoken) {
//            Log.d(TAG, String.format("onPaymentError: payment error with [error: %s, paymentError: %d], zptranstoken: %s", errorCode, paymentErrorCode, zpTranstoken));
//            onCallBack.onError();
//        }
//
//        public interface OnCallBack {
//            void onSuccess();
//
//            void onError();
//        }
//    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_LOCATION) {
            if (resultCode == Activity.RESULT_OK) {
                DmLocation dmLocation = (DmLocation) data.getSerializableExtra(Constants.KEY_DATA);

//                Log.d(TAG, "DmLocation " + dmLocation);
                String ad = dmLocation.getAddress();
                mAdress.setText(ad);

            }
        } else if (requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    if (data.getIntExtra("status", -1) == 0) {
                        String token = data.getStringExtra("data"); //Token response
                        String phoneNumber = data.getStringExtra("phonenumber");

                        String env = data.getStringExtra("env");
                        if (env == null) {
                            env = "app";
                        }

                        if (token != null && !token.equals("")) {
                            Log.i(TAG, "Success 1.1");
                            DmCallBackMoMo dmCallBackMoMo = new DmCallBackMoMo();
                            dmCallBackMoMo.setData(token);
                            dmCallBackMoMo.setTransId(tranID);
                            dmCallBackMoMo.setPhoneNumber(phoneNumber);
                            dmCallBackMoMo.setAmount((int) mDmOrderOnline.getAmount());
                            checkOrderPaymentMomo(dmCallBackMoMo);
                        } else {
                        }
                    } else if (data.getIntExtra("status", -1) == 1) {
                        //TOKEN FAIL
                        String message = data.getStringExtra("message") != null ? data.getStringExtra("message") : "Thất bại";
                    } else if (data.getIntExtra("status", -1) == 2) {
                        //TOKEN FAIL
                    } else {
                        //TOKEN FAIL
                    }
                } else {

                }
            } else {
            }
        } else {
//            ZaloPaySDK.getInstance().onActivityResult(requestCode, resultCode, data);
        }
    }

    private void checkOrderPaymentMomo(DmCallBackMoMo dmCallBackMoMo) {
        showProgressHub(mActivity);

        ApiRequest<DmCallBackMoMo> callback = new ApiRequest<>();
        callback.setCallBack(mApiHermes.apiPaymentMoMo(dmCallBackMoMo),
                response -> {
                    onResponseCallBackMoMo();
                }, error -> {
                    dismissProgressHub();
                    error.printStackTrace();
                    ToastUtil.makeText(mActivity, getString(R.string.error_network2));
                });
    }

    private void onResponseCallBackMoMo() {
        dismissProgressHub();
        if (!TextUtils.isEmpty(orderCode)) {
            getOrderDetail(orderCode);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       mActivity.unregisterReceiver(onNotice);
    }

    private final BroadcastReceiver onNotice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!TextUtils.isEmpty(orderCode)) {
                getOrderDetail(orderCode);
            }
        }
    };
}
