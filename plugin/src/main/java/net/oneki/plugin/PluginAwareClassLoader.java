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
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import net.oneki.plugin.utils.EnumerationList;

public class PluginAwareClassLoader extends URLClassLoader {

	List<PluginClassLoader> childClassLoaders;

	public PluginAwareClassLoader(URL[] urls) {
		super(urls);
	}
	
	public PluginAwareClassLoader(URL[] urls, ClassLoader parent,
			URLStreamHandlerFactory factory) {
		super(urls, parent, factory);
	}

	public PluginAwareClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}
	
	public PluginAwareClassLoader() {
		super(new URL[0], Thread.currentThread().getContextClassLoader());
	}

	@Override
	public Class<?> loadClass(String name)  throws ClassNotFoundException {
		return loadClass(name, null);
	}

	public Class<?> loadClass(String className, PluginClassLoader caller) throws ClassNotFoundException {
		Class<?> clazz = null;
		try {
			clazz = super.loadClass(className);
			return clazz;
		} catch(ClassNotFoundException e) {
		}
		
		if(childClassLoaders != null) {
			for(PluginClassLoader childClassLoader : childClassLoaders) {
				if(caller == null || childClassLoader != caller) {
					try {
						clazz = childClassLoader.loadClassAskedByParent(className);
						return clazz;
					} catch(ClassNotFoundException e) {
					}
				}
			}
		}
		
		throw new ClassNotFoundException("class " + className + " not found on classpath");
	}
	
	public URL getResource(String name) {
		return getResource(name, null);
	}
	
    public URL getResource(String name, PluginClassLoader caller) {
        URL url = super.getResource(name);
    	
        if (url != null) {
            return url;
        }
        
    	url = findResource(name);
    	if(url != null) {
    		return url;
    	}
    	
		if(childClassLoaders != null) {
			for(PluginClassLoader childClassLoader : childClassLoaders) {
				if(caller == null || childClassLoader != caller) {
					url = childClassLoader.findResource(name);
					if(url != null) {
						return url;
					}
				}
			}
		}
    	

        return url;
    }
    public Enumeration<URL> getResources(String name) throws IOException {
    	return getResources(name, null);
    }
    
    public Enumeration<URL> getResources(String name, PluginClassLoader origin) throws IOException {
    	
    	EnumerationList<URL> result = new EnumerationList<URL>();

        if (getParent() != null) {
        	result.addElement(getParent().getResources(name));
        }
        result.addElement(findResources(name));
        if(childClassLoaders != null) {
        	for(PluginClassLoader childClassLoader : childClassLoaders) {
        		if(origin == null || childClassLoader != origin) {
        			result.addElement(childClassLoader.findResources(name));
        		}
        	}
        }
        
        return result;
    }
	
	public void addPlugin(PluginClassLoader classLoader) {
		if(childClassLoaders == null) childClassLoaders = new ArrayList<PluginClassLoader>();
		childClassLoaders.add(classLoader);
	}
	
	public String[] getBasePackages() {
		List<String> basePackages = new ArrayList<String>();
		if(childClassLoaders != null) {
			for(PluginClassLoader childClassLoader : childClassLoaders) {
				String[] pluginBasePackages = childClassLoader.getBasePackages();
				for(String pluginBasePackage : pluginBasePackages) {
					basePackages.add(pluginBasePackage);
				}
			}
		}
		return basePackages.toArray(new String[basePackages.size()]);
	}


}
