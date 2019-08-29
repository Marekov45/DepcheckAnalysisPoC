package de.hhn.it.wolves.plugins.depcheckanalyzer;


import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marvin Rekovsky on 15.05.19.
 * <p>
 * This plugin was used as a testing ground for the analysis of declared dependencies that are unused in a javascript project.
 */
public class DepcheckAnalyserPlugin {

    private static final int DEP_INDEX = 1;

    public static void main(String[] args) throws IOException {
        DepcheckAnalyserPlugin plugin = new DepcheckAnalyserPlugin();
        plugin.analyseRepository();
    }


    public DepcheckAnalysisResult analyseRepository() {


        ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "npm install");
        pb.directory(new File("C:/Users/Marvin/IdeaProjects/passwords-webextension"));
        pb.redirectErrorStream(true);
        Process process = null;
        try {
            process = pb.start();
            //     if (!process.waitFor(2, TimeUnit.MINUTES)) {
            //       timeout - kill the process.
            //        process.destroy();
            //        System.out.println("Didnt work");

            //     }

        } catch (IOException e) {

            e.printStackTrace();
        }// catch (InterruptedException e) {
        //   e.printStackTrace();
        // }
        //   process = pb.start();
        BufferedReader installReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String installOutput;
        while (true) {
            try {
                if (!((installOutput = installReader.readLine()) != null)) break;

                System.out.println(installOutput);
                if (!installReader.ready()) break;

            } catch (IOException e) {
                System.out.println("WE COULDNT READ CURRENT PROCESS!");
            }
        }


        pb.command("cmd", "/c", "npm list -dev -prod -depth 0");
        System.out.println("Next Process");
        try {
            process = pb.start();
            //      if (!process.waitFor(3, TimeUnit.MINUTES)) {
            //timeout - kill the process.
            //          process.destroy(); // consider using destroyForcibly instead
            //         System.out.println("didnt work2");
            //   }
        } catch (IOException e) {

        } //catch (InterruptedException e) {
        //   e.printStackTrace();
        //  }

        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line = null;
        ArrayList<String> allDependencies = new ArrayList<>();
        try {
            input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                if (!((line = input.readLine()) != null)) break;
                if (line.contains("@") && !line.contains("UNMET")) {
                    allDependencies.add(line);
                }
                System.out.println(line);
                if (!input.ready()) break;
            } catch (IOException e) {
                System.out.println("COULDNT READ CURRENT PROCESS!");
            }
        }

        ArrayList<String> nodeDependencies = new ArrayList<>();
        for (int i = 0; i < allDependencies.size(); i++) {
            nodeDependencies.add(getNameOfDependency(allDependencies, i));
        }
        System.out.println("Next Process");
        pb.command("cmd", "/c", "depcheck --ignores=\"eslint-*,*-eslint,babel-*,*-babel,*-loader\" --skip-missing=true");
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
                if ((line2 = input2.readLine()) == null) break;
                System.out.println(line2);

                unusedNodeDependencies.add(line2);

                if (!input2.ready()) break;

            } catch (IOException e) {
                System.out.println("COULDNT READ CURRENT PROCESS");
            }
        }
      //  System.out.println(process.isAlive());
      //   System.out.println(p2.isAlive());
        System.out.println(unusedNodeDependencies);
        List<String> depcheckDependencies = new ArrayList<>();
        String depcheckText = "Missing dependencies";
        if (unusedNodeDependencies.contains(depcheckText)) {
            for (int i = 0; i < getUnusedDependencyIndex(unusedNodeDependencies, depcheckText); i++) {
                depcheckDependencies.add(unusedNodeDependencies.get(i));
            }
        } else {
            depcheckDependencies.addAll(unusedNodeDependencies);
        }

        depcheckDependencies.remove("Unused dependencies");
        depcheckDependencies.remove("Unused devDependencies");

        for (int i = nodeDependencies.size() - 1; i >= 0; i--) {
            String errorMessage = nodeDependencies.get(i);
            if (errorMessage.contains("ERR")) {
                nodeDependencies.remove(i);
            }
        }

        System.out.println(nodeDependencies);
        System.out.println(depcheckDependencies);

        String seperator = ";";
        List<String> lines = new ArrayList<>();
        lines.add("Dependency;Version;Module;Unused");

        for (String str : nodeDependencies) {
            int count = StringUtils.countMatches(str, "@");
            String[] allSplitValues = str.split("@");
            String dependency = "";
            String version;
            if (count == 2) {
                System.out.println(allSplitValues[1]);
                dependency = "@" + allSplitValues[1];
                version = allSplitValues[2];
                System.out.println(dependency);
                System.out.println(version);
            } else {
                dependency = allSplitValues[0];
                version = allSplitValues[1];
            }
            StringBuilder sb = new StringBuilder(dependency);
            sb.append(seperator).append(version);
            sb.append(seperator).append(" ");
            for (String str2 : depcheckDependencies) {
                if (!str2.contains("*")) {
                    if (str2.equals(dependency)) {
                        sb.append(seperator).append("X");
                    }
                } else {
                    String[] unusedSplitValues = str2.split("\\s");
                    //makes sure no OutOfBoundsException is thrown for some reason
                    if (unusedSplitValues[0].equals("*") && DEP_INDEX < unusedSplitValues.length) {
                        if (unusedSplitValues[1].equals(dependency)) {
                            sb.append(seperator).append("X");
                        }
                    }
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
            e.printStackTrace();
        } finally {
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }


        return new DepcheckAnalysisResult();
    }

    /**
     * Returns the dependency substring from the command line string.
     *
     * @param allDependencies the list that contains all the command lines including the dependency.
     *                        <b>Must not be <code>null</code></b>.
     * @param i               the index of the element in the list.
     * @return the dependency of the project for the current line. <b>Must not be <code>null</code></b>.
     */
    private String getNameOfDependency(ArrayList<String> allDependencies, int i) {
        String dependency;
        String line = allDependencies.get(i);
        String[] splitValues = line.split("\\s");
        if (line.contains("OPTIONAL") || line.contains("PEER")) {
            return dependency = splitValues[4];
        }
        if (!splitValues[1].equals("UNMET")) {
            dependency = splitValues[1];
            return dependency;
        }
        if (splitValues[3].equals("├──") || splitValues[3].equals("└──")) {
            dependency = splitValues[6];
        } else {
            dependency = splitValues[3];
        }
        return dependency;
    }

    /**
     * Returns the index of the starting point for extracting unused dependencies.
     *
     * @param output        the list that contains the unused dependencies along other possible stuff like missing dependencies.
     *                      <b>Must not be <code>null</code></b>.
     * @param startingPoint the string to be searched for in the list.<b>Must not be <code>null</code> or empty</b>.
     * @return the index of the starting point for extracting unused dependencies.
     */
    private int getUnusedDependencyIndex(ArrayList<String> output, String startingPoint) {
        int index = 0;
        int counter = 0;
        for (String start : output) {
            if (start.equals(startingPoint)) {
                index = counter;
            }
            counter++;
        }
        return index;
    }
}