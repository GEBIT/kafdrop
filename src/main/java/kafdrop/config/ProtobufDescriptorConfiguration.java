package kafdrop.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import kafdrop.model.ProtobufDescriptorConfigVO;

@Configuration
public class ProtobufDescriptorConfiguration {
  @Component
  @ConfigurationProperties(prefix = "protobufdesc")
  public static final class ProtobufDescriptorProperties {
    private static final Logger LOG = LoggerFactory.getLogger(ProtobufDescriptorProperties.class);
    // the idea is to let user specifying a directory stored all descriptor file
    // the program will load and .desc file and show as an option on the message
    // detail screen
    private String directory;

    private Boolean parseAnyProto = Boolean.FALSE;
    
    /**
     * YAML configuration file for protobuf descriptors.
     */
    private String descriptorConfigFile;
    
    public String getDirectory() {
      return directory;
    }

    public void setDirectory(String directory) {
      this.directory = directory;
    }

    public Boolean getParseAnyProto() {
      return parseAnyProto;
    }

    public void setParseAnyProto(Boolean parseAnyProto) {
      this.parseAnyProto = parseAnyProto;
    }
    
	public String getDescriptorConfigFile() {
		return descriptorConfigFile;
	}
	
	public void setDescriptorConfigFile(String aDescriptorConfigFile) {
		descriptorConfigFile = aDescriptorConfigFile;
	}

	public List<ProtobufDescriptorConfigVO> getProtobufDescriptorConfigs() {
		if (descriptorConfigFile == null || Files.notExists(Path.of(descriptorConfigFile))) {
			LOG.info("No descriptor config file configured, skip the setting!!");
			return Collections.emptyList();
		}
		
		List<ProtobufDescriptorConfigVO> configs = new ArrayList();
		Map<String, Object> yamlContent = new HashMap();
		
		try {
			yamlContent = new Yaml().load(new FileInputStream(descriptorConfigFile));
		} catch (FileNotFoundException exc) {
			// ignored because we already checked if the file exists
		}
		
		List<Object> protoDescriptors = (List<Object>) yamlContent.get("proto_descriptors");
		
		for (Object protoDescriptor : protoDescriptors) {
			Map<String, Object> protoDescriptorMap = (Map<String, Object>) protoDescriptor;
			String destFile = (String) protoDescriptorMap.get("dest_file");
			String groupId = (String) protoDescriptorMap.get("group_id");
			String artifactId = (String) protoDescriptorMap.get("artifact_id");
			String version = (String) protoDescriptorMap.get("version");
			
			if (StringUtils.isBlank(destFile)) {
				LOG.warn("{}.{}.{} contains no dest_file entry", groupId, artifactId, version);
				continue;
			}
			
			String topic = destFile.replace(".desc", "");
			List<String> messageTypes = (List<String>) protoDescriptorMap.get("msg_types");
			
			if (messageTypes == null || messageTypes.isEmpty()) {
				LOG.debug("{}.{}.{} contains no msg_types entry", groupId, artifactId, version);
				messageTypes = Collections.emptyList();
			}
			
			configs.add(new ProtobufDescriptorConfigVO(topic, messageTypes));
		}
		
		return configs;
	}

	public List<String> getDescFilesList() {
      // getting file list
      if (directory == null || Files.notExists(Path.of(directory))) {
        LOG.info("No descriptor folder configured, skip the setting!!");
        return Collections.emptyList();
      }
      String[] pathnames;
      File path = new File(directory);

      // apply filter for listing only .desc file
      FilenameFilter filter = (dir, name) -> name.endsWith(".desc");

      pathnames = path.list(filter);
      return Arrays.asList(Objects.requireNonNull(pathnames));
    }
  }
}
