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

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.oneki.plugin.utils.ZipUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class PluginDeployer {
	private static PluginAwareClassLoader classLoader;
	
//	public static List<PluginClassLoader> loadPlugins() {
//		String path = PluginDeployer.class.getProtectionDomain().getCodeSource().getLocation().getPath();
//		File currentDir = null;
//		try {
//			String decodedPath = URLDecoder.decode(path, "UTF-8");
//			currentDir = new File(decodedPath);
//		} catch (UnsupportedEncodingException e) {
//			throw new RuntimeException(e.getMessage(), e);
//		}
////		File currentDir = new File("").getAbsoluteFile();
//		System.out.println("currentDir = " + currentDir.getAbsolutePath());
//		File pluginDir = new File(currentDir.getParentFile(), "plugins");
//		return loadPlugins(pluginDir);
//	}
	
	public static List<PluginClassLoader> loadPlugins(File pluginBaseDir) {
		if(Thread.currentThread().getContextClassLoader() instanceof PluginAwareClassLoader) {
			return loadPlugins(pluginBaseDir, (PluginAwareClassLoader) Thread.currentThread().getContextClassLoader());
		} else {
			return loadPlugins(pluginBaseDir, new PluginAwareClassLoader());
		}
		
	}
	
	public static List<PluginClassLoader> loadPlugins(File pluginBaseDir, PluginAwareClassLoader pluginAwareClassLoader) {
		Thread.currentThread().setContextClassLoader(pluginAwareClassLoader);
		List<PluginClassLoader> pluginClassLoaders = new ArrayList<PluginClassLoader>();
		
		File[] pluginFiles = pluginBaseDir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				if(name.endsWith(".zip")) return true;
				return false;
			}
		});
		
		for(File pluginFile : pluginFiles) {
			//check if a dire with same name already exist
			File pluginDir = new File(pluginBaseDir,FilenameUtils.getBaseName(pluginFile.getName()));
			boolean doDeploy = false;
			if(pluginDir.exists()) {
				if(pluginDir.lastModified() < pluginFile.lastModified()) {
					//zip file was created/updated since last deploy
					doDeploy = true;
					//delete current dir
					try {
						FileUtils.deleteDirectory(pluginDir);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			} else {
				doDeploy = true;
			}
			
			if(doDeploy) {
				ZipUtils.unzipArchive(pluginFile, pluginDir);
			}
			
			//check if it's really a plugin
			File metadata = new File(pluginDir, Constants.classDir + "/META-INF/" + Constants.PROPERTY_FILE);
			if(metadata.exists()) {
				URL[] urls = listPluginURLs(pluginDir);
				Properties props = new Properties();
				try {
					InputStream in = new FileInputStream(metadata);
					props.load(in);
				} catch(IOException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
				PluginClassLoader pluginClassLoader = new PluginClassLoader(urls, pluginAwareClassLoader, props);
				pluginClassLoaders.add(pluginClassLoader);
			} else {
				try {
					FileUtils.deleteDirectory(pluginDir);
				} catch(Exception e) {}
				throw new RuntimeException("file " + pluginFile.getPath() + " is not a plugin");
			}
		}
		
		return pluginClassLoaders;
	}

	private static URL[] listPluginURLs(File pluginDir) {
		try {
			List<URL> urls = new ArrayList<URL>();
			File classesDir = new File(pluginDir, Constants.classDir);
			if(classesDir.exists()) {
				urls.add(classesDir.toURI().toURL());
			}
			
			File libDir = new File(pluginDir, Constants.libDir);
			if(libDir.exists()) {
				File[] libFiles = libDir.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						if(name.endsWith(".jar")) return true;
						return false;
					}
				});
				for(File libFile : libFiles) {
					urls.add(libFile.toURI().toURL());
				}
			}
	
			return urls.toArray(new URL[urls.size()]);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static PluginAwareClassLoader getClassLoader() {
		return classLoader;
	}
	
	
}
