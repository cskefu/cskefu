package com.cskefu.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MessageTypeEnum {
    @JsonProperty("file")
    FILE,
    @JsonProperty("image")
    IMAGE,
    @JsonProperty("link")
    LINK,
    @JsonProperty("location")
    LOCATION,
    @JsonProperty("miniprogram")
    MINIPROGRAM,
    @JsonProperty("text")
    TEXT,
    @JsonProperty("video")
    VIDEO,
    @JsonProperty("voice")
    VOICE,
    @JsonProperty("msgmenu")
    MSGMENU,
    @JsonProperty("business_card")
    CARD;
}
