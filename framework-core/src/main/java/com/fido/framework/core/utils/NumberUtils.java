package com.fido.framework.core.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数字工具类
 * @author: yibingzhou
 */
public class NumberUtils {
    
    /** &lt;展示的小数0位数, 对应的格式串&gt; */
    private static Map<Integer, String> demoPatternMap = new ConcurrentHashMap<>();
    /** &lt;展示的小数0位数, 对应的格式串&gt; */
    private static Map<Integer, String> demoPatternNumMap = new ConcurrentHashMap<>();
    private static Map<String, ThreadLocal<DecimalFormat>> formatMap = new ConcurrentHashMap<>();
    
    /** @see DecimalFormat */
    public static String format(Object obj, String pattern) {
        if (obj == null) {
            return null;
        }
        return getFormat(pattern).format(obj);
    }
    
    public static String format(BigDecimal val) {
        if (val == null) {
            return null;
        }
        return formatByScale(val, val.scale());
    }
    
    /** 指定小数位输出-1-无, 0-输出一个.，其他实际小数输出，推荐直接调用 {@link #format(Object, String) format(obj, "0.00")} */
    public static String formatByScale(Object obj, int scale) {
        return format(obj, getPattern(scale));
    }
    
    /**
     * 返回指定精度的 0.0{precision,}
     * @param precision
     * @return
     */
    public static String getPattern(int precision) {
        return demoPatternMap.computeIfAbsent(precision, key -> {
            if (key == -1 || key == 0) {
                return "0";
            }
            StringBuilder buf = new StringBuilder("0.");
            for (int i = 0; i < key; i++) {
                buf.append('0');
            }
            return buf.toString();
        });
    }
    
    /**
     * 返回指定精度的 0.#{precision,}
     * @param precision
     * @return
     */
    public static String getPatternN(int precision) {
        return demoPatternNumMap.computeIfAbsent(precision, key -> {
            if (key == -1 || key == 0) {
                return "0";
            }
            StringBuilder buf = new StringBuilder("0.");
            for (int i = 0; i < key; i++) {
                buf.append('#');
            }
            return buf.toString();
        });
    }
    
    /**
     * 数量格式化(无小数时返回整数，有时返回小数)
     * @param num
     * @return
     */
    public static String formatNum(BigDecimal num) {
        if (num == null) {
            return null;
        }
        return format(num, getPatternN(num.scale()));
    }
    
    /**
     * 数量格式化(无小数时返回整数，有时返回小数)
     * @param num
     * @param scale
     * @return
     */
    public static String formatNum(BigDecimal num, int scale) {
        if (num == null) {
            return null;
        }
        
        String pattern = num.scale() == 0 ? "0" : getPatternN(scale);
        return format(num, pattern);
    }
 
    /**
     * 适用于钱，保留2位小数
     * 
     * @param obj
     * @return
     */
    public static String formatMoney(BigDecimal obj) {
        if (obj == null) {
            return null;
        }
        String pattern = "0.00";
        return format(obj, pattern);
    }

    /**
     * 适用于价格，大于等于1保留2位小数，小于1保留3位小数
     * 
     * @param price
     * @return
     * @since 0.1.0
     */
    public static String formatPrice(BigDecimal price) {
        if (price == null) {
            return null;
        }
        String pattern = "0.00";
        // 小于1的三位小数
        if (price.abs().compareTo(BigDecimal.ONE) < 0) {
            pattern = "0.000#";
        }
        return format(price, pattern);
    }

    /**
     * 适用于价格，大于等于1保留2位小数，小于1保留3位小数
     * 
     * @param value
     * @return
     * @since 0.1.0
     */
    public static BigDecimal formatPrice2BigDecimal(BigDecimal value) {
        if (value == null) {
            return null;
        }
        if (BigDecimal.ONE.compareTo(value) <= 0) {
            return value.setScale(2, BigDecimal.ROUND_HALF_UP); // 大于等于1两位小数
        } else {
            return value.setScale(3, BigDecimal.ROUND_HALF_UP); // 小于1三位小数
        }
    }

    /**
     * 适用百分比.最少4位小数最多5位小数
     * 
     * @param obj
     * @return
     */
    public static String formatRatio(BigDecimal obj) {
        if (obj == null) {
            return null;
        }
        return format(obj, "0.0000#");
    }

    /**
     * 是否为空或0
     * @param obj
     * @return
     */
    public static boolean isNullOrZero(BigDecimal obj) {
        return obj == null || obj.signum() == 0;
    }
    
    /**
     * 如果为null则返回0
     * @param obj
     * @return
     */
    public static BigDecimal getOrZero(BigDecimal obj) {
        if (obj == null) {
            return BigDecimal.ZERO;
        }
        return obj;
    }

    public static String formatInt(BigDecimal obj) {
        if (obj == null) {
            return null;
        }
        String pattern = "0.##";

        return format(obj, pattern);
    }

    public static BigDecimal parseBigDecimal(String value) {
        NumberFormat instance = DecimalFormat.getInstance();
        String string;
        try {
            string = instance.parse(value).toString();
        } catch (ParseException e) {
            throw new IllegalArgumentException(value + " format error");
        }
        return new BigDecimal(string);
    }
    
    public static boolean lte(Number value, int n) {
        if (value == null) {
            return true;
        }
        if (value.longValue() <= n) {
            return true;
        }
        if (value.longValue() > n) {
            return false;
        }
        return value.floatValue() <= n;
    }
    
    public static BigDecimal subtract(BigDecimal val1, BigDecimal val2) {
        if (val1 == null || val2 == null) {
            return null;
        }
        return val1.subtract(val2);
    }
    
    public static DecimalFormat getFormat(String pattern) {
        ThreadLocal<DecimalFormat> local = formatMap.computeIfAbsent(pattern, k -> new ThreadLocal<>());
        DecimalFormat result = local.get();
        if (result == null) {
            result = new DecimalFormat(pattern);
            result.setRoundingMode(RoundingMode.HALF_UP);
            local.set(result);
        }
        return result;
    }
    
}
