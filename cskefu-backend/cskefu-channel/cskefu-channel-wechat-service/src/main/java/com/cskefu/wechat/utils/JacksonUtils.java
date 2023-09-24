package com.cskefu.wechat.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

public class JacksonUtils {
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        /**
         SimpleModule module = new SimpleModule();
         module.addSerializer(MessageSendRequest.class, new JsonSerializer<MessageSendRequest>() {
        @Override public void serialize(MessageSendRequest request, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        try {
        String messageType = request.getMsgtype().name().toLowerCase(Locale.ROOT);
        gen.writeStartObject();
        gen.writeStringField("touser", request.getTouser());
        gen.writeStringField("open_kfid", request.getOpen_kfid());
        gen.writeStringField("msgid", request.getMsgid());
        gen.writeStringField("msgtype", messageType);

        Class clazz = request.getClass();
        Field field = clazz.getDeclaredField(messageType);
        field.setAccessible(true);
        gen.writeObjectField(messageType);

        gen.writeEndObject();
        } catch (NoSuchFieldException | IllegalAccessException e) {
        e.printStackTrace();
        throw new RuntimeException("微信客服消息请求序列化失败！");
        }
        }
        });
         objectMapper.registerModule(module);
         **/
    }

    private JacksonUtils() {
        throw new RuntimeException("Unsupport operation");
    }

    public static <T> List<T> toList(@NonNull String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, getCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            String className = clazz.getSimpleName();
            throw new RuntimeException(" parse json " + json + " to class [" + className + "] error：" + e.getMessage() + "");
        }
    }

    private static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    public static <T> T parseObject(@NonNull String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            String className = clazz.getSimpleName();
            throw new RuntimeException(" parse json " + json + " to class [" + className + "] error：" + e.getMessage() + "");
        }
    }

    public static String toString(Object obj, Include include) {
        try {
            if (include != null) {
                objectMapper.setSerializationInclusion(include);
            }
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toString(Object obj) {
        return toString(obj, null);
    }

    public static <T> T toObject(String str, Class<T> clazz) {
        try {
            return objectMapper.readValue(str, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T toObject(String str, TypeReference<T> typeReference) {
        try {
            return StringUtils.isEmpty(str) ? null : objectMapper.readValue(str, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
