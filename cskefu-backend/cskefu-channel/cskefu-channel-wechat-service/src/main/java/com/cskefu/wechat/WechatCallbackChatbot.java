package com.cskefu.wechat;

import com.cskefu.wechat.utils.MessageCrypt;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.net.ssl.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;

@RestController
@RequestMapping("/wechat/kefu/chatbot")
@Slf4j
public class WechatCallbackChatbot {

    @Autowired
    private WechatKefuServiceImpl wechatKefuServiceImpl;

    @RequestMapping(value = "/notice")
    public String notice(@RequestBody Encrypted request) {
        val msgSignature = "";
        val nonce = "";
        val timestamp = "";
        String token = "ZMKJqyNF1Jsrgukrt63r6gwSUHRDL2";
        String encodingAesKey = "sbM8nhraawdlLeby8BcdtCEHBGknp9UHP417sTSLj5Z";
        String appId = "zBRtvx42IgHI7dc";
        try {
            MessageCrypt messageCrypt = new MessageCrypt(token, encodingAesKey, appId);
            // 第三方收到公众号平台发送的消息
            String result2 = messageCrypt.decryptMessage(msgSignature, timestamp, nonce, "<Encrypt>" + request.getEncrypted() + "</Encrypt>");
            System.out.println("解密后明文: " + result2);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);


            String replyMsg = " 中文<xml><ToUserName><![CDATA[oia2TjjewbmiOUlr6X-1crbLOvLw]]></ToUserName><FromUserName><![CDATA[gh_7f083739789a]]></FromUserName><CreateTime>1407743423</CreateTime><MsgType><![CDATA[video]]></MsgType><Video><MediaId><![CDATA[eYJ1MbwPRJtOvIEabaxHs7TX2D-HV71s79GUxqdUkjm6Gs2Ed1KF3ulAOA9H1xG0]]></MediaId><Title><![CDATA[testCallBackReplyVideo]]></Title><Description><![CDATA[testCallBackReplyVideo]]></Description></Video></xml>";

            String mingwen = messageCrypt.encryptMessage(replyMsg, timestamp, nonce);
            System.out.println("加密后: " + mingwen);

            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(mingwen);
            InputSource is = new InputSource(sr);
            Document document = db.parse(is);

            Element root = document.getDocumentElement();
            NodeList nodelist1 = root.getElementsByTagName("Encrypt");
            NodeList nodelist2 = root.getElementsByTagName("MsgSignature");

            String encrypt = nodelist1.item(0).getTextContent();
//            String msgSignature = nodelist2.item(0).getTextContent();

            String format = "<xml><ToUserName><![CDATA[toUser]]></ToUserName><Encrypt><![CDATA[%1$s]]></Encrypt></xml>";
            String fromXML = String.format(format, encrypt);
            // 公众平台发送消息给第三方，第三方处理


        } catch (Exception e) {
            e.printStackTrace();
        }
//
//
//        try {
//            MessageCrypt messageCrypt = new MessageCrypt(token, encodingAesKey, appId);
//            if (HttpMethod.POST.name().equals(request.getMethod())) {
//                return messageCrypt.verifyURL(msg_signature, timestamp, nonce, echostr);
//            }
//            String notice = messageCrypt.decryptMessage(msg_signature, timestamp, nonce, postData);
//            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder = dbf.newDocumentBuilder();
//            StringReader reader = new StringReader(notice);
//            InputSource source = new InputSource(reader);
//            Document document = builder.parse(source);
//
//            Element root = document.getDocumentElement();
//            String message_token = root.getElementsByTagName("Token").item(0).getTextContent();
//            Map<String, Object> param = new HashMap<>();
//            // TODO liuyong 保存cursor和open_kfid，到数据库
////            param.put("cursor", "4gw7MepFLfgF2VC5npf");//上一次调用时返回的next_cursor，第一次拉取可以不填。不多于64字节
////            param.put("open_kfid", "0");//指定拉取某个客服账号的消息，否则默认返回所有客服账号的消息
//            param.put("token", message_token);
//            param.put("limit", 10);// 期望请求的数据量，默认值和最大值都为1000。 注意：可能会出现返回条数少于limit的情况，需结合返回的has_more字段判断是否继续请求。
//            param.put("voice_format", "0");//语音消息类型，0-Amr 1-Silk，默认0。可通过该参数控制返回的语音格式
//            AsyncMessageResponse response = null;
////            do {
//            response = wechatKefuServiceImpl.syncMessage(param);
//            List<MessageItem> msg_list = response.getMsg_list();
//            if (!CollectionUtils.isEmpty(msg_list)) {
//                for (MessageItem messageItem : msg_list) {
//                    // TODO 消息入库
//                    log.error("message {}", new ObjectMapper().writeValueAsString(messageItem));
//                    // TODO 消息发送给春松客服坐席
//                }
//            }
//            } while (response.hasMore());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return "200";
    }

    public static void main(String[] args) throws Exception {
        String replyMsg = "中文<xml><ToUserName><![CDATA[oia2TjjewbmiOUlr6X-1crbLOvLw]]></ToUserName><FromUserName><![CDATA[gh_7f083739789a]]></FromUserName><CreateTime>1407743423</CreateTime><MsgType><![CDATA[video]]></MsgType><Video><MediaId><![CDATA[eYJ1MbwPRJtOvIEabaxHs7TX2D-HV71s79GUxqdUkjm6Gs2Ed1KF3ulAOA9H1xG0]]></MediaId><Title><![CDATA[testCallBackReplyVideo]]></Title><Description><![CDATA[testCallBackReplyVideo]]></Description></Video></xml>";

        val nonce = "";
        val timestamp = "";
        String token = "ZMKJqyNF1Jsrgukrt63r6gwSUHRDL2";
        String encodingAesKey = "sbM8nhraawdlLeby8BcdtCEHBGknp9UHP417sTSLj5Z";
        String appId = "zBRtvx42IgHI7dc";
        MessageCrypt pc = new MessageCrypt(token, encodingAesKey, appId);
        String mingwen = pc.encryptMessage(replyMsg, timestamp, nonce);
//        0Pp4xGMo44SpMW2PgfofKDRakK+/OirNL/8Q0JG0RKu6rMCZ3t+u2PanyCG0sCIGHv82RKIl/6K58NCkX3w9FuIq2I+rpK0q5e3UiobeLQL7qDW6GDB4AhlROEllP7lI6r7rmCYI6gzo51x3CGXe7Knu4ZlcFQYwsX0rRo8KlrhK/JjfcaRvsMKp8qwxhgMZEr+juk17zj6Nu85cfVs25tdOVIiKS1TE/RW0/36AFfKInyX/+r3DlmrN0Ei2WmyTQSnfsUABRPrnjsZI/dEBlhq28Lps0X+7jo1wQ7Fxl4oXxrh5xtAyM7dEkG1w5Ay6oHXT8rQ3N0ojPw6l7OZxCRDYeZrfgeHz1//A+xGwfk0JgxSvStfB7VoiqlQh26GqQFpsG03VvZA6jNzTu6g4XD1iiWTv4xcVH/5koXca/DBRtDhpIr/dWuaPeKY+//1FIqHGyi95dHC6vTCKlrakHgfZXTGXBxXvRWFi4ndcPKiyY32rWY+47AcEtz6PtPxfRCw51YPNre/n8l5fbtOElOspZCMQbde/wNlTDuW/kY5AQoigJQVQjkNDdsuxq3TDjtrbRCIB/2UdRyaa/XFeOA==
        mingwen = "<xml><Encrypt><![CDATA[0Pp4xGMo44SpMW2PgfofKDRakK+/OirNL/8Q0JG0RKu6rMCZ3t+u2PanyCG0sCIGHv82RKIl/6K58NCkX3w9FuIq2I+rpK0q5e3UiobeLQL7qDW6GDB4AhlROEllP7lI6r7rmCYI6gzo51x3CGXe7Knu4ZlcFQYwsX0rRo8KlrhK/JjfcaRvsMKp8qwxhgMZEr+juk17zj6Nu85cfVs25tdOVIiKS1TE/RW0/36AFfKInyX/+r3DlmrN0Ei2WmyTQSnfsUABRPrnjsZI/dEBlhq28Lps0X+7jo1wQ7Fxl4oXxrh5xtAyM7dEkG1w5Ay6oHXT8rQ3N0ojPw6l7OZxCRDYeZrfgeHz1//A+xGwfk0JgxSvStfB7VoiqlQh26GqQFpsG03VvZA6jNzTu6g4XD1iiWTv4xcVH/5koXca/DBRtDhpIr/dWuaPeKY+//1FIqHGyi95dHC6vTCKlrakHgfZXTGXBxXvRWFi4ndcPKiyY32rWY+47AcEtz6PtPxfRCw51YPNre/n8l5fbtOElOspZCMQbde/wNlTDuW/kY5AQoigJQVQjkNDdsuxq3TDjtrbRCIB/2UdRyaa/XFeOA==]]></Encrypt><MsgSignature></MsgSignature><TimeStamp></TimeStamp><Nonce></Nonce></xml>";

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);

        DocumentBuilder db = dbf.newDocumentBuilder();
        StringReader sr = new StringReader(mingwen);
        InputSource is = new InputSource(sr);
        Document document = db.parse(is);

        Element root = document.getDocumentElement();
        NodeList nodelist1 = root.getElementsByTagName("Encrypt");
        NodeList nodelist2 = root.getElementsByTagName("MsgSignature");

        String encrypt = nodelist1.item(0).getTextContent();
        String msgSignature = nodelist2.item(0).getTextContent();
        String result2 = pc.decryptMessage(msgSignature, timestamp, nonce, encrypt);
        System.out.println(result2);
