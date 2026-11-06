package ra.edu.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller cho trang chủ và điều hướng chung.
 * Kiểm tra đăng nhập ở mọi trang (trừ login/register).
 */
@Controller
public class HomeController {

    /**
     * Trang chủ (Dashboard).
     * Redirect về danh sách sản phẩm nếu đã đăng nhập.
     */
    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("pageTitle", "Dashboard");
        return "index";
    }

    /**
     * Interceptor kiểm tra đăng nhập sẽ được cấu hình riêng trong config.
     * Hoặc có thể dùng @ControllerAdvice để kiểm tra session ở mọi controller.
     */
}