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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyFilter;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactDescriptorException;
import org.sonatype.aether.resolution.ArtifactDescriptorRequest;
import org.sonatype.aether.resolution.ArtifactDescriptorResult;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.JavaScopes;
import org.sonatype.aether.util.filter.DependencyFilterUtils;

public class DependencyHelper {

	private MavenProject project;
	private RepositorySystem repoSystem;
	private RepositorySystemSession session;
	private List<RemoteRepository> remoteRepos;

	public DependencyHelper(MavenProject project, RepositorySystem repoSystem,
			RepositorySystemSession session, List<RemoteRepository> remoteRepos) {

		this.project = project;
		this.repoSystem = repoSystem;
		this.session = session;
		this.remoteRepos = remoteRepos;
	}

	public Artifact resolveArtifact(Artifact artifact)
			throws ArtifactResolutionException {

		ArtifactRequest artifactRequest = new ArtifactRequest();
		artifactRequest.setArtifact(artifact);
		artifactRequest.setRepositories(remoteRepos);

		ArtifactResult artifactResult = repoSystem.resolveArtifact(session,
				artifactRequest);

		return artifactResult.getArtifact();

	}

	public Map<Dependency, File> getDirectDependencies()
			throws MojoExecutionException {
		Map<Dependency, File> directDependencies = new HashMap<Dependency, File>();
		Artifact artifact = new DefaultArtifact(
				ArtifactHelper.getArtifactCoord(project));

		ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();
		descriptorRequest.setArtifact(artifact);
		descriptorRequest.setRepositories(remoteRepos);

		ArtifactDescriptorResult descriptorResult = null;
		try {
			descriptorResult = repoSystem.readArtifactDescriptor(session,
					descriptorRequest);
		} catch (ArtifactDescriptorException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}

		for (Dependency dependency : descriptorResult.getDependencies()) {
			Artifact resolvedArtifact;
			try {
				resolvedArtifact = resolveArtifact(dependency.getArtifact());
				directDependencies.put(dependency, resolvedArtifact.getFile());
			} catch (ArtifactResolutionException e) {
				throw new MojoExecutionException(e.getMessage(), e);
			}
		}

		return directDependencies;
	}

	public List<File> getAllDependencies() throws DependencyResolutionException {

		Artifact artifact = new DefaultArtifact(
				ArtifactHelper.getArtifactCoord(project));

		DependencyFilter classpathFlter = DependencyFilterUtils
				.classpathFilter(JavaScopes.RUNTIME);

		CollectRequest collectRequest = new CollectRequest();
		collectRequest.setRoot(new Dependency(artifact, JavaScopes.COMPILE));
		collectRequest.setRepositories(remoteRepos);

		DependencyRequest dependencyRequest = new DependencyRequest(
				collectRequest, classpathFlter);
		List<ArtifactResult> artifactResults = repoSystem.resolveDependencies(
				session, dependencyRequest).getArtifactResults();
		List<File> files = new ArrayList<File>();
		for (ArtifactResult artifactResult : artifactResults) {
			Artifact dep = artifactResult.getArtifact();
			if (!dep.getGroupId().equals(project.getGroupId())
					|| !dep.getArtifactId().equals(project.getArtifactId())
					|| !dep.getVersion().equals(project.getVersion())) {
				files.add(artifactResult.getArtifact().getFile());
			}
		}
		return files;
	}
}
