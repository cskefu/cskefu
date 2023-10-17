package com.cskefu.wechat.response;

import com.cskefu.wechat.message.MessageItem;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
public class AsyncMessageResponse extends Response {
    private String next_cursor;
    private Integer has_more;
    private List<MessageItem> msg_list;

    public boolean hasMore() {
        return Integer.valueOf(1).equals(has_more);
    }
}
