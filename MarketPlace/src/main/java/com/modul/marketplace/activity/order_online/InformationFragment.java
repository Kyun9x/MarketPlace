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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.modul.marketplace.R;
import com.modul.marketplace.activity.BaseFragment;
import com.modul.marketplace.activity.CateActivity;
import com.modul.marketplace.activity.marketplace.NvlHistoryDetailActivity;
import com.modul.marketplace.activity.marketplace.SelectAddressOrderNvlActivity;
import com.modul.marketplace.app.Constants;
import com.modul.marketplace.model.marketplace.NvlOnlineModel;
import com.modul.marketplace.model.marketplace.NvlOnlineModelData;
import com.modul.marketplace.model.orderonline.DmBrand;
import com.modul.marketplace.model.orderonline.DmCallBackMoMo;
import com.modul.marketplace.model.orderonline.DmOrderOnline;
import com.modul.marketplace.model.orderonline.DmQRCode;
import com.modul.marketplace.model.orderonline.DmService;
import com.modul.marketplace.model.orderonline.DmStore;
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
//import vn.zalopay.listener.ZaloPayListener;
//import vn.zalopay.sdk.ZaloPayErrorCode;
//import vn.zalopay.sdk.ZaloPaySDK;

import static com.modul.marketplace.app.Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK;
import static com.modul.marketplace.app.Constants.BROADCAST.HERMES_ORDER_CALLBACK;
import static com.modul.marketplace.app.Constants.BROADCAST.HERMES_ORDER_ZALO_CALLBACK;
import static com.modul.marketplace.app.Constants.FABI;
import static com.modul.marketplace.util.Utilities.sendBoardLib;
import static com.modul.marketplace.util.Utilities.sendBoardLibString;

public class InformationFragment extends BaseFragment {

    public static final int REQUEST_CODE_PICK_LOCATION = 1001;

    private View v;
    private ImageView btn_back;
    private TextView tvHeader;
    private TextView mAddress;
    private TextView text_success;
    private LinearLayout mLayoutInvoice;
    private CheckBox mCheckInvoice;
    private TextInputEditText mBrand;
    private TextInputEditText mStore;
    private ArrayList<DmBrand> mBrands = new ArrayList<>();
    private ArrayList<DmStore> mStores = new ArrayList<>();
    private String brandId = "";
    private String storeId = "";
    private TextInputEditText infoName;
    private TextInputEditText infoPhone;
    private TextInputEditText nameBussiness;
    private TextInputEditText mailBussiness;
    private TextInputEditText taxCode;
    private TextInputEditText adressBussiness;
    private TextInputEditText mNote;
    private TextView mPayment;
    private TextView mProvisional;
    private TextView mTotalAmount;
    private TextView mVoucherAmount;
    private TextView mAddAddress;
    private View mLayoutSuccess;
    private TextView btnSuccess;
    private RadioButton mMomo;
    private RadioButton mZalo;
    private RadioButton mCash;
    private TextInputLayout mTIPStore;
    private String typePayment = "";
    private String tranID = "";

    protected int getItemLayout() {
        return R.layout.fragment_information;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        btn_back = v.findViewById(R.id.mIconLeft);
        tvHeader = v.findViewById(R.id.mlbTitle);
        mLayoutInvoice = v.findViewById(R.id.linearlayout_invoice);
        mCheckInvoice = v.findViewById(R.id.checkbox);
        mBrand = v.findViewById(R.id.brand);
        mStore = v.findViewById(R.id.store);
        infoName = v.findViewById(R.id.info_name);
        infoPhone = v.findViewById(R.id.info_phone_number);
        nameBussiness = v.findViewById(R.id.name_bussiness);
        mailBussiness = v.findViewById(R.id.email_bussiness);
        taxCode = v.findViewById(R.id.tax);
        adressBussiness = v.findViewById(R.id.adress_bussiness);
        mPayment = v.findViewById(R.id.payment);
        mProvisional = v.findViewById(R.id.provisional);
        mTotalAmount = v.findViewById(R.id.total_amount);
        mLayoutSuccess = v.findViewById(R.id.layout_success);
        btnSuccess = v.findViewById(R.id.success);
        mVoucherAmount = v.findViewById(R.id.voucher_amount);
        mNote = v.findViewById(R.id.note);
        mZalo = v.findViewById(R.id.zalopay);
        mCash = v.findViewById(R.id.cod);
        mMomo = v.findViewById(R.id.momo);
        mAddAddress = v.findViewById(R.id.mAddAddress);
        mAddress = v.findViewById(R.id.mAddress);
        text_success = v.findViewById(R.id.text_success);
        mTIPStore = v.findViewById(R.id.mTIPStore);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
            LocalBroadcastManager.getInstance(getContext())
                    .registerReceiver(onNotice, new IntentFilter(Constants.BROADCAST.BROAD_INFOMATION));
        initData();
        initClick();
    }

