package io.owslab.mailreceiver.service.settings;

import io.owslab.mailreceiver.dao.EnviromentSettingDAO;
import io.owslab.mailreceiver.model.EnviromentSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by khanhlvb on 1/26/18.
 */
@Service
public class EnviromentSettingService {

    @Autowired
    private EnviromentSettingDAO enviromentSettingDAO;

    public static final HashMap<String, String> defaultKVStore = createMap();

    private static HashMap<String, String> createMap()
    {
        HashMap<String,String> map = new HashMap<>();
        map.put("check_mail_time_interval", "10");
        map.put("file_storage_path", null);
        map.put("keep_mail_on_mail_server", "1");
        map.put("delete_old_mail", "0");
        map.put("delete_after", "30");
        map.put("start_up_with_pc", "0");
        return map;
    }

    private static HashMap<String, String> createMap(List<EnviromentSetting> list)
    {
        HashMap<String,String> map = new HashMap<>();
        for (EnviromentSetting enviromentSetting : list) {
            map.put(enviromentSetting.getKey(), enviromentSetting.getValue());
        }
        return map;
    }


    public void init(){
        for (Map.Entry<String, String> entry : defaultKVStore.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            EnviromentSetting enviromentSetting = this.findByKey(key);
            if(enviromentSetting == null){
                this.set(key, value);
            }
        }
    }

    public EnviromentSetting findByKey(String key){
        List<EnviromentSetting> list = enviromentSettingDAO.findByKey(key);
        if(list.isEmpty())
            return null;
        return list.get(0);
    }

    public HashMap<String, String> getAll(){
        List<EnviromentSetting> list = (List<EnviromentSetting>)enviromentSettingDAO.findAll();
        HashMap<String, String > map = createMap(list);
        return map;
    }

    public EnviromentSetting set(String key, String value){
        EnviromentSetting enviromentSetting = new EnviromentSetting(key, value);
        return this.set(enviromentSetting);
    }

    public EnviromentSetting set(EnviromentSetting enviromentSetting){
        return enviromentSettingDAO.save(enviromentSetting);
    }
}
