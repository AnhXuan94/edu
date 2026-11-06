package ra.edu.repository;

import ra.edu.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface cho bảng Invoice.
 * Kế thừa JpaRepository để được cung cấp sẵn các phương thức CRUD cơ bản.
 * Spring Data JPA tự động implement interface này tại runtime.
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    /**
     * Lấy tất cả hóa đơn với phân trang, sắp xếp theo ngày tạo mới nhất.
     * Phương thức này sử dụng Spring Data JPA convention naming.
     * @param pageable Thông tin phân trang (số trang, kích thước trang).
     * @return Page chứa danh sách hóa đơn và thông tin phân trang.
     */
    Page<Invoice> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Lấy tất cả hóa đơn KHÔNG phân trang, sắp xếp theo ngày tạo mới nhất.
     * Sử dụng @Query JPQL tường minh để tránh xung đột tên với hàm có Pageable ở trên.
     * @return Danh sách tất cả hóa đơn.
     */
    @Query("SELECT i FROM Invoice i ORDER BY i.createdAt DESC")
    List<Invoice> findAllOrderByCreatedAtDesc();

    /**
     * Tìm kiếm hóa đơn theo tên khách hàng (tìm kiếm gần đúng).
     * JOIN với bảng customer để lấy tên khách hàng.
     * @param keyword Từ khóa tìm kiếm (tên khách hàng).
     * @return Danh sách hóa đơn có tên khách hàng chứa từ khóa.
     */
    @Query("SELECT i FROM Invoice i JOIN i.customer c WHERE c.name LIKE %:keyword% ORDER BY i.createdAt DESC")
    List<Invoice> searchByCustomerName(@Param("keyword") String keyword);

    /**
     * Thống kê doanh thu theo ngày (chỉ tính hóa đơn CONFIRMED hoặc SHIPPED).
     * FUNCTION('DATE', i.createdAt): Extract date part từ datetime.
     * SUM(i.totalAmount): Tổng doanh thu của mỗi ngày.
     * @return List<Object[]> với [0] = ngày, [1] = tổng doanh thu.
     */
    @Query("SELECT FUNCTION('DATE', i.createdAt) as date, SUM(i.totalAmount) as revenue " +
            "FROM Invoice i WHERE i.status IN ('CONFIRMED', 'SHIPPED') " +
            "GROUP BY FUNCTION('DATE', i.createdAt) ORDER BY date DESC")
    List<Object[]> getRevenueByDay();

    /**
     * Thống kê doanh thu theo tháng.
     * FUNCTION('DATE_FORMAT', i.createdAt, '%Y-%m'): Format datetime thành yyyy-MM.
     * @return List<Object[]> với [0] = tháng (yyyy-MM), [1] = tổng doanh thu.
     */
    @Query("SELECT FUNCTION('DATE_FORMAT', i.createdAt, '%Y-%m') as month, SUM(i.totalAmount) as revenue " +
            "FROM Invoice i WHERE i.status IN ('CONFIRMED', 'SHIPPED') " +
            "GROUP BY FUNCTION('DATE_FORMAT', i.createdAt, '%Y-%m') ORDER BY month DESC")
    List<Object[]> getRevenueByMonth();

    /**
     * Thống kê doanh thu theo năm.
     * FUNCTION('DATE_FORMAT', i.createdAt, '%Y'): Format datetime thành yyyy.
     * @return List<Object[]> với [0] = năm (yyyy), [1] = tổng doanh thu.
     */
    @Query("SELECT FUNCTION('DATE_FORMAT', i.createdAt, '%Y') as year, SUM(i.totalAmount) as revenue " +
            "FROM Invoice i WHERE i.status IN ('CONFIRMED', 'SHIPPED') " +
            "GROUP BY FUNCTION('DATE_FORMAT', i.createdAt, '%Y') ORDER BY year DESC")
    List<Object[]> getRevenueByYear();
}