package io.owslab.mailreceiver.service.greeting;

import io.owslab.mailreceiver.dao.GreetingDAO;
import io.owslab.mailreceiver.enums.ClickType;
import io.owslab.mailreceiver.enums.CompanyType;
import io.owslab.mailreceiver.model.Account;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.Greeting;
import io.owslab.mailreceiver.service.expansion.BusinessPartnerService;
import io.owslab.mailreceiver.service.security.AccountService;
import io.owslab.mailreceiver.utils.ConvertDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class GreetingService {

    public static final String COMPANY_FULL_NAME = "[%fcom]";
    public static final String COMPANY_NAME = "[%scom]";
    public static final String COMPANY_KANA_NAME = "[%kcom]";
    public static final String COMPANY_DOMAIN = "[%domain]";

    public static final String LOGIN_USER_FULL_NAME = "[%login]";
    public static final String LOGIN_USER_LAST_NAME = "[%logins]";
    public static final String LOGIN_USER_FIRST_NAME = "[%loginm]";

    @Autowired
    GreetingDAO greetingDAO;

    @Autowired
    AccountService accountService;

    @Autowired
    BusinessPartnerService partnerService;

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


    public Greeting getGreetingStructure(long accountId, long emailAccountId, int greetingType){
        List<Greeting> list = greetingDAO.findByAccountCreatedIdAndEmailAccountIdAndGreetingTypeAndActive(accountId, emailAccountId, greetingType, true);
        return list.size() > 0 ? list.get(0) : null;
    }

    public String getGreetings(long emailAccountId, int clickType, String emailAddress, long accountId, long busineesPartnerId) throws Exception {
        int greetingType = ClickType.getGreetingType(clickType);
        List<Greeting> list = greetingDAO.findByAccountCreatedIdAndEmailAccountIdAndGreetingTypeAndActive(accountId, emailAccountId, greetingType, true);
        if(list.size() <= 0) return  "";
        String greetingStructor = list.get(0).getGreeting();
        greetingStructor = greetingDecode(greetingStructor, emailAddress, accountId, busineesPartnerId);
        return greetingStructor;
    }

    public String greetingDecode(String greetingStructure, String emailAddress, long accountId, long businessPartnerId) throws Exception {
        if(isDetectComapany(greetingStructure) > -4){
            List<BusinessPartner> listPartner = new ArrayList<>();
            if(businessPartnerId <= 0){
                if(emailAddress != null && !emailAddress.equals("")){
                    String domain = ConvertDomain.convertEmailToDomain(emailAddress);
                    listPartner = partnerService.getPartnersByDomain(domain);
                }
            }else{
                BusinessPartner partner = partnerService.findOne(businessPartnerId);
                if(partner != null) listPartner.add(partner);
            }
            greetingStructure = detextCompany(greetingStructure, listPartner);
        }
        if(isDetectLoginUser(greetingStructure) > -3){
            greetingStructure = detextLoginUser(greetingStructure, accountId);
        }
        return greetingStructure;
    }

    private int isDetectComapany(String greetingStructure){
        int sum = greetingStructure.indexOf(COMPANY_FULL_NAME) + greetingStructure.indexOf(COMPANY_NAME) +greetingStructure.indexOf(COMPANY_KANA_NAME) +greetingStructure.indexOf(COMPANY_DOMAIN);
        return sum;
    }

    private String detextCompany(String greetingStructure, List<BusinessPartner> listPartner) {
       BusinessPartner partner = null;
       if(listPartner.size() <= 0 ){
           greetingStructure = greetingStructure.replaceAll(Pattern.quote(COMPANY_FULL_NAME), "お取引先");
           greetingStructure = greetingStructure.replaceAll(Pattern.quote(COMPANY_NAME), "お取引先");
           greetingStructure = greetingStructure.replaceAll(Pattern.quote(COMPANY_KANA_NAME), "");
           greetingStructure = greetingStructure.replaceAll(Pattern.quote(COMPANY_DOMAIN), "");
       }else{
           partner = listPartner.get(0);
           greetingStructure = greetingStructure.replaceAll(Pattern.quote(COMPANY_FULL_NAME), CompanyType.fromValue(partner.getCompanyType()).getText() + partner.getName());
           greetingStructure = greetingStructure.replaceAll(Pattern.quote(COMPANY_NAME), partner.getName());
           greetingStructure = greetingStructure.replaceAll(Pattern.quote(COMPANY_KANA_NAME), partner.getKanaName());
           greetingStructure = greetingStructure.replaceAll(Pattern.quote(COMPANY_DOMAIN), partner.getDomain());
       }
       return greetingStructure;
    }

    private int isDetectLoginUser(String greetingStructure){
        int sum = greetingStructure.indexOf(LOGIN_USER_FULL_NAME) + greetingStructure.indexOf(LOGIN_USER_LAST_NAME) +greetingStructure.indexOf(LOGIN_USER_FIRST_NAME);
        return sum;
    }

    private String detextLoginUser(String greetingStructure, long accountId) throws Exception {
        Account account = accountService.findById(accountId);
        if(account == null){
            throw new Exception("Account null");
        }else{
            greetingStructure = greetingStructure.replaceAll(Pattern.quote(LOGIN_USER_FULL_NAME), account.getAccountName());
            greetingStructure = greetingStructure.replaceAll(Pattern.quote(LOGIN_USER_LAST_NAME), account.getLastName());
            greetingStructure = greetingStructure.replaceAll(Pattern.quote(LOGIN_USER_FIRST_NAME), account.getFirstName());
        }
        return greetingStructure;
    }
}
