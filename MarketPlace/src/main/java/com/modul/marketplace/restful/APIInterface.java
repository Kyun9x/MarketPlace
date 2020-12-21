package com.modul.marketplace.restful;


import com.modul.marketplace.model.marketplace.AddressModelData;
import com.modul.marketplace.model.marketplace.AhamoveSearchData;
import com.modul.marketplace.model.marketplace.ArticlesCountModelData;
import com.modul.marketplace.model.marketplace.ArticlesImageModelData;
import com.modul.marketplace.model.marketplace.ArticlesModel;
import com.modul.marketplace.model.marketplace.ArticlesModelData;
import com.modul.marketplace.model.marketplace.ArticlesModelDataObject;
import com.modul.marketplace.model.marketplace.FeedbackModel;
import com.modul.marketplace.model.marketplace.FeedbackModelData;
import com.modul.marketplace.model.marketplace.LocationModel;
import com.modul.marketplace.model.marketplace.LocationModelData;
import com.modul.marketplace.model.marketplace.LocationModelDataObject;
import com.modul.marketplace.model.marketplace.NvlModelData;
import com.modul.marketplace.model.marketplace.NvlOnlineModel;
import com.modul.marketplace.model.marketplace.NvlOnlineModelData;
import com.modul.marketplace.model.marketplace.NvlOnlineModelDataList;
import com.modul.marketplace.model.marketplace.TagsModelData;
import com.modul.marketplace.model.orderonline.DmCallBackMoMo;
import com.modul.marketplace.model.orderonline.DmOrderOnline;
import com.modul.marketplace.paser.orderonline.RestAllDmCheckAutoPromotion;
import com.modul.marketplace.paser.orderonline.RestAllDmCheckVoucher;
import com.modul.marketplace.paser.orderonline.RestAllDmCheckVoucherToServer;
import com.modul.marketplace.paser.orderonline.RestAllDmLocate;
import com.modul.marketplace.paser.orderonline.RestAllDmServiceListOrigin;
import com.modul.marketplace.paser.orderonline.RestDmHistoryOrderOnline;
import com.modul.marketplace.paser.orderonline.RestDmOrderOnline;
import com.modul.marketplace.paser.orderonline.RestDmQRCode;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface APIInterface {
    @POST("partner/check_voucher")
    Call<RestAllDmCheckVoucher> apiCheckVoucher(@Body RestAllDmCheckVoucherToServer json);

    @GET("partner/service/menu")
    Call<RestAllDmServiceListOrigin> apiOrderOnline_ServiceList(@Query("productCode") String productCode);

    @POST("partner/check_auto_promo")
    Call<RestAllDmCheckAutoPromotion> apiCheckAutoPromotion(@Body RestAllDmCheckAutoPromotion json);

    @GET("partner/online_order/list")
    Call<RestDmHistoryOrderOnline> apiOrderHistoryHermes(@Query("companyId") String company_id, @Query("page") int page, @Query("numPerPage") int numPerPage);

    @GET("partner/online_order/detail")
    Call<RestDmOrderOnline> apiOrderHistory(@Query("orderCode") String orderCode);

    @POST("partner/online_order/create")
    Call<RestDmOrderOnline> apiOrderOnline(@Body DmOrderOnline json);

    @GET("payment/check")
    Call<RestDmOrderOnline> apiOrderCheckPayment(@Query("companyId") String companyId, @Query("storeId") String storeId, @Query("brandId") String brandId, @Query("paymentMethod") String paymentMethod, @Query("tranId") String tranId);

    @GET("payment/createOrder")
    Call<RestDmQRCode> apiZaloPaymentCreate(@Query("companyId") String companyId, @Query("storeId") String storeId, @Query("brandId") String brandId, @Query("paymentMethod") String paymentMethod, @Query("amount") Double amount, @Query("tranId") String tranId, @Query("desc") String desc);

    @POST("payment/momo/payapp")
    Call<DmCallBackMoMo> apiPaymentMoMo(@Body DmCallBackMoMo json);

    @GET("common/locate")
    Call<RestAllDmLocate> apiLocate();

    @GET("cities")
    Call<AddressModelData> apiSCMCity(@Query("results_per_page") int results);

    @GET("products")
    Call<NvlModelData> apiSCMProducts(@Query("active") int active, @Query("cityId") String cityId,
                                           @Query("page") int page,
    @Query("results_per_page") int results_per_page);

    @GET("tags")
    Call<TagsModelData> apiSCMTags();

    @GET("locations")
    Call<LocationModelData> apiSCMLocation(@Query("active") int active, @Query("company_id") String companyId, @Query("brand_ids") String brand_ids, @Query("user_id") String user_id);

    @POST("locations")
    Call<LocationModelDataObject> apiSCMLocationCreate(@Body LocationModel json);

    @PUT("locations")
    Call<LocationModelDataObject> apiSCMLocationEdit(@Body String json);

    @DELETE("locations")
    Call<LocationModelDataObject> apiSCMLocationDelete(@Query("uid") String uid);

    @GET("districts")
    Call<AddressModelData> apiSCMDistricts(@Query("city_uid") String city_uid);

    @GET("precincts")
    Call<AddressModelData> apiSCMPrecincts(@Query("city_uid") String city_uid, @Query("district_uid") String district_uid);

    @POST("feedback")
    Call<FeedbackModelData> apiSCMFeedback(@Body FeedbackModel json);

    @GET("articles")
    Call<ArticlesModelData> apiSCMArticles(@Query("city_uid") String city_uid,
                                           @Query("company_id") String companyId,
                                           @Query("brand_ids") String BrandId,
                                           @Query("page") int page,
                                           @Query("results_per_page") int results_per_page);

    @GET("articles")
    Call<ArticlesModelDataObject> apiSCMArticlesDetail(@Query("uid") String uId);

    @GET("articles")
    Call<ArticlesModelData> apiSCMArticlesByStatus(@Query("status") String status,
                                                   @Query("brand_ids") String brand_ids,
                                                   @Query("company_id") String company_id,
                                                   @Query("author_id") String author_id,
                                                   @Query("data_type") String data_type,
                                                   @Query("type") String myArticle,
                                                   @Query("results_per_page") int results_per_page);

    @POST("articles")
    Call<ArticlesModelDataObject> apiSCMArticlesCreate(@Body ArticlesModel json);

    @PUT("articles")
    Call<ArticlesModelDataObject> apiSCMArticlesEdit(@Body String json);

    @GET("articles/count-status")
    Call<ArticlesCountModelData> apiSCMArticlesCount(@Query("company_id") String company_id, @Query("author_id") String author_id);

    @POST("invoices")
    Call<NvlOnlineModelData> apiSCMInvoices(@Body NvlOnlineModel json);

    @GET("invoices")
    Call<NvlOnlineModelDataList> apiSCMInvoicesHistory(@Query("company_id") String company_id, @Query("customer_id") String customer_id, @Query("page") int page, @Query("results_per_page") int results_per_page);

    @GET("invoices")
    Call<NvlOnlineModelData> apiSCMInvoicesHistoryDetail(@Query("company_id") String company_id, @Query("uid") String uid);

    @GET("https://ep.ahamove.com/places/ipos/v1/autocomplete")
    Call<AhamoveSearchData> apiAhamoveSearchLocation(@Query("text") String text);

    @Multipart
    @POST("upload/image?thumb_size=200, 200")
    Call<ArticlesImageModelData> apiSCMUpload(@Part MultipartBody.Part file);
}