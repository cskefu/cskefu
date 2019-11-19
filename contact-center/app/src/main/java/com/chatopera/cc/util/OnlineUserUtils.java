package com.chatopera.cc.util;

import com.chatopera.cc.basic.MainUtils;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class OnlineUserUtils {

    public static void parseParameters(
            Map<String, String[]> map, String data,
            String encoding) throws UnsupportedEncodingException {
        if ((data == null) || (data.length() <= 0)) {
            return;
        }

        byte[] bytes = null;
        try {
            if (encoding == null) {
                bytes = data.getBytes();
            } else {
                bytes = data.getBytes(encoding);
            }

        } catch (UnsupportedEncodingException uee) {
        }
        parseParameters(map, bytes, encoding);
    }


    public static void parseParameters(
            Map<String, String[]> map, byte[] data,
            String encoding) throws UnsupportedEncodingException {
        if ((data != null) && (data.length > 0)) {
            int ix = 0;
            int ox = 0;
            String key = null;
            String value = null;
            while (ix < data.length) {
                byte c = data[(ix++)];
                switch ((char) c) {
                    case '&':
                        value = new String(data, 0, ox, encoding);
                        if (key != null) {
                            MainUtils.putMapEntry(map, key, value);
                            key = null;
                        }
                        ox = 0;
                        break;
                    case '=':
                        if (key == null) {
                            key = new String(data, 0, ox, encoding);
                            ox = 0;
                        } else {
                            data[(ox++)] = c;
                        }
                        break;
                    case '+':
                        data[(ox++)] = 32;
                        break;
                    case '%':
                        data[(ox++)] = (byte) ((MainUtils.convertHexDigit(
                                data[(ix++)]) << 4) + MainUtils.convertHexDigit(data[(ix++)]));

                        break;
                    default:
                        data[(ox++)] = c;
                }
            }

            if (key != null) {
                value = new String(data, 0, ox, encoding);
                MainUtils.putMapEntry(map, key, value);
            }
        }
    }
}
