# BEWSoftware PAR (POM As Resource) Plugin

This plugin generates a fully built *pom.xml* file.

The source for this file is the live information available at <u>build</u>
time. The file is saved to the `${project.build.outputDirectory}` location.
This means that it will be available at runtime.

**Why would you want this?**

There are possibly as many reasons as there are developers, or projects.
However, I wrote this little cutie because I was sick of having to use
extra files to get information out of the <u>pom</u>. Why do that when
the <u>pom</u> is just there? Unfortunately, getting to the *pom.xml*, 
file inside the <u>jar</u> file, was difficult. You had to have some of 
the information from *inside* the <u>pom</u>, to get to the *pom.xml* file:

`META-INF/maven/${project.groupId}/${project.artifact}/pom.xml`

This made any method I could come up with, very non-generic. Something I
hate.

This plugin has just the one goal:

- **process**:  
This will do the work. There are two configuration options:

    - <u>absolutePaths</u> {false}:<br>
        Set all directory paths to absolute, *instead* of relative, which is the default.
        This will mean, that references to your local directory structure
        will be stored inside the <u>jar</u> file. This could become a breach of
        security, or at least privacy.
    - <u>filename</u> {pom.xml}:<br>
        The name of the file to write to.

## Status
This plugin is fully functional. You are welcome to clone and compile
it locally.  I plan on uploading it to the Maven Repository, once I
work out how!

## Dependencies
This plugin has no dependencies beyond what is required of all maven
plugins.
