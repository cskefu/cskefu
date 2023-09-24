package com.cskefu.wechat.message;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MenuMessage implements MessageRequest {
    private String head_content;
    private List<Menu> list;
    private String tail_content;

    @Builder
    public MenuMessage(String head_content, List<Menu> list, String tail_content) {
        this.head_content = head_content;
        this.list = list;
        this.tail_content = tail_content;
    }
}

@Data
@NoArgsConstructor
class Menu {
    private String type;
    private Click click;

    @Builder
    public Menu(String type, Click click) {
        this.type = type;
        this.click = click;
    }
}

@Data
@NoArgsConstructor
class Click {
    private String id;
    private String content;

    @Builder
    public Click(String id, String content) {
        this.id = id;
        this.content = content;
    }
}
