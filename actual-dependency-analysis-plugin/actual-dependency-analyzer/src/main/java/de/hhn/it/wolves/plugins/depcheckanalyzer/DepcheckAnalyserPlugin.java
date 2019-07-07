package de.hhn.it.wolves.plugins.depcheckanalyzer;


import de.hhn.it.wolves.domain.*;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class DepcheckAnalyserPlugin {


    public static void main(String[] args) throws ScriptException {
        RepositoryInfo repositoryInfo = new RepositoryInfo();
        DepcheckAnalyserPlugin plugin = new DepcheckAnalyserPlugin();
        plugin.analyseRepository(repositoryInfo);
    }
// Test

    public AnalysisResult analyseRepository(RepositoryInfo info) throws ScriptException {


        ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "npm list --depth 0");
        pb.directory(new File("C:/Users/Marvin/IdeaProjects/pencilblue"));
        pb.redirectErrorStream(true);


        Process p = null;
        try {
            p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Alternative:
        /** Runtime rt = Runtime.getRuntime();
         try {
         Process pr = rt.exec("cmd /c depcheck C:/Users/Marvin/IdeaProjects/pencilblue/"); **/

        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

        String line = null;
        ArrayList<String> allDependencies = new ArrayList<>();


        while (true) {
            try {
                line = input.readLine();
                if (line == null) {
                    break;
                } else if (line.contains("--")) {
                    allDependencies.add(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        ArrayList<String> nodeDependencies = new ArrayList<>();
        for (int i = 0; i < allDependencies.size(); i++) {
            nodeDependencies.add(buildArtifactFromString(allDependencies, i));
        }
        pb.command("cmd", "/c", "depcheck");
        Process p2 = null;
        try {
            p2 = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader input2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));

        String line2 = null;
        ArrayList<String> unusedNodeDependencies = new ArrayList<>();
        while (true) {
            try {
                if (!((line2 = input2.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            unusedNodeDependencies.add(line2);
        }

        List<String> depcheckDependencies = new ArrayList<>();
        String depcheckText = "Missing dependencies";
        for (int i = 0; i < getUnusedDependencyIndex(unusedNodeDependencies, depcheckText); i++) {

            depcheckDependencies.add(unusedNodeDependencies.get(i));
        }

        depcheckDependencies.remove("Unused dependencies");
        depcheckDependencies.remove("Unused devDependencies");

        //allDependencies.remove("Unused dependencies");
        //  allDependencies.remove("Unused devDependencies");
        //  String depcheckText = "Missing dependencies";
        //  for (int i = 0; i < getUnusedDependencyIndex(allDependencies, depcheckText); i++) {
        //      unusedDependencies.add(allDependencies.get(i));
        //  }
        //  if (unusedDependencies.isEmpty()) {
        //     System.out.println("Keine unused dependencies");
        //     return null;
        //  }
        // unusedDependencies.remove("Unused dependencies");
        // unusedDependencies.remove("Unused devDependencies");
        System.out.println(nodeDependencies);
        System.out.println(depcheckDependencies);

        int exitVal = 0;
        try {
            exitVal = p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Exited with error code " + exitVal);

        String seperator = ";";
        List<String> lines = new ArrayList<>();
        lines.add("Dependency;Version;Unused");

        for (String str : nodeDependencies) {
            String allSplitValues[] = str.split("@");
            String dependency = allSplitValues[0];
            String version = allSplitValues[1];
            StringBuilder sb = new StringBuilder(dependency);
            sb.append(seperator).append(version);
            for (String str2 : depcheckDependencies) {
                String unusedSplitValues[] = str2.split("\\s");
                if (unusedSplitValues[1].equals(dependency)) {
                    sb.append(seperator).append("X");
                }
            }
            lines.add(sb.toString());
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("C://Users//Marvin//Documents//Studium//BACHELOR THESIS" + "/" + "nodereport.csv"));
            for (String string : lines) {
                writer.write(string);
                writer.newLine();
            }
        } catch (IOException e) {

        } finally {
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e) {

                }
        }


        return new DepcheckAnalysisResult(info);
    }

    private String buildArtifactFromString(ArrayList<String> allDependencies, int i) {
        String line = allDependencies.get(i);
        String splitValues[] = line.split("\\s");
        String dependency = splitValues[1];
        return dependency;
    }

    private int getUnusedDependencyIndex(ArrayList<String> output, String startingPoint) {
        String text = startingPoint;
        int index = 0;
        int counter = 0;
        for (String start : output) {
            if (start.equals(text)) {
                index = counter;
            }
            counter++;
        }
        return index;
    }
}
