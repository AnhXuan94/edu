package ra.edu.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Cấu hình Web MVC, đăng ký Interceptor để áp dụng cho các đường dẫn cụ thể.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    /**
     * Đăng ký Interceptor và chỉ định đường dẫn nào cần kiểm tra đăng nhập.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                // Các đường dẫn cần bảo mật (phải đăng nhập mới truy cập được)
                .addPathPatterns("/", "/products/**", "/customers/**", "/invoices/**")
                // Các đường dẫn công khai (không cần đăng nhập)
                .excludePathPatterns("/admin/login", "/admin/register", "/css/**", "/js/**", "/images/**");
    }
}