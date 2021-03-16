package com.devops.web.controllers;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.devops.backend.persistence.domain.backend.PasswordResetToken;
import com.devops.backend.persistence.domain.backend.User;
import com.devops.backend.service.EmailService;
import com.devops.backend.service.I18NService;
import com.devops.backend.service.PasswordResetTokenService;
import com.devops.backend.service.UserService;
import com.devops.utils.UserUtils;

@Controller
public class ForgotMyPasswordController {

	/** The application logger */
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ForgotMyPasswordController.class);

	public static final String EMAIL_ADDRESS_VIEW_NAME = "forgotmypassword/emailForm";

	public static final String FORGOT_PASSWORD_URL_MAPPING = "/forgotmypassword";
	
	public static final String MAIL_SENT_KEY = "mailSent";

	public static final String CHANGE_PASSWORD_PATH = "/changeuserpassword";

	public static final String EMAIL_MESSAGE_TEXT_PROPERTY_NAME = "forgotmypassword.email.text";

	public static final String CHANGE_PASSWORD_VIEW_NAME = "forgotmypassword/changePassword";

    private static final String PASSWORD_RESET_ATTRIBUTE_NAME = "passwordReset";

    private static final String MESSAGE_ATTRIBUTE_NAME = "message";
	
	@Autowired
	private I18NService i18NService;
	
    @Autowired
    private EmailService emailService;

	@Autowired
	private PasswordResetTokenService passwordResetTokenService;

	@Autowired
	private UserService userService;
	   
	@Value("${webmaster.email}")
	private String webMasterEmail;
	
	
	@RequestMapping(value=FORGOT_PASSWORD_URL_MAPPING, method = RequestMethod.GET)
	public String forgotPasswordGet() {
		return EMAIL_ADDRESS_VIEW_NAME;
	}
	
	@RequestMapping(value=FORGOT_PASSWORD_URL_MAPPING, method = RequestMethod.POST)
	public String forgotPasswordPost( HttpServletRequest request,
									  @RequestParam("email") String email,
									  ModelMap model) {
		
		PasswordResetToken passwordResetToken = passwordResetTokenService.createPasswordResetTokenForEmail(email);
		
		// Si no hay un usuario asociado a ese email, se muestra un LOG.warn en la consola y no se hace nada más
		// Esto se hace así porque no queremos informar por este medio a un posible hacker si el email existe o no
		if(passwordResetToken == null)  {
			LOG.warn("Couldn't find a password reset token for email {}", email);
		}
		else {
			User user = passwordResetToken.getUser();
			String token = passwordResetToken.getToken();
			
			String resetPasswordUrl = UserUtils.createPasswordResetUrl(request, user.getId(), token);
			LOG.debug("Reset Password URL {}", resetPasswordUrl);

			String emailText = i18NService.getMessage(EMAIL_MESSAGE_TEXT_PROPERTY_NAME, request.getLocale());

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("[Devopsbuddy]: How to Reset Your Password");
            mailMessage.setText(emailText + "\r\n" + resetPasswordUrl);
            mailMessage.setFrom(webMasterEmail);

            emailService.sendGenericEmailMessage(mailMessage);
			
			// Si la app encuentra un usuario asociado se emitirá un LOG.info con el objeto token solo para mostrar..
			// ..que todo funciona correctamente.
			// También se muestra el usuario asociado
//			LOG.info("Token value: {}", passwordResetToken.getToken());
//			LOG.debug("Username {}", passwordResetToken.getUser().getUsername());
		}
		
		model.addAttribute(MAIL_SENT_KEY, "true");
		
		return EMAIL_ADDRESS_VIEW_NAME;
	}
	
	// Los param id y token se obtienen del link que se envió al correo
	// Spring automáticamente completará los otros dos parámetros
    @RequestMapping(value = CHANGE_PASSWORD_PATH, method = RequestMethod.GET)
    public String changeUserPasswordGet(@RequestParam("id") long id,
                                        @RequestParam("token") String token,
                                        Locale locale,
                                        ModelMap model) {
    	// Primero se verifica que el token no sea null ni esté vacío, el id no debe ser 0
        // org.springframework.util.StringUtils
    	if (StringUtils.isEmpty(token) || id == 0) {
            LOG.error("Invalid user id {}  or token value {}", id, token);
            model.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "false");
            model.addAttribute(MESSAGE_ATTRIBUTE_NAME, "Invalid user id or token value");
            return CHANGE_PASSWORD_VIEW_NAME;
        }
	
    	// El token no está vacío ni es nulo ni el id es 0
        PasswordResetToken passwordResetToken = passwordResetTokenService.findByToken(token);

        // Si el token ingresado no es encontrado en la DB
        if (null == passwordResetToken) {
            LOG.warn("A token couldn't be found with value {}", token);
            model.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "false");
            model.addAttribute(MESSAGE_ATTRIBUTE_NAME, "Token not found");
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        // Si el token sí existe en la DB, se extrae el objeto-User
        User user = passwordResetToken.getUser();

        // Se evalúa si el userId del link coincide con el de la DB
        // Esto protege a la app de ser invocado por alguien que no haya recibido el email
        if (user.getId() != id) {
            LOG.error("The user id {} passed as parameter does not match the user id {} associated with the token {}",
                    id, user.getId(), token);
            model.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "false");
            model.addAttribute(MESSAGE_ATTRIBUTE_NAME, i18NService.getMessage("resetPassword.token.invalid", locale));
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        // Se compara la fecha de expiración con la fecha actual
        if (LocalDateTime.now(Clock.systemUTC()).isAfter(passwordResetToken.getExpiryDate())) {
            LOG.error("The token {} has expired", token);
            model.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "false");
            model.addAttribute(MESSAGE_ATTRIBUTE_NAME, i18NService.getMessage("resetPassword.token.expired", locale));
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        // Llegado a este punto quiere decir que todo está correcto.
        // Este valor será usado en el método POST posterior, cuando el ususario quiera resetear el password
        model.addAttribute("principalId", user.getId());

        // OK to proceed. We auto-authenticate the user so that in the POST request we can check if the user
        // is authenticated
        // import org.springframework.security.core.Authentication;
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        return CHANGE_PASSWORD_VIEW_NAME;
    }
	
    // arg-1 .. que viene del input.hidden del form
    // arg-2 .. el nuevo password
    @RequestMapping(value = CHANGE_PASSWORD_PATH, method = RequestMethod.POST)
    public String changeUserPasswordPost(@RequestParam("principal_id") long userId,
                                         @RequestParam("password") String password,
                                         ModelMap model) {

    	// Si el usuario no está autenticado, en este punto podría considerarse un security-breach
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null == authentication) {
            LOG.error("An unauthenticated user tried to invoke the reset password POST method");
            model.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "false");
            model.addAttribute(MESSAGE_ATTRIBUTE_NAME, "You are not authorized to perform this request.");
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        User user = (User) authentication.getPrincipal();
        if (user.getId() != userId) {
            LOG.error("Security breach! User {} is trying to make a password reset request on behalf of {}",
                    user.getId(), userId);
            model.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "false");
            model.addAttribute(MESSAGE_ATTRIBUTE_NAME, "You are not authorized to perform this request.");
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        userService.updateUserPassword(userId, password);
        LOG.info("Password successfully updated for user {}", user.getUsername());

        model.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "true");

        return CHANGE_PASSWORD_VIEW_NAME;

    }
	
}
