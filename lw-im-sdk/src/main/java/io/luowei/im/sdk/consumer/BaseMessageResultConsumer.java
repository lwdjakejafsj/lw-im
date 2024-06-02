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
package io.luowei.im.sdk.consumer;

import com.alibaba.fastjson.JSONObject;
import io.luowei.im.common.domain.constants.IMConstants;
import io.luowei.im.common.domain.model.IMSendResult;

/**
 * author luowei
 * description 基础结果消费者类
 */
public class BaseMessageResultConsumer {

    /**
     * 解析数据
     */
    protected IMSendResult<?> getResultMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String eventStr = jsonObject.getString(IMConstants.MSG_KEY);
        return JSONObject.parseObject(eventStr, IMSendResult.class);
    }
}
