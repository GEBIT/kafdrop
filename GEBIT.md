# Kafdrop (Gebit fork)

## Local Development

### Prerequisites
- Eclipse
- JDK 11
- finished and started the tilt setup

### Configuration
Create in your eclipse a debug configuration ("Java Application") for the kafdrop project and set the following VM arguments:

- `-Dspring.profiles.active=gebit`
- `-Dprotobufdesc.directory=pathToYour/.kafdrop_protobuf_descriptors`
- `-Dprotobufdesc.descriptorConfigFile=pathToYour/proto_descriptor_config.yml`

Note that `pathToYour` must be replaced with the correct path to the file/directory after this placeholder.

After starting this debug configuration, eclipse will connect your local kafdrop with the kafka which runs in the tilt cluster.

## Release
Start the [jenkins build](https://rp-build.local.gebit.de/job/Tools/job/kafdrop/) and update the kafdrop image version in the [tilt-core root Tiltfile](https://gitlab.local.gebit.de/rp/common/tilt/tilt-core/-/blob/master/Tiltfile).
