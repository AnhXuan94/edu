package ra.edu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class ánh xạ với bảng 'admin' trong database.
 * Lưu trữ thông tin đăng nhập của quản trị viên.
 */
@Entity
@Table(name = "admin")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /**
     * Mật khẩu đã mã hóa.
     * Trong thực tế nên dùng BCrypt, nhưng với bài tập này có thể lưu plain text.
     */
    @Column(name = "password", nullable = false, length = 255)
    private String password;
}