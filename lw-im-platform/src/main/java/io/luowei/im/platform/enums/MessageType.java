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
package io.luowei.im.platform.enums;

/**
 * author luowei
 * description 消息类型
 */
public enum MessageType {

    TEXT(0,"文字"),
    IMAGE(1,"图片"),
    FILE(2,"文件"),
    AUDIO(3,"音频"),
    VIDEO(4,"视频"),
    RECALL(10,"撤回"),
    READED(11, "已读"),

    RTC_CALL(101,"呼叫"),
    RTC_ACCEPT(102,"接受"),
    RTC_REJECT(103, "拒绝"),
    RTC_CANCEL(104,"取消呼叫"),
    RTC_FAILED(105,"呼叫失败"),
    RTC_HANDUP(106,"挂断"),
    RTC_CANDIDATE(107,"同步candidate");

    private final Integer code;

    private final String desc;

    MessageType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer code(){
        return this.code;
    }
}
