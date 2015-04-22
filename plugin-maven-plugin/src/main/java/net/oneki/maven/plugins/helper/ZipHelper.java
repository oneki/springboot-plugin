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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

public class ZipHelper {

	public static void buildPluginPackage(File classesDir, List<File> libFiles, File zipOutputFile) throws IOException {
		ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(zipOutputFile));
		compressDirectoryToZipfile(classesDir.getParentFile(), classesDir, zipFile);
		if(libFiles != null) {
			for(File libFile : libFiles) {
				addFileToZipfile("lib/" + libFile.getName(), libFile, zipFile);
			}
		}
		IOUtils.closeQuietly(zipFile);
	}
	
	public static void compressDirectoryToZipfile(File rootDir, File sourceDir, ZipOutputStream out) throws IOException, FileNotFoundException {
		for (File file : sourceDir.listFiles()) {
	        if (file.isDirectory()) {
	            compressDirectoryToZipfile(rootDir, file, out);
	        } else {
	            ZipEntry entry = new ZipEntry(file.getPath().replace(rootDir.getPath(), "").substring(1));
	            out.putNextEntry(entry);

	            FileInputStream in = new FileInputStream(file);
	            IOUtils.copy(in, out);
	            IOUtils.closeQuietly(in);
	        }
	    }
	}
	
	public static void addFileToZipfile(String pathInZip, File file, ZipOutputStream out) throws IOException {
		 ZipEntry entry = new ZipEntry(pathInZip);
		 out.putNextEntry(entry);
		 
		 FileInputStream in = new FileInputStream(file);
         IOUtils.copy(in, out);
         IOUtils.closeQuietly(in);
	}
}
