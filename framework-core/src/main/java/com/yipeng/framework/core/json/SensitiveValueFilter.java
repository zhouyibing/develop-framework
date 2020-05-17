package com.yipeng.framework.core.json;

import com.alibaba.fastjson.serializer.ValueFilter;
import com.yipeng.framework.core.constants.StrPosition;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * 敏感信息过滤
 * @author: yibingzhou
 */
@Slf4j
public class SensitiveValueFilter implements ValueFilter {
    private final char HIDDEN_CHAR = '*';
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
            log.error("{} SensitiveValueFilter解析格式化异常:", obj, e);
            return value;
        }
        return value;
    }

    private Object getFormatValue(Field field, Object value) {
        SensitiveInfo sensitiveInfo = field.getAnnotation(SensitiveInfo.class);
        if(sensitiveInfo != null) {
            SensitiveType sensitiveType = sensitiveInfo.type();
            if(sensitiveType == null) {
                StrPosition strPosition = sensitiveInfo.pos();
                boolean hidden = sensitiveInfo.hidden();
                int count = sensitiveInfo.count();
                return desensitize(value, strPosition, hidden, count);
            } else {
                return desensitize(value, sensitiveType.getStrPosition(), sensitiveType.isHidden(), sensitiveType.getCount());
            }
        }
        return value;
    }

    private Object desensitize(Object value, StrPosition strPosition, boolean hidden, Integer count) {
        if(count<0) {
            count =0;
        }
        if(count ==0 && hidden) {
            return value;
        }
        if(count == 0 && !hidden) {
            return generateChars(HIDDEN_CHAR, value.toString().length()).toString();
        }
        String valStr = value.toString();
        if(hidden) {
            //如果字符串长度小于等于隐藏长度，全部隐藏
            if(valStr.length() <= count) {
                return generateChars(HIDDEN_CHAR, valStr.length()).toString();
            }
            //至少隐藏1/3长度
            if(valStr.length()/count > 3) {
                count = valStr.length()/3;
            }
            if(StrPosition.LEFT == strPosition) {
                return generateChars(HIDDEN_CHAR, count).append(valStr.substring(count)).toString();
            }
            if(StrPosition.RIGHT == strPosition) {
                return generateChars(HIDDEN_CHAR, count).insert(0, valStr.substring(0, valStr.length() - count)).toString();
            }
            if(StrPosition.CENTER == strPosition) {
                int half = (valStr.length() - count) /2;
                return generateChars(HIDDEN_CHAR, count).insert(0, valStr.substring(0,half)).append(valStr.substring(half+count));
            }
        } else {
            if(valStr.length() <= count) {
                return value;
            }
            if(StrPosition.LEFT == strPosition) {
                return generateChars(HIDDEN_CHAR, valStr.length() - count).insert(0, valStr.substring(0, count)).toString();
            }
            if(StrPosition.RIGHT == strPosition) {
                return generateChars(HIDDEN_CHAR, valStr.length() - count).append(valStr.substring(valStr.length()-count)).toString();
            }
            if(StrPosition.CENTER == strPosition) {
                int half = (valStr.length() - count) /2;
                return generateChars(HIDDEN_CHAR, half).append(valStr.substring(half, half+count)).append(generateChars(HIDDEN_CHAR, valStr.length()-count-half));
            }
        }
        return value;
    }

    private StringBuilder generateChars(char ch, int count) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < count; i++) {
            sb.append(ch);
        }
        return sb;
    }
}
