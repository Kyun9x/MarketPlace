package com.modul.marketplace.model.orderonline;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class DmService implements Serializable {


    @SerializedName("serviceCode")
    private String serviceCode = "";

    @SerializedName("serviceName")
    private String serviceName = "";

    @SerializedName("orgPrice")
    private double orgPrice;

    @SerializedName("salePrice")
    private double salePrice;

    @SerializedName("serviceScope")
    private String serviceScope = "";

    @SerializedName("quantity")
    private double quantity;

    @SerializedName("amount")
    private double amount;

    @SerializedName("marketPrice")
    private Double marketPrice;

    @SerializedName("discountAmount")
    private double discountAmount;

    @SerializedName("promotionName")
    private String promotionName;

    @SerializedName("promotion")
    private DmPromotion dmPromotion;

    @SerializedName("details")
    private ArrayList<DmService> details;


    @SerializedName("productCode")
    private String productCode = "FABI";


    @SerializedName("serviceType")
    private String serviceType;


    @SerializedName("serviceUnit")
    private String serviceUnit;

    @SerializedName("serviceDesc")
    private String serviceDesc;

    @SerializedName("comboDesc")
    private String comboDesc;

    @SerializedName("supplier_uid")
    private String supplierUid;

    @SerializedName("product_uid")
    private String productUid;

    @SerializedName("serviceImage")
    private String serviceImage;


    @SerializedName("SKU")
    private String SKU;

    @SerializedName("warrantyPeriod")
    private double warrantyPeriod;


    @SerializedName("isGift")
    private int isGift;

    @SerializedName("brandId")
    private String brandId;

    @SerializedName("storeName")
    private String storeName;


    @SerializedName("storeId")
    private Integer storeId;

    @SerializedName("position")
    private int position;

    @SerializedName("comboId")
    private String comboId;

    @SerializedName("amountCombo")
    private double amountCombo;

    public double getAmountCombo() {
        return amountCombo;
    }

    public void setAmountCombo(double amountCombo) {
        this.amountCombo = amountCombo;
    }

    public String getComboId() {
        return comboId;
    }

    public void setComboId(String comboId) {
        this.comboId = comboId;
    }

    public int getPosition() {
        return position;
    }

    public String getComboDesc() {
        return comboDesc;
    }

    public Double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(Double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public void setComboDesc(String comboDesc) {
        this.comboDesc = comboDesc;
    }

    public String getSupplierUid() {
        return supplierUid;
    }

    public void setSupplierUid(String supplierUid) {
        this.supplierUid = supplierUid;
    }

    public String getProductUid() {
        return productUid;
    }

    public void setProductUid(String productUid) {
        this.productUid = productUid;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ArrayList<DmService> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<DmService> details) {
        this.details = details;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceUnit() {
        return serviceUnit;
    }

    public void setServiceUnit(String serviceUnit) {
        this.serviceUnit = serviceUnit;
    }

    public String getServiceDesc() {
        return serviceDesc;
    }

    public void setServiceDesc(String serviceDesc) {
        this.serviceDesc = serviceDesc;
    }

    public String getServiceImage() {
        return serviceImage;
    }

    public void setServiceImage(String serviceImage) {
        this.serviceImage = serviceImage;
    }

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public double getWarrantyPeriod() {
        return warrantyPeriod;
    }

    public void setWarrantyPeriod(double warrantyPeriod) {
        this.warrantyPeriod = warrantyPeriod;
    }

    public int getIsGift() {
        return isGift;
    }

    public void setIsGift(int isGift) {
        this.isGift = isGift;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getPromotionName() {
        if (dmPromotion != null) {
            promotionName = dmPromotion.getName();
        }
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public DmPromotion getDmPromotion() {
        return dmPromotion;
    }

    public void setDmPromotion(DmPromotion dmPromotion) {
        this.dmPromotion = dmPromotion;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public double getOrgPrice() {
        return orgPrice;
    }

    public void setOrgPrice(double orgPrice) {
        this.orgPrice = orgPrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public String getServiceScope() {
        return serviceScope;
    }

    public void setServiceScope(String serviceScope) {
        this.serviceScope = serviceScope;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public boolean isCombo() {
        return !TextUtils.isEmpty(comboId);
    }

    public DmService() {
    }

    public DmService(DmService m) {
        this.serviceCode = m.getServiceCode();
        this.serviceName = m.getServiceName();
        this.orgPrice = m.getOrgPrice();
        this.salePrice = m.getSalePrice();
        this.serviceScope = m.getServiceScope();
        this.quantity = m.getQuantity();
        this.amount = m.getAmount();
        this.discountAmount = m.getDiscountAmount();
        this.promotionName = m.getPromotionName();
        this.dmPromotion = m.getDmPromotion();
        this.details = m.getDetails();
        this.productCode = m.getProductCode();
        this.serviceType = m.getServiceType();
        this.serviceUnit = m.getServiceUnit();
        this.serviceDesc = m.getServiceDesc();
        this.comboDesc = m.getComboDesc();
        this.supplierUid = m.getSupplierUid();
        this.productUid = m.getProductUid();
        this.serviceImage = m.getServiceImage();
        this.SKU = m.getSKU();
        this.warrantyPeriod = m.getWarrantyPeriod();
        this.isGift = m.getIsGift();
        this.brandId = m.getBrandId();
        this.storeName = m.getStoreName();
        this.storeId = m.getStoreId();
        this.position = m.getPosition();
        this.comboId = m.getComboId();
        this.amountCombo = m.getAmountCombo();
    }
}
