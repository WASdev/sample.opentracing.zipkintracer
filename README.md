# Tracer Implementation

A demo project that shows how to implement a tracer for the Liberty opentracing-1.0 feature.
Once implemented as an OSGI bundle a tracer can be included as a user feature that will automatically
pull the opentracing-1.0 feature. 

Building this project relies on the bnd-process plugin to process the bnd.bnd file which will generate
the MANIFEST.MF to be included in the project. Additionally, annotations found in the OpentracingZipkinFactory.java
file provide information for the manifest. If you decide to roll your own tracer, then you'll need to ensure your factory
implementation provides these annotations to correctly generate the manifest file.

A set of tests are provided with this project that should make it possible to verify that the MANIFEST.MF and the 
OSGI-INF directory have the correct information in them.

## opentracing-zipkin-tracer-impl
Run

mvn package

An extensions directory will be built in the target directory.

Copy the contents of the extensions directory to the ${wlp.user.dir} location.

For example:

cp -r target/extensions /opt/wlp/usr/

Then enable the feature with

&lt;feature&gt;usr:opentracingZipkin-0.30&lt;/feature&gt;

in server.xml

## Modifying the Zipkin host and port

You can observe in the metatype.xml that the pid provides opentracingZipkin as
for modifying the default host and port that Liberty will look for the zipkin
server on. This can be changed in the server.xml with xml.

`<opentracingZipkin host="myhost.com" port="4433" />`
