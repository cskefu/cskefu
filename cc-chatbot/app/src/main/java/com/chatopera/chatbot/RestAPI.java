package com.chatopera.chatbot;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * RestAPI接口
 */
public class RestAPI {
    private static final Logger logger = LoggerFactory.getLogger(RestAPI.class);

    /**
     * patch headers
     *
     * @param headers
     */
    private static void x(HashMap<String, String> headers) {
        if (headers == null) {
            headers = new HashMap<String, String>();
            headers.put("accept", "application/json");
            return;
        }

        if (!headers.containsKey("Content-Type"))
            headers.put("Content-Type", "application/json");


        if (!headers.containsKey("accept"))
            headers.put("accept", "application/json");
    }


    /**
     * Post
     *
     * @param url
     * @param body
     * @param query
     * @param headers
     * @return
     * @throws UnirestException
     */
    public static JSONObject post(final String url, final HashMap<String, Object> body, final HashMap<String, Object> query, HashMap<String, String> headers) throws UnirestException {
        HttpRequestWithBody request = Unirest.post(url);
        logger.info("post body {}", body.toString());
        x(headers);
        HttpResponse<JsonNode> resp = request
                .headers(headers)
                .queryString(query)
                .fields(body)
                .asJson();

        // parse response
        JSONObject obj = resp.getBody().getObject();
        return obj;
    }

    public static JSONObject post(final String url, final HashMap<String, Object> body) throws UnirestException {
        return post(url, body, null, null);
    }

    /**
     * Get
     *
     * @param url
     * @param queryString
     * @param headers
     * @return
     * @throws UnirestException
     */
    public static JSONObject get(final String url, final HashMap<String, Object> queryString, HashMap<String, String> headers) throws UnirestException {
        GetRequest request = Unirest.get(url);
        x(headers);
        HttpResponse<JsonNode> resp = request
                .headers(headers)
                .queryString(queryString)
                .asJson();
        // parse response
        JSONObject obj = resp.getBody().getObject();
        return obj;
    }

    public static JSONObject get(final String url) throws UnirestException {
        return get(url, null, null);
    }

    public static JSONObject get(final String url, HashMap<String, Object> queryString) throws UnirestException {
        return get(url, queryString, null);
    }
}
