package io.owslab.mailreceiver.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by khanhlvb on 3/16/18.
 */
public class FileAssertResult {
    private String text;
    private String path;
    private List<FileAssertResult> nodes;

    public FileAssertResult() {
    }

    public FileAssertResult(File file) {
        this.path = file.getAbsolutePath();
        this.text = file.getName() != null && file.getName().length() > 0 ? file.getName() : this.path;
        this.nodes = new ArrayList<>();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<FileAssertResult> getNodes() {
        return nodes;
    }

    public void setNodes(List<FileAssertResult> nodes) {
        this.nodes = nodes;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean addNode(File file){
        return this.nodes.add(new FileAssertResult(file));
    }

    public void addNode(FileAssertResult fileAssertResult){
        this.nodes.add(fileAssertResult);
    }
}
