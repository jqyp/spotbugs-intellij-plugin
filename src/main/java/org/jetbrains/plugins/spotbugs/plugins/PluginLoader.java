/*
 * Copyright 2020 SpotBugs plugin contributors
 *
 * This file is part of IntelliJ SpotBugs plugin.
 *
 * IntelliJ SpotBugs plugin is free software: you can redistribute it 
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of 
 * the License, or (at your option) any later version.
 *
 * IntelliJ SpotBugs plugin is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied 
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with IntelliJ SpotBugs plugin.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jetbrains.plugins.spotbugs.plugins;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.spotbugs.core.AbstractSettings;

import java.lang.ref.WeakReference;

public final class PluginLoader {
	private static WeakReference<Project> projectRef; // @GuardedBy PluginLoader.class
	private static WeakReference<Module> moduleRef; // @GuardedBy PluginLoader.class

	private PluginLoader() {
	}

	public synchronized static void invalidate() {
		projectRef = null;
		moduleRef = null;
	}

	public synchronized static boolean load(
			@NotNull final Project project,
			@Nullable final Module module,
			@NotNull final AbstractSettings settings,
			final boolean addEditSettingsLinkToErrorMessage
	) {

		Project latestProject = projectRef == null ? null : projectRef.get();
		Module latestModule = moduleRef == null ? null : moduleRef.get();
		if (latestProject != project || latestModule != module) {
			final PluginLoaderImpl pluginLoader = new PluginLoaderImpl(addEditSettingsLinkToErrorMessage);
			pluginLoader.load(settings.plugins);
      projectRef = new WeakReference<>(project);
			moduleRef = null;
			if (module != null) {
        moduleRef = new WeakReference<>(module);
			}
			return pluginLoader.showErrorNotificationIfNecessary(project);
		}
		return true;
	}

	public synchronized static boolean isLoaded(@NotNull final Project project) {
		return projectRef != null && projectRef.get() == project;
	}

	private static class PluginLoaderImpl extends AbstractPluginLoader {
		PluginLoaderImpl(final boolean addEditSettingsLinkToErrorMessage) {
			super(addEditSettingsLinkToErrorMessage);
		}
	}
}
