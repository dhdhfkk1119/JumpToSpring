package com.example.SUB.Controller;

import com.example.SUB.DTO.UserCreateForm;
import com.example.SUB.Entity.SiteUser;
import com.example.SUB.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.apache.catalina.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @GetMapping("/username")
    @ResponseBody
    public String currentUserName(Principal principal,Model model)
    {
        model.addAttribute("username",principal.getName());
        return principal.getName();
    }

    @GetMapping("/login")
    public String login(){
        return"login_form";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute("userCreateForm") @Valid UserCreateForm userCreateForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }
        try {
            userService.create(userCreateForm.getUsername(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1());
        }catch(DataIntegrityViolationException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("username")) {
                bindingResult.reject("signupFailed", "중복된 사용자 이름입니다.");
            } else if (errorMessage.contains("email")) {
                bindingResult.reject("signupFailed", "중복된 이메일입니다.");
            } else {
                bindingResult.reject("signupFailed", "이미 등록된 사용자입니다. 아이디 또는 이메일을 확인해 주십시요");
            }
            return "signup_form";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }

    @GetMapping("/page")
    public String Userpage(){

        return "/userpage";
    }

    // 로그인시에 username 정보를 가져온다.
    @GetMapping("/user")
    public String Username() {
        // SecurityContext에서 Authentication 객체를 가져옵니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String message;
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            // 로그인한 사용자의 사용자 이름을 가져옵니다.
            String currentUserName = authentication.getName();
            // 사용자 이름을 환영 메시지에 포함합니다.
            message = "안녕하세요, " + currentUserName + "님!";
        } else {
            message = "안녕하세요, 게스트님!";
        }

        return message;
    }

}
