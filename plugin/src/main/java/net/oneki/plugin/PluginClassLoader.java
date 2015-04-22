/**
 * Copyright (C) 2015 Oneki (http://www.oneki.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.oneki.plugin;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Properties;

import net.oneki.plugin.utils.EnumerationList;

public class PluginClassLoader extends URLClassLoader {

	private Properties metadata;
	
	public PluginClassLoader(URL[] urls, PluginAwareClassLoader parent, Properties metadata) {
		super(urls, parent);
		this.metadata = metadata;
		parent.addPlugin(this);
	}	

	@Override
	public Class<?> loadClass(String className) throws ClassNotFoundException {
		Class<?> clazz = findLoadedClass(className);
		if (clazz != null) {
			return clazz;
		}

		//find on our path
		try {
			clazz = findClass(className);
			return clazz;
		} catch (ClassNotFoundException e) {
		}
		
		//delegate to parent
		if(getParent() != null) {
			PluginAwareClassLoader parent = (PluginAwareClassLoader) getParent();
			return parent.loadClass(className, this);
		}
		else throw new ClassCastException("class " + className + " not found on classpath");
	}
	


	public Class<?> loadClassAskedByParent(String className) throws ClassNotFoundException {
		Class<?> clazz = findLoadedClass(className);
		if (clazz != null) {
			return clazz;
		}

		//find on our path
		clazz = findClass(className);
		return clazz;

	}
	
    public URL getResource(String name) {
      	System.out.println("entry in getResource " + name);
        URL url = findResource(name);
        if (url != null) {
            return url;
        }

        PluginAwareClassLoader parent = (PluginAwareClassLoader) getParent();
        
        return parent.getResource(name, this);
    } 
    
    public Enumeration<URL> getResources(String name) throws IOException {
    	
    	EnumerationList<URL> result = new EnumerationList<URL>();

        result.addElement(findResources(name));
        PluginAwareClassLoader parent = (PluginAwareClassLoader) getParent();
        
        result.addElement(parent.getResources(name, this));
        
        return result;
    }
    
    public String[] getBasePackages() {
    	return metadata.getProperty(Constants.PROPERTY_BASEPACKAGES).split(",");
    }
    
    public String getName() {
    	return metadata.getProperty(Constants.PROPERTY_NAME);
    }
    
    public String[] getPluginDependencies() {
    	return metadata.getProperty(Constants.PROPERTY_DEPENDENCIES).split(",");
    }

}
