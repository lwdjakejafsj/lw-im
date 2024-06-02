/**
 * Copyright 2022-9999 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.luowei.im.sdk.client;


import io.luowei.im.common.domain.enums.IMTerminalType;
import io.luowei.im.common.domain.model.IMGroupMessage;
import io.luowei.im.common.domain.model.IMPrivateMessage;

import java.util.List;
import java.util.Map;

/**
 * author luowei
 * description IM客户端
 */
public interface IMClient {
    /**
     * 发送私聊消息
     */
    <T> void sendPrivateMessage(IMPrivateMessage<T> message);

    /**
     * 发送群消息
     */
    <T> void sendGroupMessage(IMGroupMessage<T> message);

    /**
     * 判断用户是否在线
     */
    Boolean isOnline(Long userId);

    /**
     * 筛选出在线的用户
     */
    List<Long> getOnlineUserList(List<Long> userIds);

    /**
     * 获取用户与其在线的终端列表
     */
    Map<Long,List<IMTerminalType>> getOnlineTerminal(List<Long> userIds);
}
