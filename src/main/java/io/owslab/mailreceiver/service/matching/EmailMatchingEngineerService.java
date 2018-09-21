package io.owslab.mailreceiver.service.matching;

import io.owslab.mailreceiver.dto.EngineerMatchingDTO;
import io.owslab.mailreceiver.dto.PreviewMailDTO;
import io.owslab.mailreceiver.enums.ConditionOption;
import io.owslab.mailreceiver.enums.MailItemOption;
import io.owslab.mailreceiver.enums.NumberCompare;
import io.owslab.mailreceiver.form.EmailMatchingEngineerForm;
import io.owslab.mailreceiver.form.MatchingConditionForm;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.Engineer;
import io.owslab.mailreceiver.model.NumberTreatment;
import io.owslab.mailreceiver.model.RelationshipEngineerPartner;
import io.owslab.mailreceiver.service.expansion.DomainService;
import io.owslab.mailreceiver.service.expansion.EngineerService;
import io.owslab.mailreceiver.service.mail.MailBoxService;
import io.owslab.mailreceiver.service.replace.NumberTreatmentService;
import io.owslab.mailreceiver.service.word.EmailWordJobService;
import io.owslab.mailreceiver.utils.EmailMatchingEngineerResult;
import io.owslab.mailreceiver.utils.FilterRule;
import io.owslab.mailreceiver.utils.FinalEmailMatchingEngineerResult;
import io.owslab.mailreceiver.utils.FinalMatchingResult;
import io.owslab.mailreceiver.utils.FullNumberRange;
import io.owslab.mailreceiver.utils.MatchingPartResult;
import io.owslab.mailreceiver.utils.MatchingResult;
import io.owslab.mailreceiver.utils.MatchingWordResult;
import io.owslab.mailreceiver.utils.SimpleNumberRange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Service
@CacheConfig(cacheNames = "short_term_data")
public class EmailMatchingEngineerService {
	private static final Logger logger = LoggerFactory.getLogger(EmailMatchingEngineerService.class);
    private NumberTreatment numberTreatment;
    
    @Autowired
    private NumberTreatmentService numberTreatmentService;
    
    @Autowired
    private MailBoxService mailBoxService;
    
    @Autowired    
    private MatchingConditionService matchingConditionService;
    
    @Autowired
    private EmailWordJobService emailWordJobService;
    
    @Autowired
    private DomainService domainService;
    
    public List<Email> getEmailFromDestinationCondition(EmailMatchingEngineerForm form){
        numberTreatment = numberTreatmentService.getFirst();
        FilterRule destinationRule = form.getDestinationConditionData();
        
        boolean filterSender = form.isHandleDuplicateSender();
        boolean filterSubject = form.isHandleDuplicateSubject();
        boolean filterSameDomain = form.isHandleSameDomain();
        
        List<Email> emailList = mailBoxService.getAll();
        boolean distinguish = false;
        boolean spaceEffective = false;
        
        List<Email> matchDestinationList;
        if(destinationRule.getRules().size() > 0) {
        	matchingConditionService.findMailMatching(emailList, destinationRule, distinguish, spaceEffective);
            matchDestinationList = destinationRule.getMatchEmails();
        } else {
            matchDestinationList = emailList;
        }
        matchDestinationList = mailBoxService.filterDuplicate(matchDestinationList, filterSender, filterSubject);
        return matchDestinationList;
    }
    
    public FinalEmailMatchingEngineerResult matchingEmailsWithEngineerCondition(EmailMatchingEngineerForm form){
        logger.info("start matching");
    	List<EngineerMatchingDTO> listEngineerMatchingDTO = form.getListEngineerMatchingDTO();
    	if(listEngineerMatchingDTO==null || listEngineerMatchingDTO.size()==0)  return null;
    	
    	List<EmailMatchingEngineerResult> listResult = new  ArrayList<EmailMatchingEngineerResult>();
    	Map<String, PreviewMailDTO> previewMailDTOList = new HashMap<>();
    	List<Email> listEmailMatching = getEmailFromDestinationCondition(form);
    	boolean spaceEffective = form.isSpaceEffective();
    	boolean distinguish = form.isDistinguish();
    	boolean handleDomainPartnerCurrent = form.isHandleDomainPartnerCurrent();
    	boolean handleDomainPartnerGroup = form.isHandleDomainPartnerGroup();
    	for(EngineerMatchingDTO engineerDTO : listEngineerMatchingDTO){
    		if(engineerDTO==null) continue;
    		
    		List<String> listMatchingWord = new ArrayList<String>();
    		List<String> listNotGoodWord = new ArrayList<String>();
    		
    		EmailMatchingEngineerResult result = new EmailMatchingEngineerResult();
    		
    		if(engineerDTO.getMatchingWord()!=null && !engineerDTO.getMatchingWord().trim().equals("")){
    			listMatchingWord = matchingConditionService.getWordList(engineerDTO.getMatchingWord().trim());
    		}
    		if(engineerDTO.getNotGoodWord()!=null && !engineerDTO.getNotGoodWord().trim().equals("")){
    			listNotGoodWord = matchingConditionService.getWordList(engineerDTO.getNotGoodWord().trim());
    		}
 
    		FilterRule moneyCondition = engineerDTO.getMoneyCondition();
       		result.setEngineerMatchingDTO(engineerDTO);
    		result.setListMatchingWord(listMatchingWord);
    		result.getEngineerMatchingDTO().setMoneyCondition(null);
    		System.out.println("----------------------------------------------------------------------------------------");
    		for(Email email : listEmailMatching){
    			if(handleDomainPartnerCurrent && domainService.checkDomainPartnerCurrent(email.getFrom(), engineerDTO.getPartnerId())){
    				continue;
    			}
    			if(handleDomainPartnerGroup && domainService.checkDomainPartnerGroup(email.getFrom(), engineerDTO.getPartnerId())){
    				continue;
    			}
    			if(domainService.checkDomainPartnerNotGood(email.getFrom(), engineerDTO.getId())){
    				continue;
    			}
    			if(listNotGoodWord.size()>0 && emailWordJobService.matchingWordEngineer(email, listNotGoodWord, spaceEffective, distinguish)){
    				continue;
    			}
    			if(listMatchingWord.size()==0 || emailWordJobService.matchingWordEngineer(email, listMatchingWord, spaceEffective, distinguish)){
    				if(moneyCondition!=null && moneyCondition.getRules().size()>0){
        				MatchingPartResult matchingPartResult = matchingConditionService.isMailMatchingEngineer(email, moneyCondition, distinguish);
        				if(matchingPartResult.isMatch()){
        					 FullNumberRange matchRange = matchingPartResult.getMatchRange();
        	                 FullNumberRange range = matchingPartResult.getRange();
        	                 System.out.println(range+"    "+matchRange);
        	                 result.addEmailDTO(email, matchRange, range);
        	    			 previewMailDTOList.put(email.getMessageId(), new PreviewMailDTO(email));
        				 }
    				}
    				else{
    					result.addEmailDTO(email, null, null);
   	    			 	previewMailDTOList.put(email.getMessageId(), new PreviewMailDTO(email));
    				}
    			}
    		}
    		listResult.add(result);
    	}
		FinalEmailMatchingEngineerResult result = new FinalEmailMatchingEngineerResult(listResult, previewMailDTOList);
		
    	return result;
    }
    
}
