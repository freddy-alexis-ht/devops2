package com.devops.backend.service;

import org.springframework.mail.SimpleMailMessage;

public class MockEmailService extends AbstractEmailService {

	/** The application logger */
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MockEmailService.class);

	@Override
	public void sendGenericEmailMessage(SimpleMailMessage message) {
        LOG.debug("Simulating an email service...");
        LOG.info(message.toString());
        LOG.debug("Email sent.");
    }

}
