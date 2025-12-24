package com.example.demo.controller;

import com.example.demo.dto.user.LoginRequest;
import com.example.demo.dto.user.LoginResponse;
import com.example.demo.dto.user.RegisterRequest;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/register")
    public String registerPage() {
        return "user/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequest request, Model model) {
        try {
            userService.register(request);
            return "redirect:/user/login?registered";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "user/register";
        }
    }

    @GetMapping("/login")
    public String loginPage() {
        return "user/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest request, Model model, HttpSession session) {
        try {
            LoginResponse response = userService.login(request);

            // 세션에 사용자 정보 저장
            session.setAttribute("userId", response.getId());
            session.setAttribute("userEmail", response.getEmail());
            session.setAttribute("userNickname", response.getNickname());
            session.setAttribute("userRole", response.getRole());

            // 마이페이지로 이동
            return "redirect:/user/mypage";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "user/login";
        }
    }

    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/user/login";
        }

        model.addAttribute("nickname", session.getAttribute("userNickname"));
        model.addAttribute("email", session.getAttribute("userEmail"));

        return "user/mypage";
    }

    @GetMapping("/watch-history")
    public String watchHistory(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/user/login";
        }

        model.addAttribute("userId", userId);
        return "user/watch-history";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/user/login";
    }
}
