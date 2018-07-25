package io.owslab.mailreceiver;

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
		System.setProperty("mail.mime.decodetext.strict", "false");
		SpringApplication.run(MailReceiverApplication.class, args);
		logger.info("--Application Started--");
	}
}
