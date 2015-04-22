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

import net.oneki.plugin.PluginAwareClassLoader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

public class PluginApplicationRunListener implements
		SpringApplicationRunListener {

	public PluginApplicationRunListener(SpringApplication application, String[] args) { }
	
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
