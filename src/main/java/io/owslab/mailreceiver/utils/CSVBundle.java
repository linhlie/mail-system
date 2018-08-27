package io.owslab.mailreceiver.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khanhlvb on 8/27/18.
 */
public class CSVBundle<T> {
    private String fileName;
    private String[] headers;
    private String[] keys;
    private List<T> data;

    public CSVBundle() {
        data = new ArrayList<T>();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public String[] getKeys() {
        return keys;
    }

    public void setKeys(String[] keys) {
        this.keys = keys;
    }
}
