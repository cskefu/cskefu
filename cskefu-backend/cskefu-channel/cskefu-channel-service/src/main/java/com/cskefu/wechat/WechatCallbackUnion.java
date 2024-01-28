package com.cskefu.wechat;

import com.cskefu.wechat.message.MessageItem;
import com.cskefu.wechat.response.AsyncMessageResponse;
import com.cskefu.wechat.utils.MessageCrypt;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wechat/kefu/union")
@Slf4j
public class WechatCallbackUnion {

    @Autowired
    private WechatKefuServiceImpl wechatKefuServiceImpl;

    @RequestMapping(value = "/notice")
    public String notice(HttpServletRequest request, @RequestBody(required = false) String postData) {
        val msg_signature = request.getParameter("msg_signature");
        val echostr = request.getParameter("echostr");
        val nonce = request.getParameter("nonce");
        val timestamp = request.getParameter("timestamp");
        String token = "aHBvj9oXeVBwQlyDMLRz8Xm5xnwg15";
        String encodingAesKey = "dULFVwHqBj2joSpieQREMKbLKMwwxBxoVVIb8t4OGVV";
        String receiveid = "ww61ff6708b9fcb411";
        try {
            MessageCrypt messageCrypt = new MessageCrypt(token, encodingAesKey, receiveid);
            if (HttpMethod.GET.name().equals(request.getMethod())) {//初次配置时的验证使用GET，消息通知使用POST
                return messageCrypt.verifyURL(msg_signature, timestamp, nonce, echostr);
            }
            String notice = messageCrypt.decryptMessage(msg_signature, timestamp, nonce, postData);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            StringReader reader = new StringReader(notice);
            InputSource source = new InputSource(reader);
            Document document = builder.parse(source);

            Element root = document.getDocumentElement();
            String message_token = root.getElementsByTagName("Token").item(0).getTextContent();
            Map<String, Object> param = new HashMap<>();
            // TODO liuyong 保存cursor和open_kfid，到数据库
//            param.put("cursor", "4gw7MepFLfgF2VC5npf");//上一次调用时返回的next_cursor，第一次拉取可以不填。不多于64字节
//            param.put("open_kfid", "0");//指定拉取某个客服账号的消息，否则默认返回所有客服账号的消息
            param.put("token", message_token);
            param.put("limit", 10);// 期望请求的数据量，默认值和最大值都为1000。 注意：可能会出现返回条数少于limit的情况，需结合返回的has_more字段判断是否继续请求。
            param.put("voice_format", "0");//语音消息类型，0-Amr 1-Silk，默认0。可通过该参数控制返回的语音格式
            AsyncMessageResponse response = null;
//            do {
            response = wechatKefuServiceImpl.syncMessage(param);
            List<MessageItem> msg_list = response.getMsg_list();
            if (!CollectionUtils.isEmpty(msg_list)) {
                for (MessageItem messageItem : msg_list) {
                    // TODO 消息入库
                    log.error("message {}", new ObjectMapper().writeValueAsString(messageItem));
                    // TODO 消息发送给春松客服坐席
                }
            }
//            } while (response.hasMore());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "200";
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
