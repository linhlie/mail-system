package io.owslab.mailreceiver.form;

import io.owslab.mailreceiver.model.ReceiveEmailAccountSetting;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by khanhlvb on 1/24/18.
 */
public class EnviromentSettingForm {
    private Map<String, String> map = new HashMap<String, String>();

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

}
