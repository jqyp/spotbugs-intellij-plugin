/*
 * Copyright 2016 Andre Pfeiler
 *
 * This file is part of FindBugs-IDEA.
 *
 * FindBugs-IDEA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FindBugs-IDEA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FindBugs-IDEA.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.twodividedbyzero.idea.findbugs.gui.settings;

import edu.umd.cs.findbugs.DetectorFactory;
import edu.umd.cs.findbugs.DetectorFactoryCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.twodividedbyzero.idea.findbugs.common.util.New;
import org.twodividedbyzero.idea.findbugs.core.DetectorSettings;
import org.twodividedbyzero.idea.findbugs.resources.ResourcesLoader;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

abstract class AbstractDetectorNode extends DefaultMutableTreeNode {
	AbstractDetectorNode(@NotNull final String text) {
		super(text);
	}

	abstract boolean isGroup();

	@Nullable
	abstract Boolean getEnabled();

	abstract void setEnabled(@Nullable Boolean enabled);

	@NotNull
	private static DetectorNode create(@NotNull final DetectorFactory detector, @Nullable final Boolean enabled) {
		return new DetectorNode(detector, enabled == null ? detector.isDefaultEnabled() : enabled);
	}

	@NotNull
	private static AbstractDetectorNode createGroup(@NotNull final String text) {
		return new DetectorGroupNode(text);
	}

	@NotNull
	static AbstractDetectorNode notLoaded() {
		return createGroup("not loaded");
	}

	@NotNull
	static AbstractDetectorNode buildRoot(
			final boolean addHidden,
			@NotNull final Set<DetectorSettings> detectorSettings
	) {

		final Map<String, List<DetectorNode>> byProvider = buildByProvider(addHidden, detectorSettings);

		final Comparator<DetectorNode> nodeComparator = new Comparator<DetectorNode>() {
			@Override
			public int compare(final DetectorNode o1, final DetectorNode o2) {
				return o1.toString().compareToIgnoreCase(o2.toString());
			}
		};

		final AbstractDetectorNode root = createGroup(ResourcesLoader.getString("settings.detector"));
		final ArrayList<String> providerSorted = new ArrayList<String>(byProvider.keySet());
		Collections.sort(providerSorted);
		for (final String provider : providerSorted) {
			final AbstractDetectorNode group = createGroup(provider);
			root.add(group);
			final List<DetectorNode> nodes = new ArrayList<DetectorNode>(byProvider.get(provider));
			Collections.sort(nodes, nodeComparator);
			for (final DetectorNode node : nodes) {
				group.add(node);
			}
		}
		return root;
	}

	@NotNull
	private static Map<String, List<DetectorNode>> buildByProvider(
			final boolean addHidden,
			@NotNull final Set<DetectorSettings> detectorSettings
	) {

		final Iterator<DetectorFactory> iterator = DetectorFactoryCollection.instance().factoryIterator();
		final Map<String, List<DetectorNode>> byProvider = New.map();
		while (iterator.hasNext()) {
			final DetectorFactory factory = iterator.next();
			if (!factory.isHidden() || addHidden) {
				String provider = factory.getPlugin().getProvider();
				if (provider == null) {
					provider = "Unknown";
				} else if (provider.endsWith(" project")) {
					provider = provider.substring(0, provider.length() - " project".length());
				}
				List<DetectorNode> detectors = byProvider.get(provider);
				if (detectors == null) {
					detectors = New.arrayList();
					byProvider.put(provider, detectors);
				}
				final Boolean enabled = isEnabled(detectorSettings, factory);
				detectors.add(create(factory, enabled));
			}
		}
		return byProvider;
	}

	static void fillEnabledSet(
			@NotNull final AbstractDetectorNode node,
			@NotNull final Set<DetectorSettings> detectorSettings
	) {
		for (int i = 0; i < node.getChildCount(); i++) {
			fillEnabledSet((AbstractDetectorNode) node.getChildAt(i), detectorSettings);
		}
		if (!node.isGroup()) {
			final DetectorNode detectorNode = (DetectorNode) node;
			remove(detectorSettings, detectorNode.getDetector());
			if (detectorNode.isEnabledDefaultDifferent()) {
				add(detectorSettings, detectorNode.getDetector(), detectorNode.getEnabled());
			}
		}
	}

	@Nullable
	private static Boolean isEnabled(
			@NotNull final Set<DetectorSettings> detectorSettings,
			@NotNull final DetectorFactory detector
	) {
		final String pluginId = detector.getPlugin().getPluginId();
		final String shortName = detector.getShortName();
		Boolean ret = null;
		for (final DetectorSettings settings : detectorSettings) {
			if (settings.pluginId.equals(pluginId)) {
				if (settings.shortName.equals(shortName)) {
					ret = settings.enabled;
					break;
				}
			}
		}
		return ret;
	}

	private static void remove(
			@NotNull final Set<DetectorSettings> detectorSettings,
			@NotNull final DetectorFactory detector
	) {
		final String pluginId = detector.getPlugin().getPluginId();
		final String shortName = detector.getShortName();
		final Set<DetectorSettings> toRemove = New.set();
		Boolean ret = null;
		for (final DetectorSettings settings : detectorSettings) {
			if (settings.pluginId.equals(pluginId)) {
				if (settings.shortName.equals(shortName)) {
					toRemove.add(settings);
				}
			}
		}
		detectorSettings.removeAll(toRemove);
	}

	private static void add(
			@NotNull final Set<DetectorSettings> detectorSettings,
			@NotNull final DetectorFactory detector,
			final boolean enabled
	) {
		final DetectorSettings settings = new DetectorSettings();
		settings.pluginId = detector.getPlugin().getPluginId();
		settings.shortName = detector.getShortName();
		settings.enabled = enabled;
		detectorSettings.add(settings);
	}
}
