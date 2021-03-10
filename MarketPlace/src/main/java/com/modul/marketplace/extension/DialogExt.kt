package com.modul.marketplace.extension

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.modul.marketplace.R
import com.modul.marketplace.app.ApplicationMarketPlace
import com.modul.marketplace.model.orderonline.DmService

object DialogUtil {

    fun showAlert(
        context: Context,
        textMessage: Any,
        textTitle: Any? = null,
        textOk: Any = context.getString(R.string.btn_ok),
        textCancel: Any? = null,
        cancelable: Boolean = true,
        okListener: (() -> Unit)? = null,
        cancelListener: (() -> Unit)? = null,
    ) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(R.color.black)
        dialog.setContentView(R.layout.dialog_confirmation)

        dialog.setCancelable(cancelable)

        val lblTitle = dialog.findViewById<TextView>(R.id.lblTitle)
        val lblMessage = dialog.findViewById<TextView>(R.id.lblMessage)
        val edNote = dialog.findViewById<EditText>(R.id.edNote)
        val edNote2 = dialog.findViewById<EditText>(R.id.edNote2)
        val inputLayoutNote = dialog.findViewById<TextInputLayout>(R.id.userIDTextInputLayout)
        val inputLayoutNote2 = dialog.findViewById<TextInputLayout>(R.id.userIDTextInputLayout2)
        val lblOk = dialog.findViewById<TextView>(R.id.lblOk)
        val lblCancel = dialog.findViewById<TextView>(R.id.lblCancel)

        textTitle?.let {
            lblTitle.visible()
            lblTitle.text = when (it) {
                is String -> it
                is CharSequence -> it
                is Int -> context.getString(it)
                else -> ""
            }
        }

        lblMessage.text = when (textMessage) {
            is String -> textMessage
            is CharSequence -> textMessage
            is Int -> context.getString(textMessage)
            else -> ""
        }

        lblOk.text = when (textOk) {
            is String -> textOk
            is CharSequence -> textOk
            is Int -> context.getString(textOk)
            else -> ""
        }
        lblOk.setOnClickListener {
            if (dialog.isShowing) {
                dialog.dismiss()
                okListener?.let { it1 -> it1() }
            }
        }

        val strCancel = when (textCancel) {
            is String -> textCancel
            is CharSequence -> textCancel
            is Int -> context.getString(textCancel)
            else -> ""
        }

        if (strCancel.isNullOrEmpty() || strCancel.isNullOrBlank()) {
            lblCancel.visibility = View.GONE
        } else {
            lblCancel.text = strCancel
            lblCancel.setOnClickListener {
                if (dialog.isShowing) {
                    dialog.dismiss()
                    cancelListener?.invoke()
                }
            }
        }

        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    fun showAlertEdit(
        context: Context,
        dmService: DmService,
        textOk: Any = context.getString(R.string.cart_edit),
        okListener: ((DmService) -> Unit)? = null
    ) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(R.color.black)
        dialog.setContentView(R.layout.dialog_alert_edit)

        dialog.setCancelable(true)

        val mImage = dialog.findViewById<SimpleDraweeView>(R.id.image)
        val title = dialog.findViewById<TextView>(R.id.title)
        val mQuantity = dialog.findViewById<TextView>(R.id.mQuantity)
        val mMinus = dialog.findViewById<ImageView>(R.id.mMinus)
        val mPlus = dialog.findViewById<ImageView>(R.id.mPlus)
        val mEdit = dialog.findViewById<MaterialButton>(R.id.mEdit)
//        val mDelete = dialog.findViewById<MaterialButton>(R.id.mDelete)

        var data = DmService(dmService)
        Glide.with(context).load(data.serviceImage).into(mImage)

        title.text = data.serviceName
        mQuantity.text = StringExt.convertNumberToString(data.quantity)

        mEdit.text = when (textOk) {
            is String -> textOk
            is CharSequence -> textOk
            is Int -> context.getString(textOk)
            else -> ""
        }

        mMinus.setOnClickListener {
            var quantity = mQuantity.text.toString().toInt()
            if (quantity > 0) {
                mQuantity.text = "" + quantity.minus(1)
            }

            if(quantity == 0){
                mEdit.setText(ApplicationMarketPlace.instance.getString(R.string.context_menu_xoa))
                mEdit.setTextAppearance(context,R.style.UnelevatedButtonCancel2)
//                mDelete.visible()
//                mEdit.gone()
            }else{
                mEdit.setText(ApplicationMarketPlace.instance.getString(R.string.cart_edit))
                mEdit.setTextAppearance(context,R.style.UnelevatedButton)
//                mDelete.gone()
//                mEdit.visible()
            }
            data.quantity = mQuantity.text.toString().toDouble()
        }
        mPlus.setOnClickListener {
            var quantity = mQuantity.text.toString().toInt()
            mQuantity.text = "" + quantity.plus(1)
            data.quantity = mQuantity.text.toString().toDouble()
        }
        mEdit.setOnClickListener {
            if (dialog.isShowing) {
                dialog.dismiss()
                okListener?.let { it1 -> it1(data) }
            }
        }
//        mDelete.setOnClickListener {
//            if (dialog.isShowing) {
//                dialog.dismiss()
//                okListener?.let { it1 -> it1(data) }
//            }
//        }

        if (!dialog.isShowing) {
            dialog.show()
        }
    }
}