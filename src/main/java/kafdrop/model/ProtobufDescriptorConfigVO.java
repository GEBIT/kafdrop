package kafdrop.model;

import java.util.List;

public final class ProtobufDescriptorConfigVO {

	private String topic;
	private List<String> messageTypes;
	private boolean isAnyProto;
	
	public ProtobufDescriptorConfigVO(String topic, List<String> messageTypes, boolean isAnyProto) {
		this.topic = topic;
		this.messageTypes = messageTypes;
		this.isAnyProto = isAnyProto;
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

	public boolean isAnyProto() {
		return isAnyProto;
	}
	
	public void setAnyProto(boolean isAnyProto) {
		this.isAnyProto = isAnyProto;
	}
}
