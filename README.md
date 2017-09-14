# opentracing-zipkin-tracer-impl
This sample project provides an [opentracing.io](http://opentracing.io/) tracer implementation wrapped as a [Liberty feature](https://www.ibm.com/support/knowledgecenter/en/SSEQTP_8.5.5/com.ibm.websphere.wlp.doc/ae/rwlp_feat.html).  The [Zipkin](http://http://zipkin.io/) tracer implementation is being used for this sample.

## Build
Use [Maven](https://maven.apache.org/) to build this sample by executing the following command: 
```
mvn clean package
```
## Deploy
The build process creates an extension directory under Maven's target directory.  The contents of the extension directory needs to be copied into the Liberty user directory (${wlp.user.dir}).  
```
cp -r target/extensions /opt/wlp/usr/
```
## Server Configuration
Any server where the [Zipkin](http://http://zipkin.io/) tracer is required should be configured to load this feature.  That can be accomplished by editing the server's server.xml file and adding the Zipkin open tracing feature into the server's featureManager list.
```
<featureManager>
   ...
  <feature>usr:opentracingZipkin-0.30</feature>
</featureManager>
```

By default, the feature is configured to connect to the zipkin server at http://zipkin:9411. To change where to connect to use 
```
  <opentracingZipkin host="hostName" port="portNumber"/>
```


