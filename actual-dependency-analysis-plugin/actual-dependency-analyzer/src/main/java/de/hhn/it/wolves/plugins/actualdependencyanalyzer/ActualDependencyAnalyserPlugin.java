package de.hhn.it.wolves.plugins.actualdependencyanalyzer;


import de.hhn.it.wolves.domain.*;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.dependency.analyze.*;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileReader;

public class ActualDependencyAnalyserPlugin extends AbstractAnalyzeMojo {


    public static void main(String[] args) {
        RepositoryInfo repositoryInfo = new RepositoryInfo();
        ActualDependencyAnalyserPlugin plugin = new ActualDependencyAnalyserPlugin();
        plugin.analyseRepository(repositoryInfo);
    }


    public AnalysisResult analyseRepository(RepositoryInfo info) {
        Model model = null;
        FileReader reader = null;
        MavenXpp3Reader mavenreader = new MavenXpp3Reader();
        try {
            File f = new File("C:/Users/Marvin/Documents/GitHub/example-java-maven/pom.xml");
            reader = new FileReader(f);
            model = mavenreader.read(reader);
            model.setPomFile(f);
        } catch (Exception ex) {
        }
        MavenProject project = new MavenProject(model);


        AnalyzeMojo mojo = new AnalyzeMojo();


        try {
            mojo.execute();
        } catch (MojoExecutionException e) {
            e.printStackTrace();
        } catch (MojoFailureException e) {
            e.printStackTrace();
        }
        return new ActualDependencyAnalysisResult(info);
    }
}
