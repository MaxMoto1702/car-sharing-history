package net.serebryansky.carsharinghistory.controller;

import net.serebryansky.carsharinghistory.domain.User;
import net.serebryansky.carsharinghistory.repository.UserRepository;
import net.serebryansky.carsharinghistory.service.UserService;
import net.serebryansky.carsharinghistory.validator.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

@Controller
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
//    private final SecurityService securityService;
    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final ApplicationContext applicationContext;

    public UserController(UserService userService, UserValidator userValidator, UserRepository userRepository, ApplicationContext applicationContext) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.userRepository = userRepository;
        this.applicationContext = applicationContext;
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            log.error("Registration error: {}", bindingResult);
            return "redirect:/registration.html";
        }

        userService.save(userForm);

//        securityService.autologin(userForm.getUsername(), userForm.getPasswordConfirm());

        return "redirect:/";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "login";
    }

//    @RequestMapping("/users")
//    public ResponseEntity<List<User>> list() {
//        return ok(userRepository.findAll());
//    }
}