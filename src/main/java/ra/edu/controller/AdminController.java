package ra.edu.controller;

import ra.edu.dto.RegisterDTO;
import ra.edu.entity.Admin;
import ra.edu.service.AdminService;
import ra.edu.util.StringUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller xử lý xác thực Admin (Đăng nhập, Đăng ký, Đăng xuất).
 * Sử dụng HttpSession để lưu trạng thái đăng nhập.
 * Sử dụng DTO để nhận dữ liệu từ form và Utility để validate.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * Hiển thị trang đăng nhập.
     * Nếu đã đăng nhập rồi thì redirect về trang chủ.
     */
    @GetMapping("/login")
    public String showLoginPage(HttpSession session) {
        if (session.getAttribute("loggedInAdmin") != null) {
            return "redirect:/";
        }
        return "admin/login";
    }

    /**
     * Xử lý đăng nhập.
     * Kiểm tra username và password, nếu đúng thì lưu vào session.
     */
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        return adminService.login(username, password)
                .map(admin -> {
                    session.setAttribute("loggedInAdmin", admin);
                    session.setAttribute("adminUsername", admin.getUsername());
                    return "redirect:/";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Sai tên đăng nhập hoặc mật khẩu!");
                    return "redirect:/admin/login";
                });
    }

    /**
     * Hiển thị trang đăng ký.
     * Truyền RegisterDTO vào model để form binding.
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "admin/register";
    }

    /**
     * Xử lý đăng ký admin mới.
     * Sử dụng RegisterDTO để nhận dữ liệu từ form.
     * Sử dụng StringUtil.isValidPassword() để validate password.
     */
    @PostMapping("/register")
    public String register(@RequestParam RegisterDTO registerDTO,
                           RedirectAttributes redirectAttributes) {

        // Validate password sử dụng utility function
        if (!StringUtil.isValidPassword(registerDTO.getPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Mật khẩu phải có ít nhất 6 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt!");
            return "redirect:/admin/register";
        }

        // Kiểm tra mật khẩu xác nhận có khớp không
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu xác nhận không khớp!");
            return "redirect:/admin/register";
        }

        // Chuyển từ DTO sang Entity để lưu vào database
        Admin admin = new Admin();
        admin.setUsername(registerDTO.getUsername());
        admin.setPassword(registerDTO.getPassword());

        Admin registeredAdmin = adminService.register(admin);
        if (registeredAdmin != null) {
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/admin/login";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Tên đăng nhập đã tồn tại!");
            return "redirect:/admin/register";
        }
    }

    /**
     * Xử lý đăng xuất.
     * Xóa session và redirect về trang login.
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Xóa toàn bộ session
        return "redirect:/admin/login";
    }
}