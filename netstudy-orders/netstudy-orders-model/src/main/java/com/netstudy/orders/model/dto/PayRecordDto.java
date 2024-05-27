package com.netstudy.orders.model.dto;

import com.netstudy.orders.model.po.XcPayRecord;
import lombok.Data;
import lombok.ToString;

/**
 * @author Dico
 * @version 1.0
 * @description 支付记录dto
 * @date 2025/5/24 10:21
 */
@Data
@ToString
public class PayRecordDto extends XcPayRecord {

    //二维码
    private String qrcode;

}
