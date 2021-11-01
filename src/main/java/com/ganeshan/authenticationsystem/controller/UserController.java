package com.ganeshan.authenticationsystem.controller;

import com.ganeshan.authenticationsystem.exception.InvalidTokenException;
import com.ganeshan.authenticationsystem.exception.UserAlreadyExistException;
import com.ganeshan.authenticationsystem.model.UserData;
import com.ganeshan.authenticationsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.StringUtils;

import javax.validation.Valid;


@Controller
@RequestMapping("/authenticate")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    MessageSource messageSource;

    public static final String REDIRECT_LOGIN = "redirect:/authenticate/login";


    @PostMapping("/signup")
    public String userRegistration(@Valid @ModelAttribute("user") UserData userData, BindingResult bindingResult, final Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userData);
            return "signup";
        }
        try {
            userService.register(userData);
        } catch (UserAlreadyExistException e) {
            bindingResult.rejectValue("email", "error.user", "An account already exist for this email");
            model.addAttribute("user", userData);
            return "signup";
        }
        redirectAttributes.addFlashAttribute("registrationMsg",
                messageSource.getMessage("user.registration.verification.email.msg", null, LocaleContextHolder.getLocale()));
        return "redirect:/authenticate/signup";
    }


    @GetMapping("/register/verify")
    private String verifyUser(@RequestParam(required = false) String token, final Model model, RedirectAttributes redirectAttributes) {
        if (StringUtils.isEmpty(token)) {
            redirectAttributes.addFlashAttribute("tokenError",
                    messageSource.getMessage("user.registration.verification.missing.token", null, LocaleContextHolder.getLocale()));
            return REDIRECT_LOGIN;

        }
        try {
            userService.verifyUser(token);
        } catch (InvalidTokenException e) {
            redirectAttributes.addFlashAttribute("tokenError", messageSource.getMessage("user.registration.verification.invalid.token", null, LocaleContextHolder.getLocale()));
            return REDIRECT_LOGIN;
        }
        redirectAttributes.addFlashAttribute("verifiedAccountMsg", messageSource.getMessage("user.registration.verification.success", null, LocaleContextHolder.getLocale()));
        return REDIRECT_LOGIN;
    }


    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", defaultValue = "false") boolean loginError, @RequestParam(value = "invalid-session", defaultValue = "false") boolean invalidSession, RedirectAttributes redirectAttributes, final Model model) {
        if (loginError) {
            redirectAttributes.addFlashAttribute("loginerr", messageSource.getMessage("login.error", null, LocaleContextHolder.getLocale()));
            return REDIRECT_LOGIN;
        }
        if (invalidSession) {
            model.addAttribute("invalidSession", messageSource.getMessage("user.invalid.session", null, LocaleContextHolder.getLocale()));
        }
        return "login";
    }


    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("user", new UserData());
        return "signup";
    }
}