//        String format = "<xml><ToUserName><![CDATA[toUser]]></ToUserName><Encrypt><![CDATA[%1$s]]></Encrypt></xml>";
//        String fromXML = String.format(format, encrypt);
//
//        // 第三方收到公众号平台发送的消息
//        String result2 = pc.decryptMessage(msgSignature, timestamp, nonce, fromXML);
//        System.out.println("解密后明文: " + result2);
    }

    public static String sendSSLPost(String url, String param) {
        StringBuilder result = new StringBuilder();
        String urlNameString = url + "?" + param;
        try {
            log.info("sendSSLPost - {}", urlNameString);
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
            URL console = new URL(urlNameString);
            HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("contentType", "utf-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String ret = "";
            while ((ret = br.readLine()) != null) {
                if (ret != null && !"".equals(ret.trim())) {
                    result.append(new String(ret.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
                }
            }
            log.info("recv - {}", result);
            conn.disconnect();
            br.close();
        } catch (ConnectException e) {
            log.error("调用HttpUtils.sendSSLPost ConnectException, url=" + url + ",param=" + param, e);
        } catch (SocketTimeoutException e) {
            log.error("调用HttpUtils.sendSSLPost SocketTimeoutException, url=" + url + ",param=" + param, e);
        } catch (IOException e) {
            log.error("调用HttpUtils.sendSSLPost IOException, url=" + url + ",param=" + param, e);
        } catch (Exception e) {
            log.error("调用HttpsUtil.sendSSLPost Exception, url=" + url + ",param=" + param, e);
        }
        return result.toString();
    }

    private static class TrustAnyTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}

@Data
@NoArgsConstructor
class Encrypted {
    private String encrypted;
}