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

package org.jetbrains.plugins.spotbugs.common;

import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.roots.CompilerProjectExtension;
import com.intellij.openapi.roots.impl.CompilerProjectExtensionImpl;
import com.intellij.testFramework.fixtures.*;


public class CompileTest extends JavaCodeInsightFixtureTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    public void testCompile() {
        CompilerProjectExtension compilerProjectExtension = CompilerProjectExtensionImpl.getInstance(myFixture.getProject());

        compilerProjectExtension.setCompilerOutputUrl("/");
        myFixture.configureByFile("TestData.java");

        CompilerManager compilerManager = CompilerManager.getInstance(myFixture.getProject());
        compilerManager.compile(myFixture.getModule(), null);


//        final VirtualFile[] selectedFiles = new VirtualFile[]{myFixture.getFile().getVirtualFile()};
//        new FindBugsStarter(myFixture.getProject(), "taskTitle") {
//            @Override
//            protected void createCompileScope(@NotNull final CompilerManager compilerManager, @NotNull final Consumer<CompileScope> consumer) {
//                consumer.consume(createFilesCompileScope(compilerManager, selectedFiles));
//            }
//
//            @Override
//            protected boolean configure(@NotNull final ProgressIndicator indicator, @NotNull final FindBugsProjects projects, final boolean justCompiled) {
//                return projects.addFiles(selectedFiles, !justCompiled, hasTests(selectedFiles));
//            }
//        }.start();
    }


}
