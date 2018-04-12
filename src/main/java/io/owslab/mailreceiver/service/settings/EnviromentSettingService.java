package io.owslab.mailreceiver.service.settings;

import io.owslab.mailreceiver.dao.EnviromentSettingDAO;
import io.owslab.mailreceiver.model.EnviromentSetting;
import io.owslab.mailreceiver.startup.ApplicationStartup;
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

    public static final String CHECK_MAIL_TIME_INTERVAL_KEY = "check_mail_time_interval";
    public static final String STORAGE_PATH_KEY = "file_storage_path";
    public static final String KEEP_MAIL_ON_MAIL_SERVER_KEY = "keep_mail_on_mail_server";
    public static final String DELETE_OLD_MAIL_KEY = "delete_old_mail";
    public static final String DELETE_AFTER_KEY = "delete_after";
    public static final String START_UP_WITH_PC_KEY = "start_up_with_pc";
    public static final String DEBUG_ON_KEY = "debug_on";
    public static final String DEBUG_RECEIVE_MAIL_ADDRESS_KEY = "debug_receive_mail_address";

    private static final String DEFAULT_STORAGE_PATH = ApplicationStartup.DEFAULT_STORAGE_PATH;
    private static final String DEFAULT_CHECK_MAIL_INTERVAL_IN_MINUTE = "10";
    private static final String DEFAULT_KEEP_MAIL_ON_SERVER = "1";
    private static final String DEFAULT_DELETE_OLD_MAIL = "0";
    private static final String DEFAULT_DELETE_AFTER = "30";
    private static final String DEFAULT_START_UP_WITH_PC = "0";
    private static final String DEFAULT_DEBUG_ON = "1";
    private static final String DEFAULT_DEBUG_RECEIVE_MAIL_ADDRESS = "ows-test@world-link-system.com";

    public static final HashMap<String, String> defaultKVStore = createMap();

    private static HashMap<String, String> createMap()
    {
        HashMap<String,String> map = new HashMap<>();
        map.put(CHECK_MAIL_TIME_INTERVAL_KEY, DEFAULT_CHECK_MAIL_INTERVAL_IN_MINUTE);
        map.put(STORAGE_PATH_KEY, DEFAULT_STORAGE_PATH);
        map.put(KEEP_MAIL_ON_MAIL_SERVER_KEY, DEFAULT_KEEP_MAIL_ON_SERVER);
        map.put(DELETE_OLD_MAIL_KEY, DEFAULT_DELETE_OLD_MAIL);
        map.put(DELETE_AFTER_KEY, DEFAULT_DELETE_AFTER);
        map.put(START_UP_WITH_PC_KEY, DEFAULT_START_UP_WITH_PC);
        map.put(DEBUG_ON_KEY, DEFAULT_DEBUG_ON);
        map.put(DEBUG_RECEIVE_MAIL_ADDRESS_KEY, DEFAULT_DEBUG_RECEIVE_MAIL_ADDRESS);
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

    private String getSetting(String key, String defaultValue){
        EnviromentSetting enviromentSetting = this.findByKey(key);
        return enviromentSetting == null ? defaultValue : enviromentSetting.getValue();
    }

    public String getStoragePath(){
        return this.getSetting(STORAGE_PATH_KEY, DEFAULT_STORAGE_PATH);
    }

    public int getCheckMailTimeInterval(){
        String timeIntervalStr = this.getSetting(CHECK_MAIL_TIME_INTERVAL_KEY, DEFAULT_CHECK_MAIL_INTERVAL_IN_MINUTE);
        return Integer.parseInt(timeIntervalStr);
    }

    public int getDeleteAfter(){;
        String deleteAfterStr = this.getSetting(DELETE_AFTER_KEY, DEFAULT_DELETE_AFTER);
        return Integer.parseInt(deleteAfterStr);
    }

    public boolean getKeepMailOnMailServer(){
        String value = this.getSetting(KEEP_MAIL_ON_MAIL_SERVER_KEY, DEFAULT_KEEP_MAIL_ON_SERVER);
        return value != null && value.equals("1");
    }

    public boolean getDeleteOldMail(){
        String value = this.getSetting(DELETE_OLD_MAIL_KEY, DEFAULT_DELETE_OLD_MAIL);
        return value != null && value.equals("1");
    }

    public boolean getStartupWithPC(){
        String value = this.getSetting(START_UP_WITH_PC_KEY, DEFAULT_START_UP_WITH_PC);
        return value != null && value.equals("1");
    }
}
