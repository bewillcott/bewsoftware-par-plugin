/*
 *  File Name:    ParMojo.java
 *  Project Name: par-maven-plugin
 *
 *  Copyright (c) 2025 Bradley Willcott
 *
 *  par-maven-plugin is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  par-maven-plugin is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.bewsoftware.mojo.par;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.DefaultModelWriter;
import org.apache.maven.model.io.ModelWriter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import static java.lang.String.format;
import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_RESOURCES;

/**
 * This maven plug-in exports the current maven {@link Model}.
 * <p>
 * That is, the fully interpolated version of the "pom.xml".
 * <p>
 * Why would you want this? There are possibly as many reasons as there
 * are developers. However, I wrote this little cutie because I was sick
 * of having to use extra files to get information out of the pom. Why
 * do that when the pom is just there? Unfortunately, getting to the
 * pom.xml file inside the jar file was difficult. You had to have some
 * of the information inside the pom to get to the pom.xml file:
 * <p>
 * {@code META-INF/maven/${project.groupId}/${project.artifact}/pom.xml}
 * <p>
 * This made any method I could come up with, very non-generic. Which I
 * hate. Once this is run, you will find the very full pom.xml file in
 * the root directory of where resources are normally stored:
 * <p>
 * {@code }
 *
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
@Mojo(
        name = "process",
        defaultPhase = GENERATE_RESOURCES,
        requiresProject = true,
        threadSafe = false
)
public class ParMojo extends AbstractMojo
{
    private static final String DEFAULT_FILENAME = "pom.xml";

    /**
     * Set all directory paths to absolute, instead of relative.
     * <p>
     * This will mean, that references to your local directory structure
     * will be stored inside the <u>jar</u> file. This could become a breach of
     * security.
     */
    @Parameter(property = "par.absolutePaths", defaultValue = "false", required = true)
    private String absolutePaths;

    /**
     * The name of the file to write to.
     */
    @Parameter(property = "par.filename", defaultValue = "pom.xml", required = true)
    private String filename;

    /**
     * The maven project.
     */
    @Parameter(property = "project", readonly = true, required = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        final Model model = project.getModel();
        final Build build = model.getBuild();
        final String parent = build.getOutputDirectory();
        final File file = new File(parent, filename);
        final ModelWriter mw = new DefaultModelWriter();
        final String baseDir = project.getBasedir().toString() + "/";

        if ("false".equalsIgnoreCase(absolutePaths))
        {
            final StringWriter sw = new StringWriter();

            try (final FileWriter fw = new FileWriter(file))
            {
                mw.write(sw, null, model);
                final String xml = sw.toString().replaceAll(baseDir, "");
                fw.write(xml);
                fw.flush();
                getLog().info("File written: " + file.toString());
            } catch (IOException ex)
            {
                throw new MojoFailureException(ex);
            }
        } else if ("true".equalsIgnoreCase(absolutePaths))
        {
            getLog().info("WARNING: All directory paths are listed as absolute within the output file!\n"
                        + "         == Possible security/privacy breach ==\n");

            try
            {
                mw.write(file, null, model);
                getLog().info("File written: " + file.toString());
            } catch (IOException ex)
            {
                throw new MojoFailureException(ex);
            }
        } else
        {
            throw new MojoExecutionException(
                    format("%nInvalid setting for 'absolutePaths': \"%s\".%nValid options: \"true\", or \"false\".",
                            absolutePaths
                    ));
        }
    }
}
