package ra.edu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entity class ánh xạ với bảng 'invoice_details' trong database.
 * Chứa thông tin chi tiết sản phẩm trong từng hóa đơn.
 */
@Entity
@Table(name = "invoice_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Khóa ngoại tham chiếu đến bảng invoice.
     * ManyToOne: Nhiều chi tiết thuộc về một hóa đơn.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    /**
     * Khóa ngoại tham chiếu đến bảng product.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * Số lượng sản phẩm mua.
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * Đơn giá tại thời điểm mua (lưu lại để không bị ảnh hưởng khi giá sản phẩm thay đổi).
     */
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;
}