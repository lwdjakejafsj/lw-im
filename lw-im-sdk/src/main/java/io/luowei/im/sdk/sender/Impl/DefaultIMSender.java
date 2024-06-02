package io.luowei.im.sdk.sender.Impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import io.luowei.im.common.cache.distribute.DistributedCacheService;
import io.luowei.im.common.domain.constants.IMConstants;
import io.luowei.im.common.domain.enums.IMCmdType;
import io.luowei.im.common.domain.enums.IMListenerType;
import io.luowei.im.common.domain.enums.IMSendCode;
import io.luowei.im.common.domain.enums.IMTerminalType;
import io.luowei.im.common.domain.model.*;
import io.luowei.im.common.mq.MessageSenderService;
import io.luowei.im.sdk.multicaster.MessageListenerMulticaster;
import io.luowei.im.sdk.sender.IMSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DefaultIMSender implements IMSender {

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Autowired
    private MessageSenderService messageSenderService;

    @Autowired
    private MessageListenerMulticaster messageListenerMulticaster;


    @Override
    public <T> void sendPrivateMessage(IMPrivateMessage<T> message) {
        if (message == null) {
            return;
        }
        List<Integer> receiveTerminals = message.getReceiveTerminals();

        if (CollectionUtil.isNotEmpty(receiveTerminals)) {
            // 同步自己的其他终端
            sendPrivateMessageToSelf(message,receiveTerminals);
            // 将消息发送给接收者的所有终端
            sendPrivateMessageToTargetUser(message,receiveTerminals);
        }
    }

    // 同步消息到其他终端
    private <T> void sendPrivateMessageToSelf(IMPrivateMessage<T> message , List<Integer> receiveTerminals) {
        // TODO
    }

    // 发送消息
    private <T> void sendPrivateMessageToTargetUser(IMPrivateMessage<T> message , List<Integer> receiveTerminals) {
        receiveTerminals.forEach(receiveTerminal -> {
            // 得到接收消息的用户所在的服务
            String redisKey = String.join(IMConstants.REDIS_KEY_SPLIT
                                        ,IMConstants.IM_USER_SERVER_ID
                                        ,message.getReceiveId().toString()
                                        ,receiveTerminal.toString());
            String serverId = distributedCacheService.get(redisKey);

            // 对应的用户在线
            if (StrUtil.isNotBlank(serverId)) {
                String sendKey = String.join(IMConstants.REDIS_KEY_SPLIT
                                            ,IMConstants.IM_MESSAGE_PRIVATE_QUEUE
                                            ,serverId);
                IMReceiveInfo receiveInfo = new IMReceiveInfo(IMCmdType.PRIVATE_MESSAGE.code()
                                                            ,message.getSender()
                                                            ,Collections.singletonList(new IMUserInfo(message.getReceiveId(),receiveTerminal))
                                                            ,message.getSendResult()
                                                            ,message.getData());
                receiveInfo.setDestination(sendKey);
                messageSenderService.send(receiveInfo);
            } else if (BooleanUtil.isTrue(message.getSendResult())) {
                //回复消息的状态
                IMSendResult<T> result = new IMSendResult<>(message.getSender(), new IMUserInfo(message.getReceiveId(), receiveTerminal), IMSendCode.NOT_ONLINE.code(), message.getData());
                messageListenerMulticaster.multicast(IMListenerType.PRIVATE_MESSAGE, result);
            }

        });
    }

    @Override
    public <T> void sendGroupMessage(IMGroupMessage<T> message) {
        Map<String, IMUserInfo> userTerminalGroup = this.getUserTerminalGroup(message);

        if (CollectionUtil.isEmpty(userTerminalGroup)) {
            return;
        }

        // 通过批量操作得到每一个用户对应的serverId，serverId就是不在线
        // TODO 这里可能会有问题，这个集合对顺序性要求很严格
        List<String> serverIdList = distributedCacheService.multiGet(userTerminalGroup.keySet());

        //将接收方按照服务Id进行分组，Key-服务ID，Value-接收消息的用户列表
        Map<Integer, List<IMUserInfo>> serverMap = new HashMap<>();
        //离线用户列表
        List<IMUserInfo> offlineUserList = new ArrayList<>();

        int ids = 0;
        for (Map.Entry<String,IMUserInfo> entry : userTerminalGroup.entrySet()) {
            String serverIdStr = serverIdList.get(ids++);
            if (StrUtil.isNotBlank(serverIdStr)) {
                // 用户再线
                List<IMUserInfo> list = serverMap.computeIfAbsent(Integer.parseInt(serverIdStr), o -> new LinkedList<>());
                list.add(entry.getValue());
            } else {
                // 用户离线
                offlineUserList.add(entry.getValue());
            }
        }

        //向群组其他成员发送消息
        this.sendGroupMessageToOtherUsers(serverMap, offlineUserList, message);
        //推送给自己的其他终端
        this.sendGroupMessageToSelf(message);

    }

    private <T> Map<String, IMUserInfo> getUserTerminalGroup(IMGroupMessage<T> message) {
        Map<String, IMUserInfo> map = new HashMap<>();
        if (message == null){
            return map;
        }
        for (Integer terminal : message.getReceiveTerminals()) {
            message.getReceiveIds().forEach(receiveId -> {
                String key = String.join(IMConstants.REDIS_KEY_SPLIT,IMConstants.IM_USER_SERVER_ID,receiveId.toString(),terminal.toString());
                map.put(key,new IMUserInfo(receiveId, terminal));
            });
        }
        return map;
    }

    private <T> void sendGroupMessageToSelf(IMGroupMessage<T> message) {
        //TODO
    }

    private <T> void sendGroupMessageToOtherUsers(Map<Integer, List<IMUserInfo>> serverMap, List<IMUserInfo> offlineUserList, IMGroupMessage<T> message) {
        for (Map.Entry<Integer, List<IMUserInfo>> entry : serverMap.entrySet()){
            IMReceiveInfo imReceiveInfo = new IMReceiveInfo(IMCmdType.GROUP_MESSAGE.code(), message.getSender(), new ArrayList<>(entry.getValue()), message.getSendResult(), message.getData());
            String sendKey = String.join(IMConstants.MESSAGE_KEY_SPLIT, IMConstants.IM_MESSAGE_GROUP_QUEUE, entry.getKey().toString());
            imReceiveInfo.setDestination(sendKey);
            messageSenderService.send(imReceiveInfo);
        }
        //回复离线用户消息状态
        if (message.getSendResult()){
            offlineUserList.forEach((offlineUser) -> {
                IMSendResult<T> result = new IMSendResult<>(message.getSender(), offlineUser, IMSendCode.NOT_ONLINE.code(), message.getData());
                messageListenerMulticaster.multicast(IMListenerType.GROUP_MESSAGE, result);
            });
        }
    }

    @Override
    public Map<Long, List<IMTerminalType>> getOnlineTerminal(List<Long> userIds) {
        if (CollectionUtil.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        List<Integer> codes = IMTerminalType.codes();
        Map<String,IMUserInfo> userMap = new HashMap<>();
        for (Long id : userIds) {
            for (Integer code : codes) {
                String key = String.join(IMConstants.REDIS_KEY_SPLIT, IMConstants.IM_USER_SERVER_ID,id.toString(),code.toString());
                userMap.put(key,new IMUserInfo(id,code));
            }
        }

        //从Redis批量获取数据
        List<String> serverIdList = distributedCacheService.multiGet(userMap.keySet());
        int idx = 0;
        Map<Long, List<IMTerminalType>> onlineMap = new HashMap<>();
        for (Map.Entry<String, IMUserInfo> entry : userMap.entrySet()){
            if (!StrUtil.isEmpty(serverIdList.get(idx++))){
                IMUserInfo imUserInfo = entry.getValue();
                List<IMTerminalType> imTerminalTypeList = onlineMap.computeIfAbsent(imUserInfo.getUserId(), o -> new ArrayList<>());
                imTerminalTypeList.add(IMTerminalType.fromCode(imUserInfo.getTerminal()));

            }
        }
        return onlineMap;
    }

    @Override
    public Boolean isOnline(Long userId) {
        String redisKey = String.join(IMConstants.REDIS_KEY_SPLIT,IMConstants.IM_USER_SERVER_ID,userId.toString(),"*");
        Set<String> keys = distributedCacheService.keys(redisKey);
        return CollectionUtil.isNotEmpty(keys);
    }

    @Override
    public List<Long> getOnlineUser(List<Long> userIds) {
        return new LinkedList<>(this.getOnlineTerminal(userIds).keySet());
    }
}
