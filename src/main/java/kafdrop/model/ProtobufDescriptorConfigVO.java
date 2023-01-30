package kafdrop.model;

public final class ProtobufDescriptorConfigVO {

	private String topic;
	private String messageType;
	
	public ProtobufDescriptorConfigVO(String topic, String messageType) {
		this.topic = topic;
		this.messageType = messageType;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
}
