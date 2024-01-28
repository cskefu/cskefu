package com.cskefu.wechat.message;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ShopOrderMessage implements MessageResponse {
    private String order_id;
    private String product_titles;
    private String price_wording;
    private String state;
    private String image_url;
    private String shop_nickname;
}