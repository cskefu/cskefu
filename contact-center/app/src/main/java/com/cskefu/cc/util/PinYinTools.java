/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cskefu.cc.util;


public class PinYinTools {  
    private static int[] pyvalue = new int[] {-20319, -20317, -20304, -20295, -20292, -20283, -20265, -20257, -20242, -20230, -20051, -20036, -20032,  
            -20026, -20002, -19990, -19986, -19982, -19976, -19805, -19784, -19775, -19774, -19763, -19756, -19751, -19746, -19741, -19739, -19728,  
            -19725, -19715, -19540, -19531, -19525, -19515, -19500, -19484, -19479, -19467, -19289, -19288, -19281, -19275, -19270, -19263, -19261,  
            -19249, -19243, -19242, -19238, -19235, -19227, -19224, -19218, -19212, -19038, -19023, -19018, -19006, -19003, -18996, -18977, -18961,  
            -18952, -18783, -18774, -18773, -18763, -18756, -18741, -18735, -18731, -18722, -18710, -18697, -18696, -18526, -18518, -18501, -18490,  
            -18478, -18463, -18448, -18447, -18446, -18239, -18237, -18231, -18220, -18211, -18201, -18184, -18183, -18181, -18012, -17997, -17988,  
            -17970, -17964, -17961, -17950, -17947, -17931, -17928, -17922, -17759, -17752, -17733, -17730, -17721, -17703, -17701, -17697, -17692,  
            -17683, -17676, -17496, -17487, -17482, -17468, -17454, -17433, -17427, -17417, -17202, -17185, -16983, -16970, -16942, -16915, -16733,  
            -16708, -16706, -16689, -16664, -16657, -16647, -16474, -16470, -16465, -16459, -16452, -16448, -16433, -16429, -16427, -16423, -16419,  
            -16412, -16407, -16403, -16401, -16393, -16220, -16216, -16212, -16205, -16202, -16187, -16180, -16171, -16169, -16158, -16155, -15959,  
            -15958, -15944, -15933, -15920, -15915, -15903, -15889, -15878, -15707, -15701, -15681, -15667, -15661, -15659, -15652, -15640, -15631,  
            -15625, -15454, -15448, -15436, -15435, -15419, -15416, -15408, -15394, -15385, -15377, -15375, -15369, -15363, -15362, -15183, -15180,  
            -15165, -15158, -15153, -15150, -15149, -15144, -15143, -15141, -15140, -15139, -15128, -15121, -15119, -15117, -15110, -15109, -14941,  
            -14937, -14933, -14930, -14929, -14928, -14926, -14922, -14921, -14914, -14908, -14902, -14894, -14889, -14882, -14873, -14871, -14857,  
            -14678, -14674, -14670, -14668, -14663, -14654, -14645, -14630, -14594, -14429, -14407, -14399, -14384, -14379, -14368, -14355, -14353,  
            -14345, -14170, -14159, -14151, -14149, -14145, -14140, -14137, -14135, -14125, -14123, -14122, -14112, -14109, -14099, -14097, -14094,  
            -14092, -14090, -14087, -14083, -13917, -13914, -13910, -13907, -13906, -13905, -13896, -13894, -13878, -13870, -13859, -13847, -13831,  
            -13658, -13611, -13601, -13406, -13404, -13400, -13398, -13395, -13391, -13387, -13383, -13367, -13359, -13356, -13343, -13340, -13329,  
            -13326, -13318, -13147, -13138, -13120, -13107, -13096, -13095, -13091, -13076, -13068, -13063, -13060, -12888, -12875, -12871, -12860,  
            -12858, -12852, -12849, -12838, -12831, -12829, -12812, -12802, -12607, -12597, -12594, -12585, -12556, -12359, -12346, -12320, -12300,  
            -12120, -12099, -12089, -12074, -12067, -12058, -12039, -11867, -11861, -11847, -11831, -11798, -11781, -11604, -11589, -11536, -11358,  
            -11340, -11339, -11324, -11303, -11097, -11077, -11067, -11055, -11052, -11045, -11041, -11038, -11024, -11020, -11019, -11018, -11014,  
            -10838, -10832, -10815, -10800, -10790, -10780, -10764, -10587, -10544, -10533, -10519, -10331, -10329, -10328, -10322, -10315, -10309,  
            -10307, -10296, -10281, -10274, -10270, -10262, -10260, -10256, -10254};  
    public static String[] pystr = new String[] {"A", "AI", "AN", "ANG", "AO", "BA", "BAI", "BAN", "BANG", "BAO", "BEI", "BEN", "BENG", "BI", "BIAN",  
        "BIAO", "BIE", "BIN", "BING", "BO", "BU", "CA", "CAI", "CAN", "CANG", "CAO", "CE", "CENG", "CHA", "CHAI", "CHAN", "CHANG", "CHAO", "CHE",  
        "CHEN", "CHENG", "CHI", "CHONG", "CHOU", "CHU", "CHUAI", "CHUAN", "CHUANG", "CHUI", "CHUN", "CHUO", "CI", "CONG", "COU", "CU", "CUAN",  
        "CUI", "CUN", "CUO", "DA", "DAI", "DAN", "DANG", "DAO", "DE", "DENG", "DI", "DIAN", "DIAO", "DIE", "DING", "DIU", "DONG", "DOU", "DU",  
        "DUAN", "DUI", "DUN", "DUO", "E", "EN", "ER", "FA", "FAN", "FANG", "FEI", "FEN", "FENG", "FO", "FOU", "FU", "GA", "GAI", "GAN", "GANG",  
        "GAO", "GE", "GEI", "GEN", "GENG", "GONG", "GOU", "GU", "GUA", "GUAI", "GUAN", "GUANG", "GUI", "GUN", "GUO", "HA", "HAI", "HAN", "HANG",  
        "HAO", "HE", "HEI", "HEN", "HENG", "HONG", "HOU", "HU", "HUA", "HUAI", "HUAN", "HUANG", "HUI", "HUN", "HUO", "JI", "JIA", "JIAN",  
        "JIANG", "JIAO", "JIE", "JIN", "JING", "JIONG", "JIU", "JU", "JUAN", "JUE", "JUN", "KA", "KAI", "KAN", "KANG", "KAO", "KE", "KEN",  
        "KENG", "KONG", "KOU", "KU", "KUA", "KUAI", "KUAN", "KUANG", "KUI", "KUN", "KUO", "LA", "LAI", "LAN", "LANG", "LAO", "LE", "LEI", "LENG",  
        "LI", "LIA", "LIAN", "LIANG", "LIAO", "LIE", "LIN", "LING", "LIU", "LONG", "LOU", "LU", "LV", "LUAN", "LUE", "LUN", "LUO", "MA", "MAI",  
        "MAN", "MANG", "MAO", "ME", "MEI", "MEN", "MENG", "MI", "MIAN", "MIAO", "MIE", "MIN", "MING", "MIU", "MO", "MOU", "MU", "NA", "NAI",  
        "NAN", "NANG", "NAO", "NE", "NEI", "NEN", "NENG", "NI", "NIAN", "NIANG", "NIAO", "NIE", "NIN", "NING", "NIU", "NONG", "NU", "NV", "NUAN",  
        "NUE", "NUO", "O", "OU", "PA", "PAI", "PAN", "PANG", "PAO", "PEI", "PEN", "PENG", "PI", "PIAN", "PIAO", "PIE", "PIN", "PING", "PO", "PU",  
        "QI", "QIA", "QIAN", "QIANG", "QIAO", "QIE", "QIN", "QING", "QIONG", "QIU", "QU", "QUAN", "QUE", "QUN", "RAN", "RANG", "RAO", "RE",  
        "REN", "RENG", "RI", "RONG", "ROU", "RU", "RUAN", "RUI", "RUN", "RUO", "SA", "SAI", "SAN", "SANG", "SAO", "SE", "SEN", "SENG", "SHA",  
        "SHAI", "SHAN", "SHANG", "SHAO", "SHE", "SHEN", "SHENG", "SHI", "SHOU", "SHU", "SHUA", "SHUAI", "SHUAN", "SHUANG", "SHUI", "SHUN",  
        "SHUO", "SI", "SONG", "SOU", "SU", "SUAN", "SUI", "SUN", "SUO", "TA", "TAI", "TAN", "TANG", "TAO", "TE", "TENG", "TI", "TIAN", "TIAO",  
        "TIE", "TING", "TONG", "TOU", "TU", "TUAN", "TUI", "TUN", "TUO", "WA", "WAI", "WAN", "WANG", "WEI", "WEN", "WENG", "WO", "WU", "XI",  
        "XIA", "XIAN", "XIANG", "XIAO", "XIE", "XIN", "XING", "XIONG", "XIU", "XU", "XUAN", "XUE", "XUN", "YA", "YAN", "YANG", "YAO", "YE", "YI",  
        "YIN", "YING", "YO", "YONG", "YOU", "YU", "YUAN", "YUE", "YUN", "ZA", "ZAI", "ZAN", "ZANG", "ZAO", "ZE", "ZEI", "ZEN", "ZENG", "ZHA",  
        "ZHAI", "ZHAN", "ZHANG", "ZHAO", "ZHE", "ZHEN", "ZHENG", "ZHI", "ZHONG", "ZHOU", "ZHU", "ZHUA", "ZHUAI", "ZHUAN", "ZHUANG", "ZHUI",  
        "ZHUN", "ZHUO", "ZI", "ZONG", "ZOU", "ZU", "ZUAN", "ZUI", "ZUN", "ZUO"};  
    private StringBuilder buffer;  
    private String resource;  
    private static PinYinTools characterParser = new PinYinTools();  
  
