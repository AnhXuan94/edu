package ra.edu.repository;

import ra.edu.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface cho bảng Admin.
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {

    /**
     * Tìm admin theo username.
     * Optional: Có thể có hoặc không có kết quả.
     */
    Optional<Admin> findByUsername(String username);

    /**
     * Kiểm tra username đã tồn tại chưa.
     * Dùng để validate khi đăng ký.
     */
    boolean existsByUsername(String username);
}