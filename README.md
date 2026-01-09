# Tracer Implementation

A demo project that shows how to implement a tracer for the Liberty `mpOpentracing-3.0` feature.
Once implemented as an OSGI bundle, a tracer can be included as a user feature that will automatically
pull the `mpOpentracing-3.0` feature.

Building this project relies on the `bnd-process` plugin to process the `bnd.bnd` file which will generate
the `MANIFEST.MF` to be included in the project. Additionally, annotations found in the [OpentracingZipkinTracerFactory.java](src/main/java/com/ibm/ws/opentracing/zipkin/OpentracingZipkinTracerFactory.java)
provide information for the manifest. If you decide to roll your own tracer, then you'll need to ensure your factory
implementation provides these annotations to correctly generate the manifest file.

## opentracingZipkin

To compile and package, run:

    mvn package

This will build `target/liberty-opentracing-zipkintracer-3.0-sample.zip`. Copy this file to `${wlp.user.dir}`
and `unzip` it to install the feature extension:

    $ cp target/liberty-opentracing-zipkintracer-3.0-sample.zip ${WLP}/usr/
    $ cd ${WLP}/usr/
    $ unzip liberty-opentracing-zipkintracer-3.0-sample.zip
    $ ls -R extension/
    extension/:
    lib
    
    extension/lib:
    com.ibm.ws.io.opentracing.zipkintracer-0.33.jar  features
    
    extension/lib/features:
    opentracingZipkin-0.33.mf

Enable the feature in server.xml:

    <featureManager>
      [...]
      <feature>usr:opentracingZipkin-0.33</feature>
    </featureManager>

Finally, enable the tracer with a configuration element (this defaults to a server at `http://zipkin:9411/`):

    <opentracingZipkin />

### Configuring the tracer

The `opentracingZipkin` element supports configuration of the zipkin server host and port,
and detailed configuration of the zipkin builder and reporter. This support is provided by
the `Config` class in [OpentracingZipkinTracerFactory.java](src/main/java/com/ibm/ws/opentracing/zipkin/OpentracingZipkinTracerFactory.java).
The options are localized and described in detail in [metatype.properties](src/main/resources/OSGI-INF/i10n/metatype.properties).

For example, to specify a different host and port:

    <opentracingZipkin host="myhost.com" port="4433" />

## Publishing to Maven Central

This project uses the **Maven Central Portal** for publishing releases. The publishing process uses manual review and approval for maximum control.

### Quick Start

1. **Deploy to Central Portal:**
   ```bash
   mvn clean deploy -P central-release
   ```

2. **Review and Publish:**
   - Log in to https://central.sonatype.com/
   - Navigate to "Deployments"
   - Review your deployment
   - Click "Publish" to release to Maven Central

### Prerequisites

- Maven Central Portal account with verified namespace
- User token configured in `~/.m2/settings.xml`
- GPG key for signing artifacts

For detailed instructions, see [MIGRATION.md](MIGRATION.md).

### Maven Dependency

Once published, users can include this tracer in their projects:

```xml
<dependency>
    <groupId>net.wasdev.wlp.tracer</groupId>
    <artifactId>liberty-opentracing-zipkintracer</artifactId>
    <version>3.0.2</version>
</dependency>
```

Available on Maven Central: https://central.sonatype.com/artifact/net.wasdev.wlp.tracer/liberty-opentracing-zipkintracer
