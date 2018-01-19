package io.owslab.mailreceiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MailReceiverApplication {

	private static final Logger logger = LoggerFactory.getLogger(MailReceiverApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(MailReceiverApplication.class, args);
		logger.info("--Application Started--");
	}
}
