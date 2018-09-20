package io.owslab.mailreceiver.service.matching;

import io.owslab.mailreceiver.dto.EngineerMatchingDTO;
import io.owslab.mailreceiver.dto.PreviewMailDTO;
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
    	for(EngineerMatchingDTO engineerDTO : listEngineerMatchingDTO){
    		if(engineerDTO==null) continue;
    		
    		List<String> listMatchingWord = new ArrayList<String>();
    		List<String> listNotGoodWord = new ArrayList<String>();
    		
    		EmailMatchingEngineerResult result = new EmailMatchingEngineerResult();
    		result.setEngineerMatchingDTO(engineerDTO);
    		result.setListMatchingWord(listMatchingWord);
    		if(engineerDTO.getMatchingWord()!=null && !engineerDTO.getMatchingWord().trim().equals("")){
    			listMatchingWord = matchingConditionService.getWordList(engineerDTO.getMatchingWord().trim());
    		}
    		if(engineerDTO.getNotGoodWord()!=null && !engineerDTO.getNotGoodWord().trim().equals("")){
    			listNotGoodWord = matchingConditionService.getWordList(engineerDTO.getNotGoodWord().trim());
    		}
    		
    		for(Email email : listEmailMatching){
    			if(domainService.checkDomainCurrent(email.getFrom(), engineerDTO.getPartnerId())){
    				continue;
    			}
    			if(domainService.checkDomainPartnerNotGood(email.getFrom(), engineerDTO.getId())){
    				continue;
    			}
    			if(listNotGoodWord.size()>0 && emailWordJobService.matchingWordEngineer(email, listNotGoodWord, spaceEffective, distinguish)){
    				continue;
    			}
    			if(listMatchingWord.size()==0 || emailWordJobService.matchingWordEngineer(email, listMatchingWord, spaceEffective, distinguish)){
    				checkMatchingRange(null, email, distinguish);
    				result.addEmailDTO(email, null, null);// after check
    				previewMailDTOList.put(email.getMessageId(), new PreviewMailDTO(email));
    			}
    		}
    		listResult.add(result);
    	}
		FinalEmailMatchingEngineerResult result = new FinalEmailMatchingEngineerResult(listResult, previewMailDTOList);
    	return result;
    }
    
    public MatchingPartResult checkMatchingRange(List<FullNumberRange> rangeCondition , Email email, boolean distinguish){
    	FullNumberRange ranges =  new FullNumberRange(new SimpleNumberRange(700000L));
    	String optimizedSourcePart = email.getOptimizedText(false);
    	List<FullNumberRange> listFullRange = matchingConditionService.getMailRanges(email, email.getMessageId(), optimizedSourcePart);
    	for(FullNumberRange range : listFullRange){
    		if(range.match(ranges, 1)){
    			
    		}
    		System.out.println(range.getLeft()+"  "+range.getRight()+ "  "+email.getMessageId());
    	}
//    	MatchingPartResult result = matchingConditionService.hasMatchRange(rangeCondition, listFullRange, condition);
    	return null;
    }
    
    public List<FullNumberRange> getListFullRange(FilterRule rule, List<FullNumberRange> rangeCondition){
    	if(rule.getCondition() != null){
    		if(rule.getCondition().equals("AND")){
    			
    		}else{
    			
    		}
    	}else{
    		
    	}
    	return null;
    }
}
