# Spring boot plugin

**Spring boot plugin** allows any spring boot application to be extensible via plugins.

The structure of the application is the following
```
SpringBootApplication-1.0.jar
  - lib
  - META-INF
  - org
  - application.properties
  -  ...

plugins (directory configurable)
  - plugin1.zip
    - classes
    - lib
  - plugin2.zip
    - classes
    - lib
  -  ...
```

A plugin is a zip file containing classes and library.  
At runtime, it has access to all classes of spring boot application and all classes of other plugins.

## plugin
A plugin is a standard spring project. Spring annotation like @Component, @Configuration, ... are supported. @ComponentScan is not taken into account (replaced by property 'basePackages' in net.oneki.plugin.properties) 

A plugin must be packaged as a zip file and must contain the file META-INF/net.oneki.plugin.properties:
```properties
# Name of the plugin. By default the maven plugin will put groupId/artifactId/version
name=net.oneki.plugin/demo-simple-plugin/1.0.0-SNAPSHOT

# packages (and subpackages) to be scanned by Spring
basePackages=net.oneki.plugin.demo.simpleplugin

# List of plugin names the plugin depends on
dependOnPlugins=net.oneki.plugin/demo-plugin2/1.0.0-SNAPSHOT,net.oneki.plugin/demo-plugin3/1.0.0-SNAPSHOT
```
The creation of meta data file and zip file is done automatically by adding this maven plugin in pom.xml (compatible only with maven 3)
```xml
<plugin>
	<groupId>net.oneki.plugin</groupId>
	<artifactId>plugin-maven-plugin</artifactId>
	<version>1.0.0-SNAPSHOT</version>
	<executions>
		<execution>
			<goals>
				<goal>build</goal>
			</goals>
		</execution>
	</executions>
</plugin>
```

## main application
To become plugin friendly, the main application should add this dependency in pom.xml
```xml
<dependency>
	<groupId>net.oneki.plugin</groupId>
	<artifactId>plugin</artifactId>
	<version>1.0.0-SNAPSHOT</version>
</dependency>
```
and add in application.properties the path containing plugin zip files.
```properties
plugins.path=/path/to/plugins
```

[Check the demo application](demo-plugin) for more details.