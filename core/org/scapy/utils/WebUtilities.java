package org.scapy.utils;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WebUtilities {

    private static final Pattern PARAMETER_PATTERN = Pattern.compile("<param name=\"(.*?)\" value=\"(.*?)\">");

    private WebUtilities() {

    }

    public static byte[] download(URLConnection connection) throws IOException {
        int length = connection.getContentLength();
        try (DataInputStream in = new DataInputStream(connection.getInputStream())) {
            if (length == -1) {
                return StreamUtilities.readFully(in);
            } else {
                byte[] bytes = new byte[length];
                in.readFully(bytes);
                return bytes;
            }
        }
    }

    public static byte[] download(URL address) throws IOException {
        return download(address.openConnection());
    }

    public static byte[] download(String address) throws IOException {
        return download(new URL(address));
    }

    public static String downloadPageSource(String address) throws IOException {
        return new String(download(address));
    }

    public static Map<String, String> parseParameters(String source, String... ignore) {
        List<String> ignoreList = Arrays.asList(ignore);
        Map<String, String> parameters = new HashMap<>();
        Matcher matcher = PARAMETER_PATTERN.matcher(source);
        while (matcher.find()) {
            String parameter = matcher.group(1);
            String value = matcher.group(2);
            if (!ignoreList.contains(parameter)) {
                parameters.put(parameter, value);
            }
        }
        return parameters;
    }
}