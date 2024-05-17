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

import java.util.List;

/**
 * author luowei
 * description 接收到的信息
 */
public class IMReceiveInfo extends TopicMessage {

    private static final long serialVersionUID = -7962158433664656629L;
    /**
     * 命令类型 IMCmdType枚举的值
     */
    private Integer cmd;

    /**
     * 发送消息的用户
     */
    private IMUserInfo sender;

    /**
     * 接收消息的用户列表
     */
    List<IMUserInfo> receivers;

    /**
     *  是否需要回调发送结果
     */
    private Boolean sendResult;

    /**
     * 推送消息体
     */
    private Object data;

    public IMReceiveInfo() {
    }

    public IMReceiveInfo(Integer cmd, IMUserInfo sender, List<IMUserInfo> receivers, Boolean sendResult, Object data) {
        this.cmd = cmd;
        this.sender = sender;
        this.receivers = receivers;
        this.sendResult = sendResult;
        this.data = data;
    }

    public Integer getCmd() {
        return cmd;
    }

    public void setCmd(Integer cmd) {
        this.cmd = cmd;
    }

    public IMUserInfo getSender() {
        return sender;
    }

    public void setSender(IMUserInfo sender) {
        this.sender = sender;
    }

    public List<IMUserInfo> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<IMUserInfo> receivers) {
        this.receivers = receivers;
    }

    public Boolean getSendResult() {
        return sendResult;
    }

    public void setSendResult(Boolean sendResult) {
        this.sendResult = sendResult;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
