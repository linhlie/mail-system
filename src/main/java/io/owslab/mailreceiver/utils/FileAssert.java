package io.owslab.mailreceiver.utils;

import java.io.File;

public class FileAssert {

    public static FileAssertResult getDirectoryTree(File folder, boolean subFolders) {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("folder is not a Directory");
        }
        FileAssertResult result = new FileAssertResult(folder);
        if(subFolders){
            for (File file : folder.listFiles()) {
                if (file.isDirectory()) {
                    if (file.getName().indexOf("-") != 0) {
                        result.addNode(file);
                    }
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
}
