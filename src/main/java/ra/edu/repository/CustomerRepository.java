package ra.edu.repository;

import ra.edu.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho bảng Customer.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    /**
     * Tìm khách hàng chưa bị xóa.
     */
    List<Customer> findByIsDeletedFalse();

    /**
     * Tìm khách hàng chưa bị xóa với phân trang.
     */
    Page<Customer> findByIsDeletedFalse(Pageable pageable);

    /**
     * Kiểm tra số điện thoại đã tồn tại chưa (chưa bị xóa).
     * Dùng để validate khi thêm/sửa khách hàng.
     */
    Optional<Customer> findByPhoneAndIsDeletedFalse(String phone);

    /**
     * Đếm số khách hàng chưa bị xóa.
     */
    long countByIsDeletedFalse();
}