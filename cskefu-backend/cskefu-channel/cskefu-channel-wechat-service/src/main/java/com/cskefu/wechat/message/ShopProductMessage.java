package com.cskefu.wechat.message;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ShopProductMessage implements MessageResponse {
    private String product_id;
    private String head_image;
    private String title;
    private String sales_price;
    private String shop_nickname;
    private String shop_head_image;
}
