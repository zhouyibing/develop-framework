package com.fido.framework.core.json;

import com.alibaba.fastjson.serializer.ValueFilter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.math.BigDecimal;

/**
 * BigDecimal格式化输出
 * @author: yibingzhou
 */
@Slf4j
public class BigDecimalFormatFilter implements ValueFilter {

    @Override
    public Object process(Object obj, String name, Object value) {
        if(value == null) {
            return value;
        }

        try {
            Field field = obj.getClass().getDeclaredField(name);
            value = getFormatValue( field , value );
        } catch(NoSuchFieldException e){
            return value;
        }catch (Exception e) {
            log.error("{} BigDecimalFormatFilter解析格式化异常:", obj, e);
            return value;
        }
        return value;
    }

    private Object getFormatValue(Field field, Object value) {
        if(value instanceof BigDecimal) {
            BigDecimalFormat bigDecimalFormat = field.getAnnotation(BigDecimalFormat.class);
            if (bigDecimalFormat != null) {
                int scale = bigDecimalFormat.scale();
                int mode = bigDecimalFormat.mode();
                boolean zeroToEmpty = bigDecimalFormat.zeroToEmpty();
                if (zeroToEmpty && ((BigDecimal)value).signum() == 0) {
                    return null;
                }
                return ((BigDecimal)value).setScale(scale, mode).toString();
            }
        }
        return value;
    }
}
