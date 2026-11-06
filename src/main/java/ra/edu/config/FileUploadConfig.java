package ra.edu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Cấu hình ánh xạ đường dẫn URL đến thư mục lưu file upload.
 * Giúp truy cập ảnh đã upload qua URL: /uploads/products/ten-anh.jpg
 */
@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    /**
     * Ánh xạ URL /uploads/** đến thư mục vật lý uploads/products/
     * Khi truy cập http://localhost:8080/uploads/products/anh.jpg,
     * Spring sẽ trả về file từ thư mục uploads/products/anh.jpg
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/products/");
    }
}