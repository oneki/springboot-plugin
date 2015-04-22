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