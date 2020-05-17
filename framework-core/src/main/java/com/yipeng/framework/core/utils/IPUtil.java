package com.yipeng.framework.core.utils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

/**
 * IPUtil
 *
 * @author yibing zhou
 * @date 2016/4/1
 */
@Slf4j
public class IPUtil {
    private static int ipInt;
    private static String realIp;
    private static List<String> realIps=Lists.newArrayList();
    public static String getIp() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            if(null==inetAddress) {
                return  null;
            }
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }

    public static String getRealIp(){
        if(null!=realIp) {
            return realIp;
        }
        try {
            Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
            for(;n.hasMoreElements();){
                NetworkInterface e = n.nextElement();
                Enumeration<InetAddress> a = e.getInetAddresses();
                for (; a.hasMoreElements();)
                {
                    InetAddress addr = a.nextElement();
                    String ad = addr.getHostAddress();
                    if(checkIP(ad)&&!ad.equals("127.0.0.1")) {
                        realIp = ad;
                        return realIp;
                    }
                }
            }
        } catch (SocketException e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }
    public static List<String> getRealIps(){
        if(null!=realIps&&!realIps.isEmpty()) {
            return realIps;
        }
        try {
            Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
            while(n.hasMoreElements()){
            	NetworkInterface e = n.nextElement();
                Enumeration<InetAddress> a = e.getInetAddresses();
                for (; a.hasMoreElements();)
                {
                    InetAddress addr = a.nextElement();
                    String ad = addr.getHostAddress();
                    if(checkIP(ad)&&!ad.equals("127.0.0.1")) {
                        realIps.add(ad);
                    }
                }
            }
            if(null!=realIps&&realIps.size()==1) {
                realIp=realIps.get(0);
            }
            return realIps;
        } catch (SocketException e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }

    public static int getRealIpInt(){
        if(0!=ipInt) {
            return ipInt;
        }
        String ip = getRealIp();
        if(ip!=null) {
            ipInt = ipToInt(ip);
        }
        return ipInt;
    }

    public static  boolean checkIP(String str) {
        if(null==str) {
            return false;
        }
        Pattern pattern = Pattern
                .compile("^((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]"
                        + "|[*])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])$");
        return pattern.matcher(str).matches();
    }

    public static int getIpInt(){
        String ip = getIp();
        if(null!=ip){
            return ipToInt(ip);
        }
        return 0;
    }

    /**
     * 获取访问IP
     */
    public static String getClientIp(HttpServletRequest request) {
        try {
            String ip = request.getHeader("x-forwarded-for");
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            if(ip.equals("0:0:0:0:0:0:0:1")){
                ip=getIp();
            }
            return ip;
        } catch (Exception ex) {
            // ignored
            return "";
        }
    }

    public static int getClientIpInt(HttpServletRequest request){
        String ip = getClientIp(request);
        if(ip.equals("")) {
            return 0;
        }else if(ip.equals("0:0:0:0:0:0:0:1")){
            return getIpInt();
        }else{
            return ipToInt(ip);
        }
    }

    public static int ipToInt(String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            byte[] bytes = address.getAddress();
            int a, b, c, d;
            a = byte2int(bytes[0]);
            b = byte2int(bytes[1]);
            c = byte2int(bytes[2]);
            d = byte2int(bytes[3]);
            int result = (a << 24) | (b << 16) | (c << 8) | d;
            return result;
        } catch (UnknownHostException e) {
            return 0;
        }
    }

    public static int byte2int(byte b) {
        int l = b & 0x07f;
        if (b < 0) {
            l |= 0x80;
        }
        return l;
    }

    public static String ipInt2Str(int ip){
        final StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(ip>>>24)).append(".");
        sb.append(String.valueOf((ip&0xFFFFFF)>>>16 )).append(".");
        sb.append(String.valueOf((ip&0xFFFF)>>>8 )).append(".");
        sb.append(String.valueOf(ip&0xFF));
        return sb.toString();
    }
    public static BigInteger ipv6toInt(String ipv6){

        int compressIndex = ipv6.indexOf("::");
        if (compressIndex != -1)
        {
            String part1s = ipv6.substring(0, compressIndex);
            String part2s = ipv6.substring(compressIndex + 1);
            BigInteger part1 = ipv6toInt(part1s);
            BigInteger part2 = ipv6toInt(part2s);
            int part1hasDot = 0;
            char ch[] = part1s.toCharArray();
            for (char c : ch)
            {
                if (c == ':')
                {
                    part1hasDot++;
                }
            }
            // ipv6 has most 7 dot
            return part1.shiftLeft(16 * (7 - part1hasDot )).add(part2);
        }
        String[] str = ipv6.split(":");
        BigInteger big = BigInteger.ZERO;
        for (int i = 0; i < str.length; i++)
        {
            //::1
            if (str[i].isEmpty())
            {
                str[i] = "0";
            }
            big = big.add(BigInteger.valueOf(Long.valueOf(str[i], 16))
                    .shiftLeft(16 * (str.length - i - 1)));
        }
        return big;
    }

    /**
     * 10.0.0.0/8：10.0.0.0～10.255.255.255 
     　　172.16.0.0/12：172.16.0.0～172.31.255.255 
     　　192.168.0.0/16：192.168.0.0～192.168.255.255
     * @param ipStr
     * @return
     */
    public static boolean isLocalIp(String ipStr){
        InetAddress address = null;
        try {
            address = InetAddress.getByName(ipStr);
            byte[] bytes = address.getAddress();
            return ((bytes[0]&0xff)==0x0a)||((bytes[0]&0xff)==0xac&&(bytes[1]&0xff)>=0x10&&(bytes[1]&0xff)<=0x1f)||((bytes[0]&0xff)==0xc0&&(bytes[1]&0xff)==0xa8);
        } catch (UnknownHostException e) {
            log.warn("occurred an exception:{}",e);
        }
        return false;
    }
}
