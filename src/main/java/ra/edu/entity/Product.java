package ra.edu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entity class ánh xạ với bảng 'product' trong database.
 * Sử dụng Lombok @Data để tự động sinh getter, setter, toString, equals, hashCode.
 * @Entity đánh dấu đây là JPA Entity, được quản lý bởi Hibernate.
 */
@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /**
     * Khóa chính, tự động tăng.
     * GenerationType.IDENTITY phù hợp với MySQL AUTO_INCREMENT.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Tên sản phẩm, không được để trống.
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * Nhãn hàng (brand) của sản phẩm.
     */
    @Column(name = "brand", nullable = false, length = 50)
    private String brand;

    /**
     * Giá bán sản phẩm.
     * Sử dụng BigDecimal thay vì double để tránh lỗi làm tròn số.
     */
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    /**
     * Số lượng tồn kho.
     */
    @Column(name = "stock", nullable = false)
    private Integer stock;

    /**
     * Đường dẫn ảnh sản phẩm (URL hoặc path local).
     */
    @Column(name = "image", nullable = false, length = 255)
    private String image;

    /**
     * Trạng thái xóa mềm (soft delete).
     * true = ẩn, false = hiện.
     * BIT(1) trong MySQL mapping sang Boolean trong Java.
     */
    @Column(name = "is_deleted", columnDefinition = "BIT(1) DEFAULT 0")
    private Boolean isDeleted = false;

    /**
     * Trạng thái hoạt động của sản phẩm.
     * true = Active (đang kinh doanh), false = Deactivate (tạm ngừng).
     * Dùng để admin thủ công khóa/mở khóa sản phẩm.
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}