package com.devops.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.devops.backend.service.EmailService;
import com.devops.web.domain.frontend.FeedbackPojo;

@Controller
public class ContactController {

	/** The application logger */
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ContactController.class);
	
	/** The key which identifies the feedback payload in the Model. */
	/** 'feedback' representará al objeto-FeedbackPojo. */
	public static final String FEEDBACK_MODEL_KEY = "feedback";

	/** The Contact Us view name. */
	/** carpeta: contact, archivo: contact.html */
	private static final String CONTACT_US_VIEW_NAME = "contact/contact";
	
	@Autowired
	private EmailService emailService;
	
	// Este método debe crear un objeto FeedbackPojo vacío
	// Settearlo en el 'model' como un par key-value
	@RequestMapping(value="/contact", method = RequestMethod.GET)
	public String contactGet(ModelMap model) {
		FeedbackPojo feedbackPojo = new FeedbackPojo();
		model.addAttribute(ContactController.FEEDBACK_MODEL_KEY, feedbackPojo);
		return ContactController.CONTACT_US_VIEW_NAME;
	}
	
	// @ModelAttribute le dice a Spring que tome el parámetro y lo use para llenar el 'feedback'
	@RequestMapping(value="/contact", method=RequestMethod.POST)
	public String contactPost(@ModelAttribute(FEEDBACK_MODEL_KEY) FeedbackPojo feedback) {
		LOG.debug("Feedback POJO content: {}", feedback);
		emailService.sendFeedbackEmail(feedback);
		return ContactController.CONTACT_US_VIEW_NAME;
	}
}
