package com.modul.marketplace.activity.marketplace

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.GridLayoutManager
import com.asksira.bsimagepicker.BSImagePicker
import com.bumptech.glide.Glide
import com.modul.marketplace.R
import com.modul.marketplace.activity.BaseActivity
import com.modul.marketplace.adapter.marketplace.ImageOrderAdapter
import com.modul.marketplace.app.Constants
import com.modul.marketplace.app.Constants.ArticlesStatus.CANCELED
import com.modul.marketplace.app.Constants.ArticlesStatus.SOLD
import com.modul.marketplace.extension.*
import com.modul.marketplace.model.marketplace.*
import com.modul.marketplace.model.orderonline.ImageOrderModel
import com.modul.marketplace.restful.ApiRequest
import com.modul.marketplace.util.DateTimeUtil
import com.modul.marketplace.util.Log
import com.modul.marketplace.util.ToastUtil
import com.modul.marketplace.util.Utilities
import kotlinx.android.synthetic.main.activity_article_create.*
import kotlinx.android.synthetic.main.include_header2.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*


class ArticleCreateActivity : BaseActivity(), BSImagePicker.OnSingleImageSelectedListener,
        BSImagePicker.OnMultiImageSelectedListener, BSImagePicker.ImageLoaderDelegate {

    private lateinit var mAdapterImageOrder: ImageOrderAdapter
    private val mResultImageOrder: ArrayList<ImageOrderModel> = ArrayList()
    private var areaId: String? = null
    private var tagsList = ArrayList<String>()
    private var idArticles: String? = null
    private var articlesModel: ArticlesModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_create)
        initAdapter()
        initExtra()
        initData()
        initClick()
    }

    private fun initExtra() {
        idArticles = intent?.extras?.let {
            if (it.containsKey(Constants.OBJECT)) {
                it.getSerializable(Constants.OBJECT) as String?
            } else {
                null
            }
        }

        idArticles?.run {
            apiDetail(this)
        } ?: run {
            mCreate.text = getString(R.string.dang_tin)
            val content = SpannableString(getString(R.string.policy))
            content.setSpan(UnderlineSpan(), 20, 50, 0)
            content.setSpan(ForegroundColorSpan(Color.BLUE), 19, 51, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            mLbPolicy.text = content
            mPolicy.visible()
            mLbPolicy.visible()
            mResultImageOrder.add(ImageOrderModel(img_url_thumb = null))
            mAdapterImageOrder.notifyDataSetChanged()
        }
    }

    private fun apiDetail(id: String?) {
        showProgressHub(this)

        val callback: ApiRequest<ArticlesModelDataObject> = ApiRequest()
        callback.setCallBack(mApiSCM?.apiSCMArticlesDetail(id.toString()),
                { response -> apiDetailDone(response.data) }) { error ->
            apiDetailDone(null)
            error.printStackTrace()
            ToastUtil.makeText(this, getString(R.string.error_network2))
        }
    }

    private fun apiDetailDone(data: ArticlesModel?) {
        dismissProgressHub()
        data?.run {
            articlesModel = this
            city_uid?.run {
                areaId = this
            }
            listTag?.run {
                if (size > 0) {
                    listTag?.first()?.id?.run {
                        tagsList.add(this)
                    }
                }
            }

            mImage_urls.forEach {
                var imageOrderModel = ImageOrderModel()
                imageOrderModel.img_url = it.url
                imageOrderModel.img_url_thumb = it.url_thumb
                imageOrderModel.status = status
                mResultImageOrder.add(imageOrderModel)
            }

            mTieuDe.setText(StringExt.isTextEmpty(mTitle))
            price.setText(StringExt.isTextEmpty(mPrice))
            mDesc.setText(StringExt.isTextEmpty(mContent))
            mNguoiDang.setText(StringExt.isTextEmpty(mAuthor_name))
            mSdt.setText(StringExt.isTextEmpty(mAuthor_phone))
            listTag?.run {
                if (size > 0) {
                    mTag.setText(StringExt.isTextEmpty(listTag?.first()?.tag_name))
                }
            }
            mKhuVuc.setText(StringExt.isTextEmpty(city?.city_name))

            mTieuDe.isEnabled = false
            price.isEnabled = false
            mDesc.isEnabled = false
            mTag.isEnabled = false
            mKhuVuc.isEnabled = false
            mSdt.isEnabled = false
            mNguoiDang.isEnabled = false

            when (status) {
                Constants.ArticlesStatus.PENDING -> {
                    mResultImageOrder.add(ImageOrderModel(img_url_thumb = null))
                    mHuyTin.visible()
                    mCapNhat.visible()
                    mCreate.gone()
                    mAnTin.gone()
                    mDaBan.gone()
                    mTaoLaiTin.gone()

                    mTieuDe.isEnabled = true
                    price.isEnabled = true
                    mDesc.isEnabled = true
                    mTag.isEnabled = true
                    mKhuVuc.isEnabled = true
                    mSdt.isEnabled = true
                    mNguoiDang.isEnabled = true
                }
                Constants.ArticlesStatus.CONFIRMED -> {
                    mAnTin.visible()
                    mDaBan.visible()
                    mHuyTin.gone()
                    mCapNhat.gone()
                    mTaoLaiTin.gone()
                    mCreate.gone()
                    if (active == 0) {
                        mAnTin.text = getString(R.string.hien_tin)
                    } else {
                        mAnTin.text = getString(R.string.an_tin)
                    }
                    if (expire_time != null) {
                        DateTimeUtil.compareTimeGet(DateTimeUtil.convertTimeStampToDate(expire_time, Constants.Date.Format.YYYY_MM_DD_HH_MM_SS_2)) { month, day, hour, minus, second ->
                            if (second > 0) {
                                mAnTin.gone()
                                mDaBan.gone()
                                mTaoLaiTin.visible()
                            }
                        }
                    }
                }
                Constants.ArticlesStatus.CANCELED -> {
                    mAnTin.gone()
                    mDaBan.gone()
                    mHuyTin.gone()
                    mCapNhat.gone()
                    mCreate.gone()
                    mTaoLaiTin.gone()
                }
                Constants.ArticlesStatus.SOLD -> {
                    mAnTin.gone()
                    mDaBan.gone()
                    mHuyTin.gone()
                    mCapNhat.gone()
                    mCreate.gone()
                    mTaoLaiTin.gone()
                }
            }
        }
        mAdapterImageOrder.notifyDataSetChanged()
    }

    private fun initAdapter() {
        mRecyclerViewImage.apply {
            layoutManager = GridLayoutManager(this@ArticleCreateActivity, 3)
            mAdapterImageOrder = ImageOrderAdapter(mResultImageOrder, itemClickListener = { it, potision ->
                if(idArticles == null) {
                    openSelectImage()
                }
            }, deleteClickListener = {
                mResultImageOrder.remove(it)
                mAdapterImageOrder.notifyDataSetChanged()
            })
            adapter = mAdapterImageOrder
        }

        mAdapterImageOrder.notifyDataSetChanged()
    }

    private fun openSelectImage() {
        val pickerDialog =
                BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
                        .setMaximumDisplayingImages(Integer.MAX_VALUE)
                        .setMinimumMultiSelectCount(1)
                        .setMaximumMultiSelectCount(0)
                        .build()
        pickerDialog.show(supportFragmentManager, "picker")
    }

    private fun initClick() {
        mIconLeft.setOnClickListener { onBackPressed() }
        success.setOnClickListener { checkSuccess() }
        mCreate.setOnClickListener { create() }
        mKhuVuc.setOnClickListener { choiceKhuVuc() }
        mTag.setOnClickListener { tags() }
        mHuyTin.setOnClickListener { apiHuyTin() }
        mCapNhat.setOnClickListener { capnhat() }
        mAnTin.setOnClickListener { apiAnTin() }
        mDaBan.setOnClickListener { apiDaBan() }
        mTaoLaiTin.setOnClickListener { apiTaoLaiTin() }
        mLbPolicy.setOnClickListener { Utilities.openWeb(this,"https://iposvn.gitbook.io/suppy-chain/thoa-thuan-dich-vu/thoa-thuan-dich-vu") }
    }

    private fun apiTaoLaiTin() {
        var newArticlesModel = ArticlesModel(id = articlesModel?.id, type = "renew")
        apiEdit(newArticlesModel)
        Utilities.sendBoardCounlyLib(baseContext, Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.RENEW_ARTICLE)
    }

    private fun apiHuyTin() {
        var newArticlesModel = ArticlesModel(id = articlesModel?.id, status = CANCELED)
        apiEdit(newArticlesModel)
        Utilities.sendBoardCounlyLib(baseContext, Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.CANCLE_ARTICLE)
    }

    private fun apiAnTin() {
        if (articlesModel?.active == 0) {
            var newArticlesModel = ArticlesModel(id = articlesModel?.id, active = 1)
            apiEdit(newArticlesModel)
            Utilities.sendBoardCounlyLib(baseContext, Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.ACTIVE_ARTICLE)
        } else {
            var newArticlesModel = ArticlesModel(id = articlesModel?.id, active = 0)
            apiEdit(newArticlesModel)
            Utilities.sendBoardCounlyLib(baseContext, Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.DEACTIVE_ARTICLE)
        }
    }

    private fun apiDaBan() {
        Utilities.sendBoardCounlyLib(baseContext, Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.MARK_SOLD_ARTICLE)
        var newArticlesModel = ArticlesModel(id = articlesModel?.id, status = SOLD)
        apiEdit(newArticlesModel)
    }

    private fun checkSuccess() {
        onBackPressed()
    }

    private fun capnhat() {
        Utilities.sendBoardCounlyLib(baseContext, Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.EDIT_ARTICLE)

        if (TextUtils.isEmpty(mTieuDe.text.toString())) {
            ToastUtil.makeText(this, getString(R.string.articles_title_valid))
            return
        }
        if (TextUtils.isEmpty(price.text.toString())) {
            ToastUtil.makeText(this, getString(R.string.articles_price_valid))
            return
        }
        if (TextUtils.isEmpty(mKhuVuc.text.toString())) {
            ToastUtil.makeText(this, getString(R.string.articles_khuvuc_valid))
            return
        }
        if (TextUtils.isEmpty(mNguoiDang.text.toString())) {
            ToastUtil.makeText(this, getString(R.string.articles_nguoidang_valid))
            return
        }
        if (TextUtils.isEmpty(mSdt.text.toString())) {
            ToastUtil.makeText(this, getString(R.string.articles_sdt_valid))
            return
        }
        if (TextUtils.isEmpty(mDesc.text.toString())) {
            ToastUtil.makeText(this, getString(R.string.decs_valid))
            return
        }

        var expectedValue = 0.0
        if (!TextUtils.isEmpty(price.text.toString())) {
            expectedValue =
                    price.text.toString().replace(".", "").replace(",", "").toDouble()
        }
        var newImageChoice = ArrayList<ArticlesImageModel>()
        mResultImageOrder.forEachIndexed { index, imageOrderModel ->
            imageOrderModel.img_url_thumb?.run {
                var newArticles = ArticlesImageModel(url = imageOrderModel.img_url, url_thumb = imageOrderModel.img_url_thumb)
                newImageChoice.add(newArticles)
            }
        }
        var newArticlesModel = ArticlesModel(mTitle = mTieuDe.text.toString(),
                id = articlesModel?.id,
                mPrice = expectedValue,
                city_uid = areaId,
                tags_uid = tagsList,
                mAuthor_name = mNguoiDang.text.toString(),
                mAuthor_phone = mSdt.text.toString(),
                mImage_urls = newImageChoice,
                author_id = mCartBussiness.userId,
                brand_id = mCartBussiness.getListBrandId(),
                company_id = mCartBussiness.companyId,
                mContent = mDesc.text.toString())
        apiEdit(newArticlesModel)
    }

    private fun tags() {
        showProgressHub(this)

        val callback: ApiRequest<TagsModelData> = ApiRequest()
        callback.setCallBack(mApiSCM?.apiSCMTags(),
                { response -> tagsDone(response.data) }) { error ->
            tagsDone(null)
            error.printStackTrace()
            ToastUtil.makeText(this, getString(R.string.error_network2))
        }
    }

    private fun tagsDone(data: ArrayList<TagsModel>?) {
        dismissProgressHub()
        data?.run {
            val popupMenu = PopupMenu(this@ArticleCreateActivity, mTag, Gravity.RIGHT)

            forEachIndexed { index, team ->
                popupMenu.menu.add(0, index.plus(1), index.plus(1), team.tag_name)
            }

            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                tagsList.clear()
                val cityName = item.title.toString()
                mTag.setText(cityName)
                tagsList.add(data[item.itemId.minus(1)].id.toString())
                false
            }
            popupMenu.show()
        }
    }

    private fun choiceKhuVuc() {
        showProgressHub(this)
        val callback: ApiRequest<AddressModelData> = ApiRequest()
        callback.setCallBack(mApiSCM?.apiSCMCity(1000),
                { response -> areaDone(response.data) }) { error ->
            areaDone(null)
            error.printStackTrace()
            ToastUtil.makeText(this, getString(R.string.error_network2))
        }
    }

    private fun areaDone(data: ArrayList<AddressModel>?) {
        dismissProgressHub()
        data?.run {
            val popupMenu = PopupMenu(this@ArticleCreateActivity, mKhuVuc, Gravity.RIGHT)

            popupMenu.menu.add(0, 0, 0, getString(R.string.toan_quoc))
            forEachIndexed { index, team ->
                popupMenu.menu.add(0, index.plus(1), index.plus(1), team.city_name)
            }

            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                if (item.itemId == 0) {
                    areaId = null
                } else {
                    areaId = data[item.itemId.minus(1)].id.toString()
                }
                mKhuVuc.setText(item.title.toString())
                false
            }
            popupMenu.show()
        }
    }

    private fun create() {
        if (TextUtils.isEmpty(mTieuDe.text.toString())) {
            ToastUtil.makeText(this, getString(R.string.articles_title_valid))
            return
        }
        if (TextUtils.isEmpty(price.text.toString())) {
            ToastUtil.makeText(this, getString(R.string.articles_price_valid))
            return
        }
        if (TextUtils.isEmpty(mKhuVuc.text.toString())) {
            ToastUtil.makeText(this, getString(R.string.articles_khuvuc_valid))
            return
        }
        if (TextUtils.isEmpty(mNguoiDang.text.toString())) {
            ToastUtil.makeText(this, getString(R.string.articles_nguoidang_valid))
            return
        }
        if (TextUtils.isEmpty(mSdt.text.toString())) {
            ToastUtil.makeText(this, getString(R.string.articles_sdt_valid))
            return
        }
        if (TextUtils.isEmpty(mDesc.text.toString())) {
            ToastUtil.makeText(this, getString(R.string.decs_valid))
            return
        }
        if (mPolicy.visibility == View.VISIBLE) {
            if (!mPolicy.isChecked) {
                ToastUtil.makeText(this, getString(R.string.polilcy_valid))
                return
            }
        }

        var expectedValue = 0.0
        if (!TextUtils.isEmpty(price.text.toString())) {
            expectedValue =
                    price.text.toString().replace(".", "").replace(",", "").toDouble()
        }
        var newImageChoice = ArrayList<ArticlesImageModel>()
        mResultImageOrder.forEachIndexed { index, imageOrderModel ->
            imageOrderModel.img_url_thumb?.run {
                var newArticles = ArticlesImageModel(url = imageOrderModel.img_url, url_thumb = imageOrderModel.img_url_thumb)
                newImageChoice.add(newArticles)
            }
        }

        if (newImageChoice.size == 0) {
            ToastUtil.makeText(this, getString(R.string.image_valid))
            return
        }
        var newArticlesModel = ArticlesModel(mTitle = mTieuDe.text.toString(),
                mPrice = expectedValue,
                city_uid = areaId,
                tags_uid = tagsList,
                mAuthor_name = mNguoiDang.text.toString(),
                mAuthor_phone = mSdt.text.toString(),
                mImage_urls = newImageChoice,
                author_id = mCartBussiness.userId,
                brand_id = mCartBussiness.getListBrandId(),
                company_id = mCartBussiness.companyId,
                mContent = mDesc.text.toString())
        apiCreate(newArticlesModel)
        Utilities.sendBoardCounlyLib(baseContext, Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.CREATE_ARTICLE)
    }

    private fun apiCreate(articlesModel: ArticlesModel) {
        showProgressHub(this)
        val callback: ApiRequest<ArticlesModelDataObject> = ApiRequest()
        callback.setCallBack(mApiSCM?.apiSCMArticlesCreate(articlesModel),
                { response -> createDone(response.data) }) { error ->
            createDone(null)
            error.printStackTrace()
            ToastUtil.makeText(this, getString(R.string.error_network2))
        }
    }

    private fun apiEdit(articlesModel: ArticlesModel) {
        showProgressHub(this)
        val callback: ApiRequest<ArticlesModelDataObject> = ApiRequest()
        callback.setCallBack(mApiSCM?.apiSCMArticlesEdit(articlesModel.toJson()),
                { response -> editDone(response.data) }) { error ->
            editDone(null)
            error.printStackTrace()
            ToastUtil.makeText(this, getString(R.string.error_network2))
        }
    }

    private fun createDone(data: ArticlesModel?) {
        dismissProgressHub()
        data?.run {
            id?.run {
                text_success.text = getString(R.string.tao_raovat_thanhcong)
                text_success2.text = getString(R.string.create_content_articles)
                layout_success.visible()
            } ?: run {
                showToast(getString(R.string.error_network2))
            }
        }
    }

    private fun editDone(data: ArticlesModel?) {
        dismissProgressHub()
        data?.run {
            when (status) {
                Constants.ArticlesStatus.PENDING -> {
                }
                Constants.ArticlesStatus.CONFIRMED -> {
                    if (active == 0) {
                        text_success.text = getString(R.string.an_tin_success)
                        text_success2.text = getString(R.string.an_tin_lb)
                    } else {
                        text_success.text = getString(R.string.mo_tin_success)
                        text_success2.text = getString(R.string.mo_tin_lb)
                    }
                }
                Constants.ArticlesStatus.SOLD -> {
                    text_success.text = getString(R.string.da_ban_success)
                    text_success2.text = getString(R.string.da_ban_lb)
                }
                Constants.ArticlesStatus.CANCELED -> {
                    text_success.text = getString(R.string.huytin_success)
                    text_success2.text = getString(R.string.lb_huytin)
                }
            }
            layout_success.visible()
        }
    }

    private fun initData() {
        showStatusBar(statusColor = true, color = R.color.grayF8)
        mlbTitle.text = getString(R.string.create_articles)
        text_success.text = getString(R.string.create_articles)
        price.addTextChangedListener(NumberTextWatcher(price))

    }

    override fun onSingleImageSelected(uri: Uri?, tag: String?) {
        uri?.run {
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, this)

            if (bitmap.getWidth() >= bitmap.getHeight()){

                bitmap = Bitmap.createBitmap(
                        bitmap,
                        bitmap.getWidth()/2 - bitmap.getHeight()/2,
                        0,
                        bitmap.getHeight(),
                        bitmap.getHeight()
                );

            }else{

                bitmap = Bitmap.createBitmap(
                        bitmap,
                        0,
                        bitmap.getHeight()/2 - bitmap.getWidth()/2,
                        bitmap.getWidth(),
                        bitmap.getWidth()
                );
            }

//            var bitmapConvert = Bitmap.createScaledBitmap(bitmap, 1000, 750, true)

            showProgressHub(this@ArticleCreateActivity)

            val f = File(cacheDir, "image.jpg")
            try {
                f.createNewFile()
                val bm = bitmap
                val bos = ByteArrayOutputStream()
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                val bitmapdata = bos.toByteArray()
                var fos: FileOutputStream? = null
                fos = FileOutputStream(f)
                fos.write(bitmapdata)
                fos.flush()
                fos.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val reqFile: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), f)
            val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", f.name, reqFile)

            val callback: ApiRequest<ArticlesImageModelData> = ApiRequest()
            callback.setCallBack(mApiSCM?.apiSCMUpload(body),
                    { response -> uploadDone(response.data) }) { error ->
                error.printStackTrace()
                dismissProgressHub()
            }
        }
    }

    override fun onMultiImageSelected(uriList: MutableList<Uri>?, tag: String?) {

    }

    private fun uploadDone(data: UploadImageModel?) {
        dismissProgressHub()
        data?.run {
            Log.e("data", "data: " + data.toJson())
            mResultImageOrder.add(ImageOrderModel(img_url_thumb = img_url_thumb, img_url = img_url, status = Constants.ArticlesStatus.PENDING))

            with(mResultImageOrder.iterator()) {
                forEach {
                    if (it.img_url_thumb == null) {
                        remove()
                    }
                }
            }
        }
        runOnUiThread {
            mResultImageOrder.add(ImageOrderModel(img_url_thumb = null))
            mAdapterImageOrder.notifyDataSetChanged()
        }
    }

    override fun loadImage(imageUri: Uri?, ivImage: ImageView?) {
        Glide.with(this).load(imageUri).into(ivImage!!)
    }
}