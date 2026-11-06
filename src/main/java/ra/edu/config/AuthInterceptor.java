package ra.edu.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor kiểm tra trạng thái đăng nhập của Admin.
 * Chặn các request đến các trang quản lý nếu chưa đăng nhập.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    /**
     * Phương thức chạy trước khi request được xử lý bởi Controller.
     * @return true nếu cho phép đi tiếp, false nếu bị chặn.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();

        // Kiểm tra xem session có chứa thông tin admin đã đăng nhập hay không
        if (session.getAttribute("loggedInAdmin") == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang login
            response.sendRedirect("/admin/login");
            return false; // Chặn request
        }

        return true; // Cho phép đi tiếp nếu đã đăng nhập
    }
}