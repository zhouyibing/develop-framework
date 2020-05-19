package com.yipeng.framework.core.utils;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.Collection;

/**
 * uri路径匹配工具
 * ant matcher路径匹配
 * @author: yibingzhou
 */
public class PathMatcherUtil {
    private static PathMatcher pathMatcher = new AntPathMatcher();

    public static boolean matchAny(String[] patterns, String path) {
        for(String p:patterns) {
            if(pathMatcher.match(p,path)){
                return true;
            }
        }
        return false;
    }

    public static boolean matchAny(String[] patterns, String[] paths) {
        for (String path : paths){
            for(String p:patterns) {
                if(pathMatcher.match(p,path)){
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean matchAny(String[] patterns, Collection<String> paths) {
        for (String path : paths){
            for(String p:patterns) {
                if(pathMatcher.match(p, path)){
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean match(String pattern, String path) {
        return pathMatcher.match(pattern, path);
    }
}
