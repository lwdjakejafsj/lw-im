package io.luowei.im.sdk.listener;

import io.luowei.im.common.domain.model.IMSendResult;

/**
 * 消息监听接口
 * author: luowei
 * date:
 */
public interface MessageListener<T> {
    /**
     * 处理发送的结果
     */
    void doProcess(IMSendResult<T> result);
}
