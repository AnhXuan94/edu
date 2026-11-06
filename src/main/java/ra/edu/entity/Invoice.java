package ra.edu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity class ánh xạ với bảng 'invoice' trong database.
 * EnumType.STRING: Lưu status dưới dạng chuỗi (PENDING, CONFIRMED...) thay vì số.
 */
@Entity
@Table(name = "invoice")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Khóa ngoại tham chiếu đến bảng customer.
     * FetchType.LAZY: Chỉ load customer khi cần, tối ưu hiệu suất.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    /**
     * Thời điểm tạo hóa đơn, tự động lấy thời gian hiện tại.
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Tổng tiền hóa đơn.
     */
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    /**
     * Trạng thái đơn hàng.
     * @Enumerated(EnumType.STRING): Lưu dưới dạng chuỗi trong database.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvoiceStatus status = InvoiceStatus.PENDING;

    /**
     * Enum định nghĩa các trạng thái hóa đơn.
     * Giá trị phải khớp với ENUM trong database.
     */
    public enum InvoiceStatus {
        PENDING,      // Chờ xác nhận
        CONFIRMED,    // Đã xác nhận
        SHIPPED,      // Đã giao hàng
        CANCELLED     // Đã hủy
    }

    /**
     * Callback tự động set createdAt khi entity được persist (insert).
     * @PrePersist: Chạy trước khi lưu vào database lần đầu.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}