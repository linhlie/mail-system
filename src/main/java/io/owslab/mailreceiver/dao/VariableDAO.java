package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.Account;
import io.owslab.mailreceiver.model.FuzzyWord;
import io.owslab.mailreceiver.model.Variable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by khanhlvb on 3/15/18.
 */
public interface VariableDAO extends CrudRepository<Variable, String> {
    @Query(
            value = "show VARIABLES LIKE :variableName",
            nativeQuery = true
    )
    List<Variable> findByVariableName(@Param("variableName") String variableName);
}
