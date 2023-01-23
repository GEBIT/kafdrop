package kafdrop.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.protobuf.Any;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.util.JsonFormat.Printer;

public class ProtobufMessageDeserializer implements MessageDeserializer {

  private final String fullDescFile;
  private final String msgTypeName;
  private final boolean isAnyProto;
  private final ObjectMapper mapper;

  private static final Logger LOG = LoggerFactory.getLogger(ProtobufMessageDeserializer.class);

  public ProtobufMessageDeserializer(String fullDescFile, String msgTypeName, boolean isAnyProto, ObjectMapper mapper) {
    this.fullDescFile = fullDescFile;
    this.msgTypeName = msgTypeName;
    this.isAnyProto = isAnyProto;
    this.mapper = mapper;
  }

  @Override
  public String deserializeMessage(ByteBuffer buffer) {

    AtomicReference<String> msgTypeNameRef = new AtomicReference<>(msgTypeName);
    try (InputStream input = new FileInputStream(fullDescFile)) {
      FileDescriptorSet set = FileDescriptorSet.parseFrom(input);

      List<FileDescriptor> descs = new ArrayList<>();
      for (FileDescriptorProto ffdp : set.getFileList()) {
        FileDescriptor fd = Descriptors.FileDescriptor.buildFrom(
                ffdp,
                descs.toArray(new FileDescriptor[0]));
        descs.add(fd);
      }

      final var descriptors = descs.stream().flatMap(desc -> desc.getMessageTypes().stream()).collect(Collectors.toList());
      // automatically detect the message type name if the proto is "Any" and no message type name is given
      if (isAnyProto && msgTypeName.isBlank()) {
        String typeUrl = Any.parseFrom(buffer).getTypeUrl();
				String[] splittedTypeUrl = typeUrl.split("/");
				// the last part in the type url is always the FQCN for this proto
				msgTypeNameRef.set(splittedTypeUrl[splittedTypeUrl.length - 1]);
      }
      // check for full name too if the proto is "Any"
      final var messageDescriptor = descriptors.stream().filter(desc -> msgTypeNameRef.get().equals(desc.getName()) || msgTypeNameRef.get().equals(desc.getFullName())).findFirst();
      if (messageDescriptor.isEmpty()) {
        final String errorMsg = "Can't find specific message type: " + msgTypeNameRef.get();
        LOG.error(errorMsg);
        return buildErrorJson(errorMsg, null);
      }
      DynamicMessage message = null;
      if (isAnyProto) {
        // parse the value from "Any" proto instead of the "Any" proto itself
        message = DynamicMessage.parseFrom(messageDescriptor.get(), Any.parseFrom(buffer).getValue());
      } else {
        message = DynamicMessage.parseFrom(messageDescriptor.get(), CodedInputStream.newInstance(buffer));
      }

      JsonFormat.TypeRegistry typeRegistry = JsonFormat.TypeRegistry.newBuilder().add(descriptors).build();
      Printer printer = JsonFormat.printer().usingTypeRegistry(typeRegistry);

      String content = printer.print(message).replace("\n", ""); // must remove line break so it defaults to collapse mode
      ObjectNode messageJson = mapper.createObjectNode();
      
      ObjectNode metadata = mapper.createObjectNode();
      metadata.put("name", messageDescriptor.get().getName());
      metadata.put("fullName", messageDescriptor.get().getFullName());
      metadata.put("index", messageDescriptor.get().getIndex());
      
      messageJson.set("metadata", metadata);
      messageJson.set("content", mapper.readTree(content));
      
      return mapper.writeValueAsString(messageJson);
    } catch (FileNotFoundException e) {
      final String errorMsg = "Couldn't open descriptor file: " + fullDescFile;
      LOG.error(errorMsg, e);
      return buildErrorJson(errorMsg, e);
    } catch (IOException e) {
      final String errorMsg = "Can't decode Protobuf message";
      LOG.error(errorMsg, e);
      return buildErrorJson(errorMsg, e);
    } catch (DescriptorValidationException e) {
      final String errorMsg = "Can't compile proto message type: " + msgTypeNameRef.get();
      LOG.error(errorMsg, e);
      return buildErrorJson(errorMsg, e);
    }
  }

  private String buildErrorJson(String errorMsg, Throwable exception) {
	  try {
		ObjectNode exceptionJson = mapper.createObjectNode();
		exceptionJson.put("error_message", errorMsg);
		
		if (exception != null) {
			exceptionJson.put("exception", ExceptionUtils.getStackTrace(exception));
		}
		
		return mapper.writeValueAsString(exceptionJson);
	} catch (JsonProcessingException exc) {
		throw new RuntimeException(exc);
	}
  }
}
