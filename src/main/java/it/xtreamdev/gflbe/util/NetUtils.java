package it.xtreamdev.gflbe.util;

import lombok.extern.apachecommons.CommonsLog;

import java.net.MalformedURLException;
import java.net.URL;

@CommonsLog
public class NetUtils {

    public static String extractDomain(String urlString) {
        try {
            URL url = new URL(urlString);
            String host = url.getHost();
            return host.startsWith("www.") ? host.substring(4) : host;
        } catch (MalformedURLException e) {
            return urlString;
        }
    }

}
