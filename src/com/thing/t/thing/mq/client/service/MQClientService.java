package com.thing.t.thing.mq.client.service;

import com.thing.t.thing.mq.dto.MQDetailDTO;

public interface MQClientService {
	
	public String dropMQMessageOnQueue(MQDetailDTO mqDetailDTO, String message);
	
}
