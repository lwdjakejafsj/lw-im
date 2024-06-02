package io.luowei.im.sdk.multicaster.Impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import io.luowei.im.common.domain.enums.IMListenerType;
import io.luowei.im.common.domain.model.IMSendResult;
import io.luowei.im.sdk.annotation.IMListener;
import io.luowei.im.sdk.listener.MessageListener;
import io.luowei.im.sdk.multicaster.MessageListenerMulticaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

@Component
public class DefaultMessageListenerMulticaster implements MessageListenerMulticaster {

    @Autowired(required = false)
    private List<MessageListener> messageListenerList = Collections.emptyList();

    @Override
    public <T> void multicast(IMListenerType listenerType, IMSendResult<T> result) {
        if (CollectionUtil.isEmpty(messageListenerList)) {
            return;
        }

        messageListenerList.forEach(messageListener -> {
            IMListener listener = messageListener.getClass().getAnnotation(IMListener.class);
            if (listener != null && (IMListenerType.ALL.equals(listener.listenerType()) || listener.listenerType().equals(listenerType))) {
                if (result.getData() instanceof JSONObject) {
                    Type superInterface = messageListener.getClass().getGenericInterfaces()[0];
                    Type type = ((ParameterizedType)superInterface).getActualTypeArguments()[0];
                    JSONObject data = (JSONObject) result.getData();
                    result.setData(data.toJavaObject(type));
                }
                messageListener.doProcess(result);
            }
        });
    }
}
