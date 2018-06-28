package io.owslab.mailreceiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MailReceiverApplication {

	private static final Logger logger = LoggerFactory.getLogger(MailReceiverApplication.class);

	public static void main(String[] args) {
		System.setProperty("mail.mime.decodetext.strict", "false");
		SpringApplication.run(MailReceiverApplication.class, args);
		logger.info("--Application Started--");
	}
}