    private void initClick() {
        mAddAddress.setOnClickListener(v -> choiceAddress());
        mAddress.setOnClickListener(v -> choiceAddress());
        btn_back.setOnClickListener(v -> mActivity.onBackPressed());
        mCheckInvoice.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mLayoutInvoice.setVisibility(View.VISIBLE);
            } else {
                mLayoutInvoice.setVisibility(View.GONE);
            }
        });
        mBrand.setOnClickListener(v1 -> showDialogBrand());
        mStore.setOnClickListener(v1 -> {
            Log.i(TAG, "Brand: " + brandId + " Store " + mStores.size());
            if (!TextUtils.isEmpty(brandId)) {
                showDialogStores();
            }
        });
        mPayment.setOnClickListener(v1 -> {
//            mCartBussiness.getOrder();
            if (!TextUtils.isEmpty(mCartBussiness.getOrder().getOrderCode())) {
                if (DmOrderOnline.ZALOPAY.equals(typePayment)) {
                    apiZaloPaymentCreate(mCartBussiness.getOrder());
                } else {
                    tranID = mCartBussiness.getOrder().getOrderCode() + "-" + System.currentTimeMillis();
                    requestPaymentMoMo(tranID, mCartBussiness.getOrder().getOrderCode(), (int) mCartBussiness.getOrder().getAmount());
                }
            } else {
                checkData();
            }
        });
        btnSuccess.setOnClickListener(v1 -> {
            Log.e("mCartBussiness.getOrder().getOrderType()", "aaaa : " + mCartBussiness.getOrder().getOrderType());
            if (mCartBussiness.getOrder().getInvoice_id() != null) {
                Intent mIntent = new Intent(getContext(), NvlHistoryDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.OBJECT, mCartBussiness.getOrder().getInvoice_id());
                bundle.putString(Constants.DATA, OrderDetailFragment.TYPE_CREATE_ORDER);
                mIntent.putExtras(bundle);
                startActivity(mIntent);
            } else {
                CateActivity.gotoOrderDetailFragment(mActivity, mCartBussiness.getOrder().getOrderCode(), OrderDetailFragment.TYPE_CREATE_ORDER);
            }
        });

        CompoundButton.OnCheckedChangeListener listenerRadio = (compoundButton, b) -> {
            if (b) {
                int id = compoundButton.getId();
                if (id == R.id.zalopay) {
                    typePayment = DmOrderOnline.ZALOPAY;
                } else if (id == R.id.momo) {
                    typePayment = DmOrderOnline.MOMO;
                } else if (id == R.id.cod) {
                    typePayment = DmOrderOnline.COD;
                }
            }
        };
        mZalo.setOnCheckedChangeListener(listenerRadio);
        mMomo.setOnCheckedChangeListener(listenerRadio);
        mCash.setOnCheckedChangeListener(listenerRadio);
    }

    private void choiceAddress() {
        Intent i = new Intent(getContext(), SelectAddressOrderNvlActivity.class);
        mActivity.startActivityForResult(i, REQUEST_CODE_PICK_LOCATION);
    }

    private void showDialogBrand() {
        PopupMenu popupMenu = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            popupMenu = new PopupMenu(mActivity, mBrand, Gravity.END);
        }

        for (DmBrand dmBrand : mBrands) {
            popupMenu.getMenu().add(dmBrand.getBrandName());
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            String brandName = String.valueOf(item.getTitle());
            mBrand.setText(brandName);
            selectUIDbrand(brandName);
            return false;
        });
        popupMenu.show();
    }

    private void selectUIDbrand(String title) {
        for (DmBrand dmBrand : mBrands) {
            if (title.equals(dmBrand.getBrandName())) {
                brandId = dmBrand.getBrandId();
                mStore.setText("");
                storeId = "";
                mStores.clear();
                break;
            }
        }
        loadStore(brandId);
    }

    private void showDialogStores() {
        PopupMenu popupMenu = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            popupMenu = new PopupMenu(mActivity, mStore, Gravity.END);
        }

        for (DmStore dmStore : mStores) {
            popupMenu.getMenu().add(dmStore.getStoreName());
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            String storeName = String.valueOf(item.getTitle());
            mStore.setText(storeName);
            selectUIdStore(storeName);
            return false;
        });
        popupMenu.show();
    }

    protected void loadStore(String brandId) {
        mStores.clear();
        if (mCartBussiness.getMListBrands() != null && mCartBussiness.getMListBrands().size() > 0) {
            for (DmBrand brand : mCartBussiness.getMListBrands()) {
                if (brand.getBrandId().equals(brandId)) {
                    mStores.addAll(brand.getStores());
                }
            }
        }
    }

    private void selectUIdStore(String title) {
        for (DmStore dmStore : mStores) {
            if (title.equals(dmStore.getStoreName())) {
                storeId = dmStore.getStoreID();
                break;
            }
        }
    }


    private void initData() {
        tvHeader.setText(getResources().getString(R.string.information));
        if (mCartBussiness.getMListBrands() != null) {
            if (mCartBussiness.getMListBrands().size() > 0) {
                mBrands.addAll(mCartBussiness.getMListBrands());
            }
        }

        if (mCartBussiness.getOrder().getOrderType() == Constants.OrderType.OrderNvl) {
            mZalo.setVisibility(View.GONE);
            mMomo.setVisibility(View.GONE);
            mCash.setChecked(true);
            typePayment = DmOrderOnline.COD;
        } else {
            mCash.setVisibility(View.GONE);
            mMomo.setChecked(true);
            typePayment = DmOrderOnline.MOMO;
        }
        validAmountCart();
        refreshAddress();
    }

    private void checkData() {
        //brand, store
        String storeName = mStore.getText().toString();
        if (TextUtils.isEmpty(brandId)) {
            ToastUtil.makeText(mActivity, getString(R.string.brand) + " " + getString(R.string.not_empty));
            return;
        }

        if (TextUtils.isEmpty(storeName) && mCartBussiness.getAppType() == FABI) {
            ToastUtil.makeText(mActivity, getString(R.string.store) + " " + getString(R.string.not_empty));
            return;
        }

        if (TextUtils.isEmpty(infoPhone.getText().toString()) && infoPhone.length() < 9) {
            ToastUtil.makeText(mActivity, getString(R.string.phone_valid));
            return;
        }

        //info bussiness
        String nameB = nameBussiness.getText().toString();
        String emailB = mailBussiness.getText().toString();
        String tax = taxCode.getText().toString();
        String adressB = adressBussiness.getText().toString();

        if (mCheckInvoice.isChecked()) {
            if (TextUtils.isEmpty(nameB)) {
                ToastUtil.makeText(mActivity, getString(R.string.name_bussiness) + " " + getString(R.string.not_empty));
                return;
            }
            if (TextUtils.isEmpty(emailB)) {
                ToastUtil.makeText(mActivity, getString(R.string.email_bussiness) + " " + getString(R.string.not_empty));
                return;
            }
            if (TextUtils.isEmpty(tax)) {
                ToastUtil.makeText(mActivity, getString(R.string.tax_code) + " " + getString(R.string.not_empty));
                return;
            }
            if (TextUtils.isEmpty(adressB)) {
                ToastUtil.makeText(mActivity, getString(R.string.adress_bussiness) + " " + getString(R.string.not_empty));
                return;
            }
        }

        //delivery info
        String namedl = infoName.getText().toString();
        String phonedl = infoPhone.getText().toString();
        String note = mNote.getText().toString();

        if (mCartBussiness.getOrder().getDmDeliveryInfo() == null) {
            ToastUtil.makeText(mActivity, getString(R.string.mess_delivery_info));
            return;
        }

        if (TextUtils.isEmpty(namedl)) {
            ToastUtil.makeText(mActivity, getString(R.string.mess_delivery_info));
            return;
        }
        if (TextUtils.isEmpty(phonedl)) {
            ToastUtil.makeText(mActivity, getString(R.string.mess_delivery_info));
            return;
        }
        if (TextUtils.isEmpty(typePayment)) {
            ToastUtil.makeText(mActivity, getString(R.string.mess_check_payment));
            return;
        }
        mCartBussiness.getOrder().getDmDeliveryInfo().setReceiverName(namedl);
        mCartBussiness.getOrder().getDmDeliveryInfo().setReceiverPhone(phonedl);

        mCartBussiness.getOrder().setContactCompany(nameB);
        mCartBussiness.getOrder().setCustomerName(nameB);
        mCartBussiness.getOrder().setCompanyTaxCode(tax);
        mCartBussiness.getOrder().setCompanyTaxEmail(emailB);
        mCartBussiness.getOrder().setCompanyFullAddress(adressB);
        mCartBussiness.getOrder().setCustomerNote(note);

        if (mCheckInvoice.isChecked()) {
            mCartBussiness.getOrder().setRequestInvoice(1);
        } else {
            mCartBussiness.getOrder().setRequestInvoice(0);
        }

        for (DmService dmService : mCartBussiness.getOrder().getDetails()) {
            if (dmService.getDetails() != null) {
                dmService.setDetails(null);
            }
            dmService.setStoreId(storeId);
            dmService.setStoreName(storeName);
            dmService.setBrandId(brandId);
        }

        mCartBussiness.getOrder().setStoreId(storeId);
        mCartBussiness.getOrder().setStoreName(storeName);
        mCartBussiness.getOrder().setBrandId(brandId);
        if (mCartBussiness.getOrder().getOrderType() == Constants.OrderType.OrderOnline) {
            createOrderOnline(mCartBussiness.convertOrderToJson());
            Utilities.sendBoardCounlyLib(getContext(),Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.ORDER_HERMES_PRODUCT);
        } else {
            createOrderNvl(mCartBussiness.convertNvlToJson());
            Utilities.sendBoardCounlyLib(getContext(),Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.ORDER_SCM_PRODUCT);
        }
    }

    private void createOrderNvl(NvlOnlineModel convertNvlToJson) {
        showProgressHub(mActivity);
        ApiRequest<NvlOnlineModelData> callback = new ApiRequest<>();
        callback.setCallBack(mApiSCM.apiSCMInvoices(convertNvlToJson),
                response -> {
                    onReponseOrderNvl(response);
                }, error -> {
                    onReponseOrderNvl(null);
                    error.printStackTrace();
                    ToastUtil.makeText(mActivity, getString(R.string.error_network2));
                });
    }

    private void onReponseOrderNvl(NvlOnlineModelData response) {
        dismissProgressHub();
        if (response != null) {
            text_success.setText(getString(R.string.tao_don_thanh_cong));
            mCartBussiness.getOrder().setInvoice_id(response.getData().getId());
            mLayoutSuccess.setVisibility(View.VISIBLE);
        }
    }

    private void createOrderOnline(DmOrderOnline dmOrderOnline) {
        showProgressHub(mActivity);
        ApiRequest<RestDmOrderOnline> callback = new ApiRequest<>();
        callback.setCallBack(mApiHermes.apiOrderOnline(dmOrderOnline),
                response -> {
                    onReponseOrderOnline(response.getData());
                }, error -> {
                    onReponseOrderOnline(null);
                    error.printStackTrace();
                    ToastUtil.makeText(mActivity, getString(R.string.error_network2));
                });
    }

    private void onReponseOrderOnline(DmOrderOnline dmOrderOnline) {
        dismissProgressHub();
        if (dmOrderOnline != null) {
            mCartBussiness.setOder(dmOrderOnline);
            if (DmOrderOnline.ZALOPAY.equals(typePayment)) {
                apiZaloPaymentCreate(dmOrderOnline);
            } else {
                tranID = dmOrderOnline.getOrderCode();
                requestPaymentMoMo(tranID, dmOrderOnline.getOrderCode(), (int) dmOrderOnline.getAmount());
            }
        }
    }

    private void apiZaloPaymentCreate(DmOrderOnline dmOrderOnline) {
        showProgressHub(mActivity);
        Log.e("dmOrderOnline", "json: " + new Gson().toJson(dmOrderOnline));

        ApiRequest<RestDmQRCode> callback = new ApiRequest<>();
        callback.setCallBack(mApiHermes.apiZaloPaymentCreate(dmOrderOnline.getCompanyId(), dmOrderOnline.getStoreId(), dmOrderOnline.getBrandId(), typePayment, dmOrderOnline.getAmount(), dmOrderOnline.getOrderCode(), ""),
                response -> {
                    onResponseZaloPaymentCreate(response.getData());
                }, error -> {
                    onResponseZaloPaymentCreate(null);
                    error.printStackTrace();
                    ToastUtil.makeText(mActivity, getString(R.string.error_network2));
                });
    }

    private void onResponseZaloPaymentCreate(DmQRCode dmQRCode) {
        if (dmQRCode != null) {
            sendBoardLibString(getContext(),BROAD_MANAGER_HOME_CALLBACK,HERMES_ORDER_ZALO_CALLBACK,new Gson().toJson(dmQRCode));
//            ZaloPaySDK.getInstance().payOrder(
//                    getActivity(), dmQRCode.getZptranstoken(), new MyZaloPayListener(new MyZaloPayListener.OnCallBack() {
//                        @Override
//                        public void onSuccess() {
//                            if (mCartBussiness.getOrder() != null) {
//                                checkOrderPayment(mCartBussiness.getOrder());
//                            }
//                        }
//
//                        @Override
//                        public void onError() {
//                            ToastUtil.makeText(mActivity, getString(R.string.payment_error));
//                        }
//                    })
//            );
            new Handler().postDelayed(() -> dismissProgressHub(), 2000);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_LOCATION) {
            if (resultCode == Activity.RESULT_OK) {
                refreshAddress();
            }
        } else if (requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO) {
            Log.e("momo:","resultCode: "+ resultCode);
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Log.e("momo:","status: "+ data.getIntExtra("status",-1));
                    if (data.getIntExtra("status", -1) == 0) {
                        String token = data.getStringExtra("data"); //Token response
                        String phoneNumber = data.getStringExtra("phonenumber");

                        String env = data.getStringExtra("env");
                        if (env == null) {
                            env = "app";
                        }

                        if (token != null && !token.equals("")) {
                            Log.i(TAG, "Success 1.1");
                            // TODO: send phoneNumber & token to your server side to process payment with MoMo server
                            // IF Momo topup success, continue to process your order
                            DmCallBackMoMo dmCallBackMoMo = new DmCallBackMoMo();
                            dmCallBackMoMo.setData(token);
                            dmCallBackMoMo.setTransId(tranID);
                            dmCallBackMoMo.setPhoneNumber(phoneNumber);
                            dmCallBackMoMo.setAmount((int) mCartBussiness.getOrder().getAmount());
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
//                mActivity.onBackPressed();
            }
        } else {
//            ZaloPaySDK.getInstance().onActivityResult(requestCode, resultCode, data);
        }
    }

    private void refreshAddress() {
        mAddress.setVisibility(View.GONE);
        if (mCartBussiness.getOrder().getDmDeliveryInfo() != null) {
            if (mCartBussiness.getOrder().getDmDeliveryInfo().getAddress() != null) {
                mAddress.setVisibility(View.VISIBLE);
                mAddress.setText(mCartBussiness.getOrder().getDmDeliveryInfo().getAddress());
            }
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
        mLayoutSuccess.setVisibility(View.VISIBLE);
    }

    private void validAmountCart() {
        if (mCartBussiness.getOrder() != null) {
            mProvisional.setText(FormatNumberUtil.formatCurrency(mCartBussiness.getOrder().getAmount() + mCartBussiness.getOrder().getDiscountAmount()));
            mVoucherAmount.setText("-" + FormatNumberUtil.formatCurrency(mCartBussiness.getOrder().getDiscountAmount()));
            mTotalAmount.setText(FormatNumberUtil.formatCurrency(mCartBussiness.getOrder().getAmount()));
        }
    }

    public static InformationFragment newInstance() {
        InformationFragment fragment = new InformationFragment();
        return fragment;

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
            text_success.setText(getString(R.string.tao_don_thanh_cong_hermes));
            mLayoutSuccess.setVisibility(View.VISIBLE);
        } else {
            mLayoutSuccess.setVisibility(View.GONE);
        }
    }

//    private static class MyZaloPayListener implements ZaloPayListener {
//        private final String TAG = "MyZaloPayListener";
//        private OnCallBack onCallBack;
//
//        public MyZaloPayListener(OnCallBack onCallBack) {
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(onNotice);
    }

    BroadcastReceiver onNotice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String value = intent.getStringExtra("value");
            if(intent.getStringExtra("value") == Constants.BROADCAST.BACK){
                mActivity.finish();
            }
        }
    };
}
