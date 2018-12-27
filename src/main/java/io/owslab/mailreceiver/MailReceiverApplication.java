package io.owslab.mailreceiver;

import io.owslab.mailreceiver.service.errror.ReportErrorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class MailReceiverApplication {

	private static final Logger logger = LoggerFactory.getLogger(MailReceiverApplication.class);
	public static boolean AUTO_FETCH_MAIL = false;

	@Bean(name="processExecutor")
	public TaskExecutor workExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setThreadNamePrefix("Async-");
		threadPoolTaskExecutor.setCorePoolSize(3);
		threadPoolTaskExecutor.setMaxPoolSize(3);
		threadPoolTaskExecutor.setQueueCapacity(600);
		threadPoolTaskExecutor.afterPropertiesSet();
		logger.info("ThreadPoolTaskExecutor set");
		return threadPoolTaskExecutor;
	}

	public static void main(String[] args) {
		for(int i=0;i<args.length;i++){
			int index = args[i].indexOf("auto-fetch-mail");
			if(index>-1){
				AUTO_FETCH_MAIL = Boolean.valueOf(args[i].substring(args[i].indexOf("=")+1).trim());
			}
		}
		System.setProperty("spring.devtools.restart.enabled", "false");
		System.setProperty("mail.mime.decodetext.strict", "false");
		SpringApplication.run(MailReceiverApplication.class, args);
		logger.info("--Application Started--");
	}
}
