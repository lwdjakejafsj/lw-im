package io.luowei.im.server.consumer;

import com.alibaba.fastjson.JSONObject;
import io.luowei.im.common.domain.constants.IMConstants;
import io.luowei.im.common.domain.model.IMReceiveInfo;
import io.luowei.im.common.domain.model.IMSendInfo;

/**
 * 接收的消息
 * author: luowei
 * date:
 */
public class BaseMessageConsumer {

    public IMReceiveInfo getReceiveInfo(String msg) {
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String eventStr = jsonObject.getString(IMConstants.MSG_KEY);
        return JSONObject.parseObject(eventStr, IMReceiveInfo.class);
    }

}
