package ra.edu.dto;

import lombok.Data;

/**
 * Data Transfer Object cho form đăng ký admin mới.
 * DTO này có thêm trường confirmPassword để xác nhận mật khẩu,
 * trong khi Entity Admin chỉ có password.
 * Điều này thể hiện sự tách biệt giữa View layer và Data layer.
 */
@Data
public class RegisterDTO {
    /**
     * Tên đăng nhập của admin mới.
     */
    private String username;

    /**
     * Mật khẩu của admin mới.
     */
    private String password;

    /**
     * Xác nhận mật khẩu (chỉ dùng trong form, không lưu vào DB).
     */
    private String confirmPassword;
}