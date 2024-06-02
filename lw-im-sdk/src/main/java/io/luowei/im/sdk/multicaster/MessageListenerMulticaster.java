package io.luowei.im.sdk.multicaster;

import io.luowei.im.common.domain.enums.IMListenerType;
import io.luowei.im.common.domain.model.IMSendResult;

/**
 * 广播消息
 * author: luowei
 * date:
 */
public interface MessageListenerMulticaster {
    <T> void multicast(IMListenerType type, IMSendResult<T> result);
}