    public static PinYinTools getInstance() {  
        return characterParser;  
    }  
  
    public String getResource() {  
        return resource;  
    }  
  
    public void setResource(String resource) {  
        this.resource = resource;  
    }  
  
    /** * 汉字转成ASCII码 * * @param chs * @return */  
    private int getChsAscii(String chs) {  
        int asc = 0;  
        try {  
            byte[] bytes = chs.getBytes("gb2312");  
            if (bytes == null || bytes.length > 2 || bytes.length <= 0) {  
                throw new RuntimeException("illegal resource string");  
            }  
            if (bytes.length == 1) {  
                asc = bytes[0];  
            }  
            if (bytes.length == 2) {  
                int hightByte = 256 + bytes[0];  
                int lowByte = 256 + bytes[1];  
                asc = (256 * hightByte + lowByte) - 256 * 256;  
            }  
        } catch (Exception e) {  
            System.out.println("ERROR:ChineseSpelling.class-getChsAscii(String chs)" + e);  
        }  
        return asc;  
    }  
  
    /** * 单字解析 * * @param str * @return */  
    public String convert(String str) {  
        String result = null;  
        int ascii = getChsAscii(str);  
        if (ascii > 0 && ascii < 160) {  
            result = String.valueOf((char) ascii);  
        } else {  
            for (int i = (pyvalue.length - 1); i >= 0; i--) {  
                if (pyvalue[i] <= ascii) {  
                    result = pystr[i];  
                    break;  
                }  
            }  
        }  
        return result;  
    }  
  
    /** * 词组解析 * * @param chs * @return */  
    public String getSelling(String chs) {  
        String key, value;  
        buffer = new StringBuilder();  
        for (int i = 0; i < chs.length(); i++) {  
            key = chs.substring(i, i + 1);  
            if (key.getBytes().length >= 2) {  
                value = (String) convert(key);  
                if (value == null) {  
                    value = "unknown";  
                }  
            } else {  
                value = key;  
            }  
            buffer.append(value+" ");  
        }  
        return buffer.toString();  
    }  
  
    public String getSpelling() {  
        return this.getSelling(this.getResource());  
    }  
    
    public String getFirstPinYin(String word){
    	String firstWord = "0";
    	String pinYin = PinYinTools.getInstance().getSelling(word) ;
    	if(pinYin.length() > 0){
    		firstWord = pinYin.substring(0,1) ;
    	}
    	return firstWord ;
    }
  
}  