package com.cskefu.wechat;

import com.cskefu.wechat.response.AccessTokenResponse;
import com.cskefu.wechat.response.AsyncMessageResponse;
import com.cskefu.wechat.response.Response;
import com.cskefu.wechat.response.SendMessageResponse;
import com.cskefu.wechat.utils.JacksonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Slf4j
public class WechatKefuServiceImpl {
    private String accessToken = null;

    private String corpid = "ww61ff6708b9fcb411";
    //    private String corpsecret = "c6xCoH0GrEdS7hpK-ILDRxctHlrhTzCnAEB008LHn_8";//独立版
    private String corpsecret = "vCzWPyJvtN9p-qMCOCl0OzpXP8fYRCtbKr6-UqJEuFA";//联合版

    private String accessTokenUrl = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";
    private String syncMessageUrl = "https://qyapi.weixin.qq.com/cgi-bin/kf/sync_msg?access_token=%s";
    private String recallMessageUrl = "https://qyapi.weixin.qq.com/cgi-bin/kf/recall_msg?access_token=%s";
    private String sendMessageOnEventUrl = "https://qyapi.weixin.qq.com/cgi-bin/kf/send_msg_on_event?access_token=%s";
    private String sendMessageUrl = "https://qyapi.weixin.qq.com/cgi-bin/kf/send_msg?access_token=%s";

    private String corpidUnion = corpid;

    List<MessageTypeEnum> messageTypeEnums = Arrays.asList(MessageTypeEnum.MSGMENU, MessageTypeEnum.TEXT);

    private Header[] headers = new BasicHeader[]{
            new BasicHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE),
            new BasicHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE)
    };

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public SendMessageResponse sendMessage(MessageSendRequest request) {
        return retryOnceBecauseOfTokenExpirePostRequest(sendMessageUrl, headers, request, SendMessageResponse.class);
    }

    public Response recallMessage(RecallMessageRequest request) {
        return retryOnceBecauseOfTokenExpirePostRequest(recallMessageUrl, headers, request, Response.class);
    }

    public AsyncMessageResponse syncMessage(Map<String, Object> param) {
        return retryOnceBecauseOfTokenExpirePostRequest(syncMessageUrl, null, param, AsyncMessageResponse.class);
    }

    public SendMessageResponse sendMessageOnEvent(MessageSendRequest request, String code) {
        if (!messageTypeEnums.contains(request.getMsgtype())) {
            throw new RuntimeException("仅支持菜单消息和菜单消息");
        }
        Map<String, Object> param = new HashMap<>();
        param.put("code", code);
        param.put("msgid", request.getMsgid());
        param.put("msgtype", request.getMsgtype());
        param.put(request.getMsgtype().name().toLowerCase(Locale.ROOT), request.getMsgmenu());
        return retryOnceBecauseOfTokenExpirePostRequest(sendMessageOnEventUrl, headers, param, SendMessageResponse.class);
    }

    private String accessToken() {
        if (StringUtils.isNotEmpty(this.accessToken)) {
            return this.accessToken;
        }
        synchronized (this) {
            String result = request(new HttpGet(String.format(accessTokenUrl, corpid, corpsecret)), headers);
            try {
                AccessTokenResponse response = objectMapper.readValue(result, AccessTokenResponse.class);
                this.accessToken = response.getAccess_token();
                return this.accessToken;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                throw new RuntimeException("access_token接口响应转换失败");
            }
        }
    }

    private <T extends Response> T retryOnceBecauseOfTokenExpirePostRequest(String rawUrl, Header[] headers, Object param, Class<T> responseClass) {
        HttpPost http = new HttpPost();
        http.setEntity(new StringEntity(JacksonUtils.toString(param), StandardCharsets.UTF_8));
        return retryOnceBecauseOfTokenExpireRequest(http, rawUrl, headers, responseClass);
    }

    private <T extends Response> T retryOnceBecauseOfTokenExpireGetRequest(String rawUrl, Header[] headers, Class<T> responseClass) {
        return retryOnceBecauseOfTokenExpireRequest(new HttpGet(), rawUrl, headers, responseClass);
    }

    private <T extends Response> T retryOnceBecauseOfTokenExpireRequest(HttpRequestBase http, String rawUrl, Header[] headers, Class<T> responseClass) {
        http.setURI(URI.create(String.format(rawUrl, accessToken())));
        String result = request(http, headers);
        try {
            T response = objectMapper.readValue(result, responseClass);
            if (response.success()) {
                return response;
            } else if (response.tokenExpire()) {
                this.accessToken = null;
                http.setURI(URI.create(String.format(rawUrl, accessToken())));
                result = request(http, headers);
                response = objectMapper.readValue(result, responseClass);
                if (response.success()) {
                    return response;
                }
            }
            log.info("微信客服接口调用失败：" + result);
            throw new RuntimeException("微信客服接口调用失败：" + (StringUtils.isNotEmpty(response.getErrmsg()) ? response.getErrmsg() : ""));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("微信客服接口响应转换失败");
        }
    }

    private String request(HttpRequestBase http, Header[] headers) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        if (headers != null) {
            http.setHeaders(headers);
        }
        try {
            CloseableHttpResponse response = httpClient.execute(http);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (HttpStatus.OK.value() == statusCode) {
                return EntityUtils.toString(response.getEntity());
            }
            throw new RuntimeException("调用微信客服接口 " + http.getURI() + " 失败：" + statusCode);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO liuyong，接口调用失败，这里可能的原因是网络问题
            throw new RuntimeException("调用微信客服接口 " + http.getURI() + " 失败：" + e.getMessage());
        }
    }

