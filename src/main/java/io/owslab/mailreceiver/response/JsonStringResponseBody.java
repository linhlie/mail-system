package io.owslab.mailreceiver.response;

import org.json.JSONObject;

/**
 * Created by khanhlvb on 7/17/18.
 */
public class JsonStringResponseBody extends AjaxResponseBody {
    private String json;

    public JsonStringResponseBody(String msg) {
        super(msg, false);
    }

    public JsonStringResponseBody() {
        this("");
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
