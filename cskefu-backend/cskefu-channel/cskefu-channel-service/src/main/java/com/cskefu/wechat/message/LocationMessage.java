package com.cskefu.wechat.message;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LocationMessage implements MessageRequest, MessageResponse {
    private Double latitude;
    private Double longitude;
    private String name;
    private String address;

    @Builder
    public LocationMessage(Double latitude, Double longitude, String name, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.address = address;
    }
}
