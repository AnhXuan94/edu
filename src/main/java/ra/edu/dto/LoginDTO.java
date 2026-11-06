package ra.edu.dto;

import lombok.Data;

/**
 * Data Transfer Object cho form đăng nhập.
 * DTO giúp tách biệt dữ liệu từ giao diện và entity trong database.
 * Trong trường hợp này, LoginDTO giống với Entity Admin,
 * nhưng việc sử dụng DTO thể hiện đúng kiến trúc phân tầng.
 */
@Data
public class LoginDTO {
    /**
     * Tên đăng nhập của admin.
     */
    private String username;

    /**
     * Mật khẩu của admin.
     */
    private String password;
}