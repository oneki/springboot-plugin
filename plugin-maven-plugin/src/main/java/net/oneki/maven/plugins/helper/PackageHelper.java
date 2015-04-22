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
package net.oneki.maven.plugins.helper;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class PackageHelper {
	
	public static String getBasePackages(File baseDir) {
		int baseDirLength = baseDir.getPath().length();
		List<File> basePackages = new ArrayList<File>();
		
		listBasePackages(baseDir, basePackages);
		
		StringBuffer sb = new StringBuffer();
		for(File file : basePackages) {
			String basePackage = file.getPath().substring(baseDirLength);
			basePackage = basePackage.replace("/", ".");
			basePackage = basePackage.replace("\\", ".");
			
			if(basePackage.startsWith(".")) {
				basePackage = basePackage.substring(1);
			}
			if(sb.length() > 0) {
				sb.append(",");
			}
			sb.append(basePackage);
		}
		
		return sb.toString();
	}

	private static void listBasePackages(File baseDir, List<File> basePackages) {
		File[] files = baseDir.listFiles(new FileFilter() {
			
			public boolean accept(File file) {
				if(file.isDirectory()) return false;
				if(file.getName().endsWith(".java")) return true;
				return false;
			}
		});
		if(files.length > 0) {
			basePackages.add(baseDir);
		} else {
			File[] directories = baseDir.listFiles(new FileFilter() {
				public boolean accept(File file) {
					if(file.isDirectory()) return true;
					return false;
				}
			});
			for(File dir : directories) {
				listBasePackages(dir, basePackages);
			}
		}
	}
}
