package io.owslab.mailreceiver.service.greeting;

import io.owslab.mailreceiver.dao.GreetingDAO;
import io.owslab.mailreceiver.model.Greeting;
import io.owslab.mailreceiver.service.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GreetingService {

    @Autowired
    GreetingDAO greetingDAO;

    @Autowired
    AccountService accountService;

    public Greeting getById(long id){
        return greetingDAO.findOne(id);
    }

    public List<Greeting> getByEmailAccount(long emailAccountId){
        long accountId = accountService.getLoggedInAccountId();
        return greetingDAO.findByAccountCreatedIdAndEmailAccountId(accountId, emailAccountId);
    }

    public void addGreeting(Greeting form) throws Exception {
        if(form==null || form.getTitle()==null){
            throw new Exception("[Add Greeting] Form must not null");
        }
        long accountId = accountService.getLoggedInAccountId();
        List<Greeting> list = greetingDAO.findByAccountCreatedIdAndEmailAccountIdAndTitle(accountId, form.getEmailAccountId(), form.getTitle());
        if(list.size() > 0){
            throw new Exception("[Add Greeting] Title existed");
        }
        if(form.isActive()){
            greetingDAO.removeActive(accountId, form.getEmailAccountId(), form.getGreetingType());
        }
        form.setAccountCreatedId(accountId);
        System.out.println(form.getGreetingType());
        greetingDAO.save(form);
    }

    public void updateGreeting(Greeting form) throws Exception {
        if(form==null || form.getTitle()==null){
            throw new Exception("[Add Greeting] Form must not null");
        }
        long accountId = accountService.getLoggedInAccountId();
        List<Greeting> list = greetingDAO.findByAccountCreatedIdAndEmailAccountIdAndTitle(accountId, form.getEmailAccountId(), form.getTitle());
        for(Greeting greeting : list){
            if (greeting.getId() != form.getId()){
                throw new Exception("[Add Greeting] Title existed");
            }
        }
        if(form.isActive()){
            greetingDAO.removeActive(accountId, form.getEmailAccountId(), form.getGreetingType());
        }
        form.setAccountCreatedId(accountId);
        greetingDAO.save(form);
    }

    public void delete(long id) {
        greetingDAO.delete(id);
    }
}
