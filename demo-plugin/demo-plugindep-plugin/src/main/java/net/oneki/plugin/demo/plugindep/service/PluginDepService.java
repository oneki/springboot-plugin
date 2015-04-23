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
package net.oneki.plugin.demo.plugindep.service;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.oneki.plugin.demo.application.service.DemoService;
import net.oneki.plugin.demo.simpleplugin.service.ComplexService;
import net.oneki.plugin.demo.simpleplugin.service.SimpleService;

@Component
public class PluginDepService implements DemoService {

	private SimpleService simpleService;
	private ComplexService complexService;

	@Autowired
	public PluginDepService(SimpleService simpleService,
			ComplexService complexService) {
		this.simpleService = simpleService;
		this.complexService = complexService;
	}

	public String getName() {
		// We can use commons-codec lib, because demo-lib-plugin is declared
		// as dependency in pom.xml
		String base64Name = new String(Base64.encodeBase64("pluginDepService"
				.getBytes()));
		return "pluginDepService: (" + base64Name + "),  simpleService name="
				+ simpleService.getName() + ", complexService name="
				+ complexService.getName();
	}
}
