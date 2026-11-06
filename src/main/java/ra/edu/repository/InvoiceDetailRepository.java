package ra.edu.repository;

import ra.edu.entity.InvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface cho bảng InvoiceDetail.
 */
@Repository
public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetail, Integer> {

    /**
     * Tìm tất cả chi tiết của một hóa đơn.
     */
    List<InvoiceDetail> findByInvoiceId(Integer invoiceId);
}