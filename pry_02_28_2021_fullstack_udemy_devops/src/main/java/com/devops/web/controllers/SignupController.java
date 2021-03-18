package com.devops.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.devops.enums.PlansEnum;
import com.devops.web.domain.frontend.ProAccountPayload;

@Controller
public class SignupController {

	/** The application logger */
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SignupController.class);
	
	public static final String SIGNUP_URL_MAPPING = "/signup";
	
	// key que identifica el Basic o Pro Account POJO en el ModelMap
	public static final String PAYLOAD_MODEL_KEY_NAME = "payload";

	public static final String SUBSCRIPTION_VIEW_NAME = "registration/signup";
	
//	public static final String PAYLOAD_MODEL_KEY_NAME = "payload";

	@RequestMapping(value=SIGNUP_URL_MAPPING, method=RequestMethod.GET)
	public String signupGet(@RequestParam("planId") int planId, ModelMap model) {
		
		// Se verifica si el planId es válido
		// Por ahora, se arroja un IllegalArgumentException si es inválido
		if (planId != PlansEnum.BASIC.getId() && planId != PlansEnum.PRO.getId()) {
			throw new IllegalArgumentException("Plan id is not valid");
		}
		// Si sí es válido se crea una instancia de ProAccountPayload y se settea en el Model
		model.addAttribute(PAYLOAD_MODEL_KEY_NAME, new ProAccountPayload());
		return SUBSCRIPTION_VIEW_NAME;
	}

}
