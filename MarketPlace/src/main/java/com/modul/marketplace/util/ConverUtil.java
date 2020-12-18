package com.modul.marketplace.util;

import com.modul.marketplace.model.orderonline.DmService;
import com.modul.marketplace.model.orderonline.DmServiceListOrigin;

import java.util.ArrayList;

public class ConverUtil {

    private static final String TAG = "ConverUtil";

    public static DmService convertServiceListToPromotion(DmServiceListOrigin dmServiceListOrigin) {
        DmService dmService = new DmService();
        dmService.setServiceCode(dmServiceListOrigin.getCode());
        dmService.setServiceName(dmServiceListOrigin.getName());
        dmService.setServiceScope(dmServiceListOrigin.getUnitScope());
        dmService.setServiceImage(dmServiceListOrigin.getImage());
        dmService.setQuantity(dmServiceListOrigin.getQuantity());
        dmService.setOrgPrice(dmServiceListOrigin.getUnitPrice());
        dmService.setSalePrice(dmServiceListOrigin.getMarketPrice());
        if (DmServiceListOrigin.TYPE_COMBO.equals(dmServiceListOrigin.getType())) {
            dmService.setAmount(0);
        }else{
            dmService.setAmount(dmServiceListOrigin.getOriginAmount());
        }
        dmService.setServiceUnit(dmServiceListOrigin.getUnitName());
        dmService.setSKU(dmServiceListOrigin.getSKU());
        dmService.setServiceType(dmServiceListOrigin.getType());
        dmService.setAmountCombo(dmServiceListOrigin.getAmountCombo());
        dmService.setComboId(dmServiceListOrigin.getComboId());
        dmService.setComboDesc(dmServiceListOrigin.getComboDesc());
        dmService.setSupplierUid(dmServiceListOrigin.getSupplierUid());
        dmService.setProductUid(dmServiceListOrigin.getProductUid());
        dmService.setMarketPrice(dmServiceListOrigin.getMarketPrice());
        return dmService;
    }

    public static ArrayList<DmService> convertListService(ArrayList<DmServiceListOrigin> listOrigins) {
        ArrayList<DmService> mDatas = new ArrayList<>();
        if (listOrigins != null) {
            for (DmServiceListOrigin dmServiceListOrigin : listOrigins) {
                mDatas.add(convertServiceListToPromotion(dmServiceListOrigin));
            }
        }
        return mDatas;
    }
}
