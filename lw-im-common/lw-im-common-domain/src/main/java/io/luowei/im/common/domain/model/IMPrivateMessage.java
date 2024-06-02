package io.luowei.im.common.domain.model;

import io.luowei.im.common.domain.enums.IMTerminalType;

import java.util.List;

/**
 * 私聊消息
 * author: luowei
 * date:
 */
public class IMPrivateMessage<T> {
    /**
     * 发消息的用户
     */
    private IMUserInfo sender;

    /**
     * 接收消息的用户id
     */
    private Long receiveId;

    /**
     * 接收者的终端类型
     */
    private List<Integer> receiveTerminals = IMTerminalType.codes();

    /**
     * 是否发送给自己的其他终端,默认true
     */
    private Boolean sendToSelf = true;

    /**
     * 是否需要回推发送结果,默认true
     */
    private Boolean sendResult = true;

    /**
     *  消息内容
     */
    private T data;

    public IMPrivateMessage() {
    }

    public IMPrivateMessage(IMUserInfo sender, Long receiveId, List<Integer> receiveTerminals, Boolean sendToSelf, Boolean sendResult, T data) {
        this.sender = sender;
        this.receiveId = receiveId;
        this.receiveTerminals = receiveTerminals;
        this.sendToSelf = sendToSelf;
        this.sendResult = sendResult;
        this.data = data;
    }

    public IMUserInfo getSender() {
        return sender;
    }

    public void setSender(IMUserInfo sender) {
        this.sender = sender;
    }

    public Long getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(Long receiveId) {
        this.receiveId = receiveId;
    }

    public List<Integer> getReceiveTerminals() {
        return receiveTerminals;
    }

    public void setReceiveTerminals(List<Integer> receiveTerminals) {
        this.receiveTerminals = receiveTerminals;
    }

    public Boolean getSendToSelf() {
        return sendToSelf;
    }

    public void setSendToSelf(Boolean sendToSelf) {
        this.sendToSelf = sendToSelf;
    }

    public Boolean getSendResult() {
        return sendResult;
    }

    public void setSendResult(Boolean sendResult) {
        this.sendResult = sendResult;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
