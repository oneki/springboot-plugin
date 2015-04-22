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
package net.oneki.plugin.demo.application;

import java.util.List;

import net.oneki.plugin.demo.application.service.DemoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