//    public static String sslPost(String url, String param) {
//        StringBuilder result = new StringBuilder();
//        String urlNameString = url + "?" + param;
//        try {
//            log.info("sendSSLPost - {}", urlNameString);
//            SSLContext sc = SSLContext.getInstance("SSL");
//            sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
//            URL console = new URL(urlNameString);
//            HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
//            conn.setRequestProperty("accept", "*/*");
//            conn.setRequestProperty("connection", "Keep-Alive");
//            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
//            conn.setRequestProperty("Accept-Charset", "utf-8");
//            conn.setRequestProperty("contentType", "utf-8");
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
//
//            conn.setSSLSocketFactory(sc.getSocketFactory());
//            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
//            conn.connect();
//            InputStream is = conn.getInputStream();
//            BufferedReader br = new BufferedReader(new InputStreamReader(is));
//            String ret = "";
//            while ((ret = br.readLine()) != null) {
//                if (ret != null && !"".equals(ret.trim())) {
//                    result.append(new String(ret.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
//                }
//            }
//            log.info("recv - {}", result);
//            conn.disconnect();
//            br.close();
//        } catch (ConnectException e) {
//            log.error("调用HttpUtils.sendSSLPost ConnectException, url=" + url + ",param=" + param, e);
//        } catch (SocketTimeoutException e) {
//            log.error("调用HttpUtils.sendSSLPost SocketTimeoutException, url=" + url + ",param=" + param, e);
//        } catch (IOException e) {
//            log.error("调用HttpUtils.sendSSLPost IOException, url=" + url + ",param=" + param, e);
//        } catch (Exception e) {
//            log.error("调用HttpsUtil.sendSSLPost Exception, url=" + url + ",param=" + param, e);
//        }
//        return result.toString();
//    }
//
//    private static class TrustAnyTrustManager implements X509TrustManager {
//        @Override
//        public void checkClientTrusted(X509Certificate[] chain, String authType) {
//        }
//
//        @Override
//        public void checkServerTrusted(X509Certificate[] chain, String authType) {
//        }
//
//        @Override
//        public X509Certificate[] getAcceptedIssuers() {
//            return new X509Certificate[]{};
//        }
//    }
//
//    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
//        @Override
//        public boolean verify(String hostname, SSLSession session) {
//            return true;
//        }
//    }
//   @Override
//    public Long uploadFile(Long fileId) {
//        Header[] headers = new BasicHeader[]{
//                new BasicHeader(HttpHeaders.AUTHORIZATION, accessToken()),
//                new BasicHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE)
//        };
//        String url = "/files/upload";
//        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//        builder.setCharset(StandardCharsets.UTF_8);
//
//        File file = FileUtil.getFileById(fileId);
//        builder.addPart("storageUsage", new StringBody("ENVELOPE", ContentType.TEXT_PLAIN));
//        builder.addPart("filename", new StringBody(file.getName(), ContentType.TEXT_PLAIN));
//        builder.addPart("type", new StringBody("001", ContentType.TEXT_PLAIN));
//        builder.addPart("useType", new StringBody("SIGN", ContentType.TEXT_PLAIN));
//        try {
//            builder.addPart("file", new InputStreamBody(new FileInputStream(file.getAbsolutePath()), ContentType.APPLICATION_OCTET_STREAM, file.getName()));
//            JSONObject jsonObject = postHttp(url, builder.build(), headers);
//
//            if (String.valueOf(HttpStatus.OK.value()).equals(jsonObject.getString("code"))) {
//                return jsonObject.getJSONObject("content").getLong("fileId");
//            } else if (String.valueOf(HttpStatus.UNAUTHORIZED.value()).equals(jsonObject.getString("code"))) {
//                // accessToken可能过期了，重试一次
//                this.accessToken = null;
//                headers[0] = new BasicHeader(HttpHeaders.AUTHORIZATION, accessToken());
//                jsonObject = postHttp(url, builder.build(), headers);
//                if (String.valueOf(HttpStatus.OK.value()).equals(jsonObject.getString("code"))) {
//                    return jsonObject.getJSONObject("content").getLong("fileId");
//                }
//            }
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new EbsException("上传文件到大家签失败！");
//        }
//    }

//    @Override
//    public ResponseEntity<byte[]> download(Long envelopeId) {
//        try {
//            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//            String url = "/envelopes/download?envelopeId=" + envelopeId;
//            HttpGet http = new HttpGet(url(url));
//            Header[] headers = new BasicHeader[]{
//                    new BasicHeader(HttpHeaders.AUTHORIZATION, accessToken()),
//                    new BasicHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
//            };
//            http.setHeaders(headers);
//            CloseableHttpResponse response = httpClient.execute(http);
//            if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
//                InputStream inputStream = response.getEntity().getContent();
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                byte[] buffer = new byte[1024];
//                int len;
//                while ((len = inputStream.read(buffer)) != -1) {
//                    bos.write(buffer, 0, len);
//                }
//                bos.close();
//                inputStream.close();
//                MultiValueMap<String, String> responseHeaders = new HttpHeaders();
//                responseHeaders.add("Content-Disposition", "attachment;");
//                return new ResponseEntity<>(bos.toByteArray(), responseHeaders, HttpStatus.OK);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new EbsException("签名文件下载失败");
//        }
//        throw new EbsException("签名文件下载失败");
//    }
}
