package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.Greeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface GreetingDAO extends JpaRepository<Greeting, Long> {
    List<Greeting> findByAccountCreatedIdAndEmailAccountId(long accountId, long emailAccountId);
    List<Greeting> findByAccountCreatedIdAndEmailAccountIdAndGreetingTypeAndActive(long accountId, long emailAccountId, int greetingType, boolean active);
    List<Greeting> findByAccountCreatedIdAndEmailAccountIdAndTitle(long accountId, long emailAccountId, String title);

    @Modifying(clearAutomatically = true)
    @Query(
            value = "UPDATE `greeting` SET `active`='0' WHERE `account_created_id` = :accountId AND `email_account_id` = :emailAccountId AND `greeting_type` = :greetingType",
            nativeQuery = true
    )
    void removeActive(@Param("accountId") long accountId,@Param("emailAccountId")  long emailAccountId,@Param("greetingType")  int greetingType);
}
