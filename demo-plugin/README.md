# Spring boot plugin demo
This application demonstrates that Spring @Component defined in plugins can be discovered at runtime by the main spring boot application

3 services, implementing the interface [DemoService](demo-application/src/main/java/net/oneki/plugin/demo/application/service/DemoService.java), are defined:

One service is defined in main application
```java
@Component
public class DemoServiceImpl implements DemoService {

	@Override
	public String getName() {
		return "Demo Service from main application";
	}

}
```
Two services are defined in demo-simple-plugin
```java
@Configuration
public class PluginConfiguration {

	@Bean
	public ComplexService complexService() {
		return new ComplexService("Plugin 1 complex service");
	}
}

public class ComplexService implements DemoService {

	private String name;
	
	public ComplexService(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}

@Component
public class SimpleService implements DemoService{

	public String getName() {
		return "Plugin 1 simple service";
	}

}
```
Spring scans and injects all services implementing [DemoService](demo-application/src/main/java/net/oneki/plugin/demo/application/service/DemoService.java) in ServiceListing at startup.  
Once application is started, it displays the list of services.