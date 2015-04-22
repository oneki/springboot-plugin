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
package net.oneki.maven.plugins.mojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import net.oneki.maven.plugins.helper.ArtifactHelper;
import net.oneki.maven.plugins.helper.DependencyHelper;
import net.oneki.maven.plugins.helper.PackageHelper;
import net.oneki.maven.plugins.helper.ZipHelper;
import net.oneki.plugin.Constants;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.repository.RemoteRepository;

@Mojo(defaultPhase = LifecyclePhase.PACKAGE, name = "build", requiresDependencyResolution = ResolutionScope.COMPILE)
public class BuildMojo extends AbstractMojo {

	@Component
	private MavenProject project;

	@Parameter(defaultValue = "${project.build.outputDirectory}")
	private File outputDir;

	@Parameter(defaultValue = "${project.build.sourceDirectory}")
	private File sourceDir;

	@Component
	private RepositorySystem repoSystem;

	@Parameter(defaultValue = "${repositorySystemSession}")
	private RepositorySystemSession session;

	@Parameter(defaultValue = "${project.remoteProjectRepositories}")
	private List<RemoteRepository> remoteRepos;

	public void execute() throws MojoExecutionException, MojoFailureException {
		buildMetadataFile();
		buildZipFile();
	}
	
	private void buildMetadataFile() throws MojoExecutionException {
		getLog().info("Generate plugin metadata file");

		File metadataFile = createMetadataFile();

		Properties metadata = new Properties();
		try {
			metadata.load(new FileInputStream(metadataFile));
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
		
		metadata.setProperty(Constants.PROPERTY_NAME, getPluginName());
		metadata.setProperty(Constants.PROPERTY_BASEPACKAGES, PackageHelper.getBasePackages(sourceDir));
		metadata.setProperty(Constants.PROPERTY_DEPENDENCIES, buildDependsOn());
		
		saveMetadataFile(metadataFile, metadata);
	}
	
	private void buildZipFile() throws MojoExecutionException {
		getLog().info("Generate plugin zip file");
		DependencyHelper dependencyHelper = new DependencyHelper(project, repoSystem, session, remoteRepos);
		try {
			List<File> libFiles = dependencyHelper.getAllDependencies();
			File zipFile = new File(outputDir.getParentFile(), project.getArtifactId() + "-" + project.getVersion() + ".zip");
			ZipHelper.buildPluginPackage(outputDir, libFiles, zipFile);
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	private File createMetadataFile() throws MojoExecutionException{
		File metadataDir = new File(outputDir, "META-INF");
		File metadataFile = new File(metadataDir,Constants.PROPERTY_FILE);
		if (!metadataFile.exists()) {
			try {
				if (!metadataFile.getParentFile().exists()) {
					metadataFile.getParentFile().mkdirs();
				}
				metadataFile.createNewFile();
			} catch (IOException e) {
				throw new MojoExecutionException(
						"Couldn't create properties file: " + metadataFile, e);
			}
		}
		return metadataFile;
	}
	
	private void saveMetadataFile(File metadataFile, Properties metadata) throws MojoExecutionException {
	    OutputStream out = null;
		try {
			out = new FileOutputStream(metadataFile);
			metadata.store(out,"Plugin metadata");
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			try { out.close(); } catch(Exception e) {}
		}		
	}

	private String buildDependsOn() throws MojoExecutionException{
		DependencyHelper dependencyHelper = new DependencyHelper(project, repoSystem, session, remoteRepos);
		StringBuffer result = new StringBuffer();
		Map<Dependency, File> directDependencies = dependencyHelper.getDirectDependencies();
		URLClassLoader classLoader = null;
		for(Entry<Dependency, File> entry : directDependencies.entrySet()) {
			try {
				File jarDependency = entry.getValue();
				Dependency dependency = entry.getKey();
				URL[] urls = new URL[] { jarDependency.toURI().toURL() };
				classLoader = new URLClassLoader(urls);
				Properties metadata = new Properties();
				InputStream metadataStream = classLoader
						.getResourceAsStream("META-INF/" + Constants.PROPERTY_FILE);
				if (metadataStream != null) {
					// this is a plugin artifact
					if (!dependency.getScope().equalsIgnoreCase("provided")) {
						throw new MojoExecutionException(
								dependency
										+ " is a plugin, so scope must be set to provided");
					}
					metadata.load(metadataStream);
					if(result.length() > 0) result.append(",");
					result.append(ArtifactHelper.getArtifactCoord(dependency.getArtifact()));
				}
			} catch(Exception e) {
				throw new MojoExecutionException(e.getMessage(), e);
			} finally {
				try { classLoader.close(); } catch (Exception e) {}
			}
		}
		return result.toString();		
	}
	
	private String getPluginName() {
		return project.getGroupId() + "/" + project.getArtifactId() + "/" + project.getVersion();
	}
}