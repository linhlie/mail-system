package io.owslab.mailreceiver.utils;

import io.owslab.mailreceiver.controller.SettingsController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;

public class FileAssert {
    private static final Logger logger = LoggerFactory.getLogger(FileAssert.class);

    public static FileAssertResult getDirectoryTree(File folder, boolean subFolders) {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("folder is not a Directory");
        }
        FileAssertResult result = new FileAssertResult(folder);
        if(subFolders){
            File[] files = folder.listFiles();
            Arrays.sort(files);
            for (File file : files) {
                if (file.isDirectory()) {
//                    if (file.getName().indexOf("-") != 0) {
//                        result.addNode(file);
//                    }
                    result.addNode(file);
                }
            }
        }
        return result;
    }
    /**
     * Pretty print the directory tree and its file names.
     *
     * @param folder
     *            must be a folder.
     * @return
     */
    public static String printDirectoryTree(File folder) {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("folder is not a Directory");
        }
        int indent = 0;
        StringBuilder sb = new StringBuilder();
        printDirectoryTree(folder, indent, sb);
        return sb.toString();
    }

    private static void printDirectoryTree(File folder, int indent,
                                           StringBuilder sb) {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("folder is not a Directory");
        }
        sb.append(getIndentString(indent));
        sb.append("+--");
        sb.append(folder.getName());
        sb.append("/");
        sb.append("\n");
        if(indent == 0){
            for (File file : folder.listFiles()) {
                if (file.isDirectory()) {
                    if(file.getName().indexOf("-") != 0){
                        printDirectoryTree(file, indent + 1, sb);
                    }
                }
//            else {
//                printFile(file, indent + 1, sb);
//            }
            }
        }
    }

    private static void printFile(File file, int indent, StringBuilder sb) {
        sb.append(getIndentString(indent));
        sb.append("+--");
        sb.append(file.getName());
        sb.append("\n");
    }

    private static String getIndentString(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("|  ");
        }
        return sb.toString();
    }

    public static String findFullPath (String folderPath) {
        String result = "";
        try {
            Runtime rt = Runtime.getRuntime();
            String commands[] = { "/bin/sh", "-c", "cd " + folderPath + "; pwd" };
            Process proc = rt.exec(commands);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

            String s = "";
            while ((s = stdInput.readLine()) != null) {
                result = s;
            }

            while ((s = stdError.readLine()) != null) {
                result = "/";
            }

        } catch (Exception e) {
            logger.error(e.toString());
        }

        return result;
    }

    public static FileAssertResult getRootPath(String fullPath){
        FileAssertResult result = null;
        try {
            String subFolder[] = fullPath.split("/");
            File file = new File("/");
            result = new FileAssertResult(file);
            if (subFolder.length > 1) {
                getFullNodeFile(result, file, subFolder, 1);
            }
        }catch (Exception e){
            logger.error(e.toString());
        }
        return result;
    }

    public static void getFullNodeFile(FileAssertResult result, File currentFile, String[] subFolder, int level){
        if(level >= subFolder.length){
            return;
        }
        File[] files = currentFile.listFiles();
        Arrays.sort(files);
        for (File file : files) {
            if (file.isDirectory()) {
                FileAssertResult childNode = new FileAssertResult(file);
                if(file.getName().equals(subFolder[level])){
                    getFullNodeFile(childNode, file, subFolder, level+1);
                }
                result.addNode(childNode);
            }
        }
    }
}
