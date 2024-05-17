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
package io.luowei.im.common.domain.model;

/**
 * author luowei
 * description
 */
public class IMSendResult<T> extends TopicMessage {

    private static final long serialVersionUID = -1235468585098257903L;
    /**
     * 发送消息的用户
     */
    private IMUserInfo sender;

    /**
     * 接收消息的用户
     */
    private IMUserInfo receiver;

    /**
     * 发送状态 IMCmdType
     */
    private Integer code;

    /**
     *  消息内容
     */
    private T data;

    public IMSendResult() {
    }

    public IMSendResult(IMUserInfo sender, IMUserInfo receiver, Integer code, T data) {
        this.sender = sender;
        this.receiver = receiver;
        this.code = code;
        this.data = data;
    }

    public IMUserInfo getSender() {
        return sender;
    }

    public void setSender(IMUserInfo sender) {
        this.sender = sender;
    }

    public IMUserInfo getReceiver() {
        return receiver;
    }

    public void setReceiver(IMUserInfo receiver) {
        this.receiver = receiver;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
