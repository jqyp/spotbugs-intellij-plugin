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

package org.jetbrains.plugins.spotbugs.gui.common;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.StripeTable;
import com.intellij.ui.table.JBTable;

import javax.swing.JTable;
import javax.swing.table.TableModel;


/**
 * $Date$
 *
 * @author Andre Pfeiler<andrep@dev.java.net>
 * @version $Revision$
 * @since 0.9.97
 */
@SuppressWarnings({"UnusedDeclaration"})
public final class TableFacade {

	private static final Logger LOGGER = Logger.getInstance(TableFacade.class.getName());


	private TableFacade() {
	}

	public static JTable createStripeTable(final TableModel dataModel) {
		return new StripeTable(dataModel);
	}


	public static JTable createTable(final TableModel dataModel) {
		return new JBTable(dataModel);
	}


	public static JTable createTable() {
		return new JBTable();
	}

}
