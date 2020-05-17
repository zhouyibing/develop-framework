package com.yipeng.framework.core.json;

import com.yipeng.framework.core.constants.StrPosition;
import lombok.Getter;

/**
 * @author: yibingzhou
 */
public enum SensitiveType {
    //身份证 362201*******22818
    ID_CARD(StrPosition.CENTER, true,7),
    //护照 E85****34
    PASSPORT(StrPosition.CENTER, true,4),
    //手机 150*****315
    MOBILE(StrPosition.CENTER, true, 5),
    //银行卡 6214********7315
    BANK_CARD(StrPosition.CENTER, true,8),
    //姓名 张**
    NAME(StrPosition.LEFT, false,1);

    //字符位置
    @Getter
    private StrPosition strPosition;
    //保留或隐藏 true 隐藏 false 保留
    @Getter
    private boolean hidden;
    //保留/隐藏的字符数量
    @Getter
    private Integer count;

    SensitiveType(StrPosition strPosition, boolean retainOrHidden, Integer count) {
        this.strPosition = strPosition;
        this.hidden = retainOrHidden;
        this.count = count;
    }
}
