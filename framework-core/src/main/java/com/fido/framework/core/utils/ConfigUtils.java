package com.fido.framework.core.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigUtils {
    private static Environment environment = SpringContextUtils.getBean(Environment.class);
    private static Map<String,Object> cache = Maps.newConcurrentMap();
    private final static String ARR_FLAG="#arr";
    private final static String LIST_FLAG="#list";
    private final static String SET_FLAG="#set";

    public static String getProperty(String key, String def,boolean useCache) {
        Object ret = null;
        if(useCache) {
            ret = cache.get(key);
        }
        if(null==ret){
            ret = environment.getProperty(key, def);
            if(null!=ret){
                cache.put(key,ret);
            }
        }
        return null==ret?def:(String) ret;
    }

    public static String getProperty(String key) {
        return getProperty(key,null,true);
    }

    public static String getProperty(String key,String def) {
        return getProperty(key,def,true);
    }

    public static Boolean getBoolean(String key,Boolean def){
        String v = getProperty(key);
        if(null == v){
            return def;
        }
        return Boolean.valueOf(v);
    }

    public static Boolean getBoolean(String key){
        return getBoolean(key,null);
    }

    public static Integer getInteger(String key,Integer def,boolean useCache){
        Object ret = null;
        if(useCache) {
            ret = cache.get(key);
        }
        if(null==ret){
            ret = getProperty(key);
            if(null!=ret){
                ret = Integer.valueOf(ret.toString());
                cache.put(key,ret);
            }
        }
        return null==ret?def: (Integer) ret;
    }

    public static Integer getInteger(String key){
        return getInteger(key,null,true);
    }
    public static Integer getInteger(String key,Integer def){
        return getInteger(key,def,true);
    }

    public static Long getLong(String key,Long def,boolean useCache){
        Object ret = null;
        if(useCache) {
            ret = cache.get(key);
        }
        if(null==ret){
            ret = getProperty(key);
            if(null!=ret){
                ret = Long.valueOf(ret.toString());
                cache.put(key,ret);
            }
        }
        return null==ret?def: (Long) ret;
    }

    public static Long getLong(String key){
        return getLong(key,null,true);
    }

    public static Long getLong(String key,Long def){
        return getLong(key,def,true);
    }

    public static String[] getArray(String key){
        return getArray(key,",");
    }
    public static String[] getArray(String key,boolean useCache){
        return getArray(key,",",useCache);
    }

    public static String[] getArray(String key,String separator){
        return getArray(key,separator,true);
    }

    public static String[] getArray(String key,String separator,boolean useCache){
        Object ret = null;
        if(useCache) {
            ret = cache.get(key+ARR_FLAG);
        }
        if(null==ret){
            ret = getProperty(key);
            if(null!=ret){
                ret = ret.toString().split(separator);
                cache.put(key+ARR_FLAG,ret);
            }
        }
        return null==ret?null: (String[]) ret;
    }

    public static List<String> getList(String key) {
        return getList(key,true);
    }

    public static List<String> getList(String key,boolean useCache){
        Object ret = null;
        if(useCache) {
            ret = cache.get(key+LIST_FLAG);
        }
        if(null==ret){
            ret = getArray(key,useCache);
            if(null!=ret){
                List<String> s = Lists.newArrayList();
                for(String r:(String[])ret) {
                    s.add(r);
                }
                ret = s;
                cache.put(key+LIST_FLAG,ret);
            }
        }
        return null==ret?null: (List<String>) ret;
    }

    public static Set<String> getSet(String key){
        return getSet(key,true);
    }

    public static Set<String> getSet(String key,boolean useCache){
        Object ret = null;
        if(useCache) {
            ret = cache.get(key+SET_FLAG);
        }
        if(null==ret){
            ret = getArray(key,useCache);
            if(null!=ret){
                Set<String> s = Sets.newHashSet();
                for(String r:(String[])ret) {
                    s.add(r);
                }
                ret = s;
                cache.put(key+SET_FLAG,ret);
            }
        }
        return null==ret?null: (Set<String>) ret;
    }
}
