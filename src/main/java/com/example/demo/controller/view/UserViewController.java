package com.example.demo.controller.view;

import com.example.demo.dto.user.RegisterRequest;
import com.example.demo.security.CustomOAuth2User;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserViewController {
    private final UserService userService;

    // 개발자용 회원가입 페이지
    @GetMapping("/dev-register")
    public String devRegisterPage() {
        return "user/dev-register";
    }

    // 개발자용 회원가입 처리
    @PostMapping("/dev-register")
    public String devRegister(@ModelAttribute RegisterRequest request,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        try {
            userService.register(request);
            redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
            return "redirect:/user/dev-login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "user/dev-register";
        }
    }

    // 로그인 페이지 (소셜 로그인만 표시)
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                           Model model) {
        if (error != null) {
            model.addAttribute("error", "로그인에 실패했습니다. 다시 시도해주세요.");
        }
        return "user/login";
    }

    // 개발자용 로그인 페이지 (이메일/패스워드)
    @GetMapping("/dev-login")
    public String devLoginPage(@RequestParam(required = false) String error,
                              Model model) {
        if (error != null) {
            model.addAttribute("error", "이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        return "user/dev-login";
    }

    // 마이페이지 (Spring Security가 인증 확인)
    @GetMapping("/mypage")
    public String mypage(@AuthenticationPrincipal Object principal, Model model) {
        if (principal == null) {
            return "redirect:/user/login";
        }

        // OAuth 로그인과 Form 로그인 모두 지원
        if (principal instanceof CustomOAuth2User oauth2User) {
            model.addAttribute("nickname", oauth2User.getNickname());
            model.addAttribute("email", oauth2User.getEmail());
            model.addAttribute("userId", oauth2User.getUserId());
        } else if (principal instanceof CustomUserDetails userDetails) {
            model.addAttribute("nickname", userDetails.getNickname());
            model.addAttribute("email", userDetails.getEmail());
            model.addAttribute("userId", userDetails.getUserId());
        } else {
            return "redirect:/user/login";
        }

        return "user/mypage";
    }

    // 관람기록 페이지 (Spring Security가 인증 확인)
    @GetMapping("/watch-history")
    public String watchHistory(@AuthenticationPrincipal Object principal, Model model) {
        if (principal == null) {
            return "redirect:/user/login";
        }

        // OAuth 로그인과 Form 로그인 모두 지원
        if (principal instanceof CustomOAuth2User oauth2User) {
            model.addAttribute("userId", oauth2User.getUserId());
        } else if (principal instanceof CustomUserDetails userDetails) {
            model.addAttribute("userId", userDetails.getUserId());
        } else {
            return "redirect:/user/login";
        }

        return "user/watch-history";
    }
}
