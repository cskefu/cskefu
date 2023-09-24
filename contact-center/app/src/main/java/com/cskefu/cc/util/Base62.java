/*
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd.
 * <https://www.chatopera.com>, Licensed under the Chunsong Public
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2018- Jun. 2023 Chatopera Inc, <https://www.chatopera.com>,  Licensed under the Apache License, Version 2.0,
 * http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (C) 2017 优客服-多渠道客服系统,  Licensed under the Apache License, Version 2.0,
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.cskefu.cc.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Base62 {
    private static final int BINARY = 0x2;

    private static final int NUMBER_61 = 0x0000003d;

    static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B',
            'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z'};


    public static String encode(long value) {
        return encode(String.valueOf(value)).toLowerCase();
    }

    public static String encode(String str) {
        String md5Hex = DigestUtils.md5Hex(str);
        // 6 digit binary can indicate 62 letter & number from 0-9a-zA-Z
        int binaryLength = 6 * 6;
        long binaryLengthFixer = Long.valueOf(StringUtils.repeat("1", binaryLength), BINARY);
        for (int i = 0; i < 4; ) {
            String subString = StringUtils.substring(md5Hex, i * 8, (i + 1) * 8);
            subString = Long.toBinaryString(Long.valueOf(subString, 16) & binaryLengthFixer);
            subString = StringUtils.leftPad(subString, binaryLength, "0");
            StringBuilder sbBuilder = new StringBuilder();
            for (int j = 0; j < 6; j++) {
                String subString2 = StringUtils.substring(subString, j * 6, (j + 1) * 6);
                int charIndex = Integer.valueOf(subString2, BINARY) & NUMBER_61;
                sbBuilder.append(DIGITS[charIndex]);
            }
            String shortUrl = sbBuilder.toString();
            if (shortUrl != null) {
                return shortUrl;
            }
        }
        // if all 4 possibilities are already exists
        return null;
    }

    @SuppressWarnings("unused")
    private static void print(Object messagr) {
        System.out.println(messagr);
    }


    /**
     * 生成 16 位随机字符串作为 ID
     * https://stackoverflow.com/questions/4267475/generating-8-character-only-uuids
     */
    public static String generateShortId() {
        SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
        String randomStr = RandomStringUtils.randomAlphanumeric(10);
        return df.format(new Date()) + randomStr;
    }

    /**
     * 生成随机字符串
     *
     * @param targetStringLength
     * @return
     */
    public static String generatingRandomAlphanumericString(final int targetStringLength) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }
}