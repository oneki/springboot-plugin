# Spring boot plugin demo
This application demonstrates that Spring @Component defined in plugins can be discovered at runtime by the main spring boot application

In this demo, Spring scans and injects all services implementing [DemoService](demo-application/src/main/java/net/oneki/plugin/demo/application/service/DemoService.java) in ServiceListing at startup.  
```java
@Component
public class ServiceListing {
	public static Logger logger = LoggerFactory.getLogger(ServiceListing.class);
	
	private List<DemoService> services;
	
	@Autowired
	public ServiceListing(List<DemoService> services) {
		this.services = services;
	}
	
	public void listDemoServices() {
		if(services != null) {
			for(DemoService service : services) {
				logger.info("Service: " + service.getName());
			}
		}
	}
}
```
Once application is started, it calls ServiceListing to display the list of services.

## Services
Service defined in [main application](demo-application)
```java
@Component
public class DemoServiceImpl implements DemoService {

	@Override
	public String getName() {
		return "Demo Service from main application";
	}

}
```

Services defined in [demo-simple-plugin](demo-simple-plugin)
```java
//Configuration in plugin are scanned
@Configuration
public class PluginConfiguration {

	@Bean
	public ComplexService complexService() {
		return new ComplexService("Plugin 1 complex service");
	}
}

//Service to demonstrate that Service instancied via @Bean works
public class ComplexService implements DemoService {

	private String name;
	
	public ComplexService(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}

//Simple service to demonstrate that Service inside plugin are scanned.
@Component
public class SimpleService implements DemoService{

	public String getName() {
		return "Plugin 1 simple service";
	}

}
```

Service defined in [demo-lib-plugin](demo-lib-plugin)
```java
//Service to demonstrate that plugin can contain specific lib (in this case commons-codec)
@Component
public class LibService implements DemoService{

	public String getName() {
		String name = "Lib service";
		
		//call lib specific to plugin
		String base64Name = new String(Base64.encodeBase64(name.getBytes()));
		
		return "Lib service ("+base64Name+")";
	}

}
```

# How to test
1. clone or download the repository.
2. Update the property
```properties
plugins.path=C\:/workspace/java-plugin/springboot-plugin/demo-plugin/plugins
```
in application.properties of demo-application.
In this demo, plugins are stored in "springboot-plugin/demo-plugin/plugins"
3. Build everything by executing
```
mvn install
```
at the root of the repository.
4. Execute the application build in target directory of demo-application via
```
java -jar demo-application-1.0.0-SNAPSHOT.jar
```