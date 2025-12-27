package com.example.demo.controller.view;

import com.example.demo.dto.user.RegisterRequest;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserViewController {
    private final UserService userService;

    // 회원가입 페이지
    @GetMapping("/register")
    public String registerPage() {
        return "user/register";
    }

    // 회원가입 처리
    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequest request,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        try {
            userService.register(request);
            redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
            return "redirect:/user/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "user/register";
        }
    }

    // 로그인 페이지
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                           @RequestParam(required = false) String registered,
                           Model model) {
        if (error != null) {
            model.addAttribute("error", "이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        return "user/login";
    }

    // 마이페이지 (Spring Security가 인증 확인)
    @GetMapping("/mypage")
    public String mypage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/user/login";
        }

        model.addAttribute("nickname", userDetails.getNickname());
        model.addAttribute("email", userDetails.getEmail());
        model.addAttribute("userId", userDetails.getUserId());

        return "user/mypage";
    }

    // 관람기록 페이지 (Spring Security가 인증 확인)
    @GetMapping("/watch-history")
    public String watchHistory(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/user/login";
        }

        model.addAttribute("userId", userDetails.getUserId());
        return "user/watch-history";
    }
}
