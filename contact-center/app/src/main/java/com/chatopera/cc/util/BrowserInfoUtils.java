package com.chatopera.cc.util;

public class BrowserInfoUtils {
    private BrowserInfoUtils() {
        throw new UnsupportedOperationException();
    }

    public static String parseBrowserHostOSFromUserAgent(String userAgentFromRequest) {

        String os = "";

        if (userAgentFromRequest.toLowerCase().indexOf("windows") >= 0) {
            os = "Windows";
        } else if (userAgentFromRequest.toLowerCase().indexOf("mac") >= 0) {
            os = "Mac";
        } else if (userAgentFromRequest.toLowerCase().indexOf("x11") >= 0) {
            os = "Unix";
        } else if (userAgentFromRequest.toLowerCase().indexOf("android") >= 0) {
            os = "Android";
        } else if (userAgentFromRequest.toLowerCase().indexOf("iphone") >= 0) {
            os = "IPhone";
        } else {
            os = "UnKnown, User-Agent: " + userAgentFromRequest;
        }
        return os;
    }

    public static String parseBrowserFromUserAgent(String userAgentFromRequest) {
        String lowerCase = userAgentFromRequest.toLowerCase();

        String browser = "";

        if (lowerCase.contains("edge")) {
            browser = (userAgentFromRequest.substring(userAgentFromRequest.indexOf("Edge")).split(" ")[0]).replace("/", "-");
        } else if (lowerCase.contains("msie")) {
            String substring = userAgentFromRequest.substring(userAgentFromRequest.indexOf("MSIE")).split(";")[0];
            browser = substring.split(" ")[0].replace("MSIE", "IE") + "-" + substring.split(" ")[1];
        } else if (lowerCase.contains("safari") && lowerCase.contains("version")) {
            browser = (userAgentFromRequest.substring(userAgentFromRequest.indexOf("Safari")).split(" ")[0]).split("/")[0]
                    + "-" + (userAgentFromRequest.substring(userAgentFromRequest.indexOf("Version")).split(" ")[0]).split("/")[1];
        } else if (lowerCase.contains("opr") || lowerCase.contains("opera")) {
            if (lowerCase.contains("opera")) {
                browser = (userAgentFromRequest.substring(userAgentFromRequest.indexOf("Opera")).split(" ")[0]).split("/")[0]
                        + "-" + (userAgentFromRequest.substring(userAgentFromRequest.indexOf("Version")).split(" ")[0]).split("/")[1];
            } else if (lowerCase.contains("opr")) {
                browser = ((userAgentFromRequest.substring(userAgentFromRequest.indexOf("OPR")).split(" ")[0]).replace("/", "-"))
                        .replace("OPR", "Opera");
            }
        } else if (lowerCase.contains("chrome")) {
            browser = (userAgentFromRequest.substring(userAgentFromRequest.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
        } else if ((lowerCase.indexOf("mozilla/7.0") > -1) || (lowerCase.indexOf("netscape6") != -1) ||
                (lowerCase.indexOf("mozilla/4.7") != -1) || (lowerCase.indexOf("mozilla/4.78") != -1) ||
                (lowerCase.indexOf("mozilla/4.08") != -1) || (lowerCase.indexOf("mozilla/3") != -1)) {
            browser = "Netscape-?";
        } else if (lowerCase.contains("firefox")) {
            browser = (userAgentFromRequest.substring(userAgentFromRequest.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
        } else if (lowerCase.contains("rv")) {
            String IEVersion = (userAgentFromRequest.substring(userAgentFromRequest.indexOf("rv")).split(" ")[0]).replace("rv:", "-");
            browser = "IE" + IEVersion.substring(0, IEVersion.length() - 1);
        } else {
            browser = "UnKnown, User-Agent: " + userAgentFromRequest;
        }
        return browser;
    }
}
