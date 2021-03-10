package com.devops.backend.service;

import org.springframework.mail.SimpleMailMessage;

import com.devops.web.domain.frontend.FeedbackPojo;

/**
 * Contract for email service.
 */
public interface EmailService {

	/**
	 * Sends an email with the content in the Feedback Pojo.
	 * @param feedbackPojo - The feedback Pojo
	 */
	public void sendFeedbackEmail(FeedbackPojo feedbackPojo);

	/** 
	 * Sends an email with the content of the SimpleMailMessage object.
	 * @param message - The object containing the email content
	 */
	public void sendGenericEmailMessage(SimpleMailMessage message);
}
