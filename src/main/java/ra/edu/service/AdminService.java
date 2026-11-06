package ra.edu.service;

import ra.edu.entity.Admin;
import ra.edu.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service layer xử lý business logic cho Admin authentication.
 */
@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    /**
     * Đăng nhập admin.
     * @param username Tên đăng nhập.
     * @param password Mật khẩu.
     * @return Optional chứa Admin nếu đăng nhập thành công.
     */
    public Optional<Admin> login(String username, String password) {
        return adminRepository.findByUsername(username)
                .filter(admin -> admin.getPassword().equals(password));
    }

    /**
     * Đăng ký admin mới.
     * @param admin Thông tin admin cần đăng ký.
     * @return Admin đã đăng ký, hoặc null nếu username đã tồn tại.
     */
    public Admin register(Admin admin) {
        if (adminRepository.existsByUsername(admin.getUsername())) {
            return null;
        }
        return adminRepository.save(admin);
    }

    /**
     * Kiểm tra username đã tồn tại chưa.
     */
    public boolean isUsernameExists(String username) {
        return adminRepository.existsByUsername(username);
    }
}
