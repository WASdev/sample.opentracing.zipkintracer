# Tracer Implementation

A demo project that shows how to implement a tracer for the Liberty `mpOpentracing-2.0` feature.
Once implemented as an OSGI bundle, a tracer can be included as a user feature that will automatically
pull the `mpOpentracing-2.0` feature. 

Building this project relies on the `bnd-process` plugin to process the `bnd.bnd` file which will generate
the `MANIFEST.MF` to be included in the project. Additionally, annotations found in the [OpentracingZipkinTracerFactory.java](src/main/java/com/ibm/ws/opentracing/zipkin/OpentracingZipkinTracerFactory.java)
provide information for the manifest. If you decide to roll your own tracer, then you'll need to ensure your factory
implementation provides these annotations to correctly generate the manifest file.

## opentracingZipkin

To compile and package, run:

    mvn package

This will build `target/liberty-opentracing-zipkintracer-2.0-sample.zip`. Copy this file to `${wlp.user.dir}`
and `unzip` it to install the feature extension:

    $ cp target/liberty-opentracing-zipkintracer-2.0-sample.zip ${WLP}/usr/
    $ cd ${WLP}/usr/
    $ unzip liberty-opentracing-zipkintracer-2.0-sample.zip
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
