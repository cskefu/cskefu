/*
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd.
 * <https://www.chatopera.com>, Licensed under the Chunsong Public
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cskefu.cc.peer;

import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.socketio.message.ChatMessage;
import org.apache.commons.lang3.StringUtils;

public class PeerUtils {

    /**
     * 过滤书写中的消息
     *
     * @param chatMessage
     * @return
     */
    public static boolean isMessageInWritting(final ChatMessage chatMessage) {
        return StringUtils.equals(
                chatMessage.getType(), Constants.IM_MESSAGE_TYPE_WRITING);
    }
}
