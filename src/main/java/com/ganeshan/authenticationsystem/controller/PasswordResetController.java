package com.ganeshan.authenticationsystem.controller;

import com.ganeshan.authenticationsystem.exception.InvalidTokenException;
import com.ganeshan.authenticationsystem.exception.UnknownIdentifierException;
import com.ganeshan.authenticationsystem.model.ResetPasswordData;
import com.ganeshan.authenticationsystem.service.CustomerAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.StringUtils;

import static com.ganeshan.authenticationsystem.AppConstant.REDIRECT_LOGIN;

@Controller
@RequestMapping("/reset/password")
public class PasswordResetController {

    private static final String RESETMSG = "resetPasswordMsg";

    @Autowired
    CustomerAccountService customerAccountService;

    @Autowired
    MessageSource messageSource;

    @PostMapping("/request")
    public String resetPassword(final ResetPasswordData resetPasswordData, RedirectAttributes redirectAttributes) {
        try {
            customerAccountService.forgottenPassword(resetPasswordData.getEmail());
        } catch (UnknownIdentifierException e) {
            e.printStackTrace();
        }
        redirectAttributes.addFlashAttribute(RESETMSG, messageSource.getMessage("user.forgot.password.message", null, LocaleContextHolder.getLocale()));
        return REDIRECT_LOGIN;
    }

    @GetMapping("/change")
    public String changePassword(@RequestParam(required = false) String token, final RedirectAttributes redirectAttributes, final Model model) {
        if (StringUtils.isEmpty(token)) {
            redirectAttributes.addFlashAttribute(RESETMSG, messageSource.getMessage("user.registration.verification.missing.token", null, LocaleContextHolder.getLocale()));
            return REDIRECT_LOGIN;
        }
        ResetPasswordData passwordData = new ResetPasswordData();
        passwordData.setToken(token);
        setResetPasswordForm(model, passwordData);
        return "changepassword";
    }

    @PostMapping("/change")
    public String changePassword(final ResetPasswordData resetPasswordData, final Model model, RedirectAttributes redirectAttributes) {
        try {
            customerAccountService.updatePassword(resetPasswordData.getPassword(), resetPasswordData.getToken());
        } catch (InvalidTokenException | UnknownIdentifierException e) {
            e.printStackTrace();
            model.addAttribute("tokenError",
                    messageSource.getMessage("user.registration.verification.invalid.token", null, LocaleContextHolder.getLocale())
            );
            setResetPasswordForm(model, new ResetPasswordData());
            return "changepassword";
        }
        redirectAttributes.addFlashAttribute("passwordUpdateMsg",
                messageSource.getMessage("user.password.update.message", null, LocaleContextHolder.getLocale())
        );
        setResetPasswordForm(model, new ResetPasswordData());
        return REDIRECT_LOGIN;
    }

    @GetMapping("/forgot")
    public String forgotPasswordPage(Model model) {
        model.addAttribute("resetPasswordData", new ResetPasswordData());
        return "forgotpassword";
    }


    private void setResetPasswordForm(Model model, ResetPasswordData passwordData) {
        model.addAttribute("forgotPassword", passwordData);
    }


}
