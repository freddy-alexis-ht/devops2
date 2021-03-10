package com.devops.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;

import com.devops.web.domain.frontend.FeedbackPojo;

public abstract class AbstractEmailService implements EmailService {

	@Value("${default.to.address}")
	private String defaultToAddress;
	
	/**
	 * Creates a SimpleMailMessage from a FeedbackPojo.
	 * @param feedback - The Feedback pojo
	 * @return
	 */
	protected SimpleMailMessage prepareSimpleMailMessageFromFeedbackPojo (FeedbackPojo feedback) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(defaultToAddress); 		// a donde se enviará
        message.setFrom(feedback.getEmail());	// quién envía
        message.setSubject("[DevOps Buddy]: Feedback received from " + feedback.getFirstName() + " " + feedback
                .getLastName() + "!");
        message.setText(feedback.getFeedback());
        return message;
    }
	
	// sendGenericEmailMessage() está declarado en la interface
	// ..cada clase que la implemente deberá definiral, es lo que hara la diferencia
	// ..dependiendo del runtime-email-service que se use: MockEmailService o SMTPEmailService
	@Override
    public void sendFeedbackEmail(FeedbackPojo feedbackPojo) {
        sendGenericEmailMessage(prepareSimpleMailMessageFromFeedbackPojo(feedbackPojo));
    }
}
