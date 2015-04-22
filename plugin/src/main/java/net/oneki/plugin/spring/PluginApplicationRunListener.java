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
package net.oneki.plugin.spring;

import java.io.File;

import net.oneki.plugin.Constants;
import net.oneki.plugin.PluginAwareClassLoader;
import net.oneki.plugin.PluginDeployer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

public class PluginApplicationRunListener implements
		SpringApplicationRunListener {

	public PluginApplicationRunListener(SpringApplication application, String[] args) { 
		if(!(Thread.currentThread().getContextClassLoader() instanceof PluginAwareClassLoader)) {
			Thread.currentThread().setContextClassLoader(new PluginAwareClassLoader());
		}
		
	}
	
	@Override
	public void started() {
		
	}

	@Override
	public void environmentPrepared(ConfigurableEnvironment environment) {
		
	}

	@Override
	public void contextPrepared(ConfigurableApplicationContext context) {
		
	}

	@Override
	public void contextLoaded(ConfigurableApplicationContext context) {
		
		if(context instanceof AnnotationConfigEmbeddedWebApplicationContext) {
			AnnotationConfigEmbeddedWebApplicationContext appContext = (AnnotationConfigEmbeddedWebApplicationContext) context;
			
			ConfigurableEnvironment env = appContext.getEnvironment();
			
			String pluginsPath = env.getProperty(Constants.PROPERTY_PLUGIN_PATH);
			if(pluginsPath != null) {
				File pluginBaseDir = new File(pluginsPath);
				if(!pluginBaseDir.exists()) {
					throw new RuntimeException("ERROR: plugins path " + pluginsPath + " doesn't exist");
				}
				PluginDeployer.loadPlugins(pluginBaseDir);
			}
			
			if(Thread.currentThread().getContextClassLoader() instanceof PluginAwareClassLoader) {
				PluginAwareClassLoader pluginAwareClassLoader = (PluginAwareClassLoader) Thread.currentThread().getContextClassLoader();
				appContext.scan(pluginAwareClassLoader.getBasePackages());
			}
			
		}
	}

	@Override
	public void finished(ConfigurableApplicationContext context,
			Throwable exception) {

	}

}
