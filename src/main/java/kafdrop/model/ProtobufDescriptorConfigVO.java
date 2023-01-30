package kafdrop.model;

import java.util.List;

public final class ProtobufDescriptorConfigVO {

	private String topic;
	private List<String> messageTypes;
	
	public ProtobufDescriptorConfigVO(String topic, List<String> messageTypes) {
		this.topic = topic;
		this.messageTypes = messageTypes;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public List<String> getMessageTypes() {
		return messageTypes;
	}

	public void setMessageTypes(List<String> messageTypes) {
		this.messageTypes = messageTypes;
	}
}
