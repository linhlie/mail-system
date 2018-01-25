package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.EnviromentSetting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface EnviromentSettingDAO extends CrudRepository<EnviromentSetting, String> {

    List<EnviromentSetting> findByKey(String key);

}
