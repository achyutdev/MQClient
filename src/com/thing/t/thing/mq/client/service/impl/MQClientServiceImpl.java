package com.thing.t.thing.mq.client.service.impl;

import com.thing.t.thing.mq.client.MQClient;
import com.thing.t.thing.mq.client.service.MQClientService;
import com.thing.t.thing.mq.dto.MQDetailDTO;

public class MQClientServiceImpl implements MQClientService{

	@Override
	public String dropMQMessageOnQueue(MQDetailDTO mqDetailDTO, String message) {
		MQClient client = new MQClient();
		return client.putMessageOnMQ(mqDetailDTO, message); 
	}

}
