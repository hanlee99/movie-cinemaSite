package com.movierang.controller.view;

import com.movierang.dto.user.RegisterRequest;
import com.movierang.exception.BadRequestException;
import com.movierang.security.CustomOAuth2User;
import com.movierang.security.CustomUserDetails;
import com.movierang.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserViewController {
    private final UserService userService;

    // 개발자용 회원가입 페이지
    @GetMapping("/dev-register")
    public String devRegisterPage() {
        return "user/dev-register";
    }

    // 개발자용 회원가입 처리
    @PostMapping("/dev-register")
    public String devRegister(@Valid @ModelAttribute RegisterRequest request,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        // Validation 에러 처리
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getFieldError().getDefaultMessage());
            return "user/dev-register";
        }

        try {
            userService.register(request);
            redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
            return "redirect:/user/dev-login";
        } catch (BadRequestException e) {
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

    // 찜 목록 페이지 (Spring Security가 인증 확인)
    @GetMapping("/wishlist")
    public String wishlist(@AuthenticationPrincipal Object principal, Model model) {
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

        return "user/wishlist";
    }

    // 회원 탈퇴 확인 페이지
    @GetMapping("/withdrawal")
    public String withdrawalPage(@AuthenticationPrincipal Object principal, Model model) {
        if (principal == null) {
            return "redirect:/user/login";
        }

        boolean isOAuthUser = false;
        String email = null;
        String nickname = null;

        if (principal instanceof CustomOAuth2User oauth2User) {
            isOAuthUser = true;
            email = oauth2User.getEmail();
            nickname = oauth2User.getNickname();
        } else if (principal instanceof CustomUserDetails userDetails) {
            isOAuthUser = false;
            email = userDetails.getEmail();
            nickname = userDetails.getNickname();
        } else {
            return "redirect:/user/login";
        }

        model.addAttribute("isOAuthUser", isOAuthUser);
        model.addAttribute("email", email);
        model.addAttribute("nickname", nickname);

        return "user/withdrawal";
    }

    // 회원 탈퇴 처리
    @PostMapping("/withdrawal")
    public String processWithdrawal(
        @AuthenticationPrincipal Object principal,
        @RequestParam(required = false) String password,
        HttpServletRequest request,
        RedirectAttributes redirectAttributes
    ) {
        if (principal == null) {
            return "redirect:/user/login";
        }

        try {
            Long userId = null;

            if (principal instanceof CustomOAuth2User oauth2User) {
                userId = oauth2User.getUserId();
            } else if (principal instanceof CustomUserDetails userDetails) {
                userId = userDetails.getUserId();
            } else {
                redirectAttributes.addFlashAttribute("error", "인증 정보를 찾을 수 없습니다.");
                return "redirect:/user/withdrawal";
            }

            // 회원 탈퇴 처리
            userService.deleteUser(userId, password);

            // 세션 무효화 및 로그아웃
            request.getSession().invalidate();

            redirectAttributes.addFlashAttribute("message",
                "회원 탈퇴가 완료되었습니다. 그동안 이용해주셔서 감사합니다.");
            return "redirect:/";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/withdrawal";
        } catch (Exception e) {
            log.error("회원 탈퇴 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error",
                "회원 탈퇴 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            return "redirect:/user/withdrawal";
        }
    }
}
