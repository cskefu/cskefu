package com.cskefu.wechat.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TextMessage implements MessageRequest, MessageResponse {
    private String content;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String menu_id;

    @Builder
    public TextMessage(String content) {
        this.content = content;
    }
}
