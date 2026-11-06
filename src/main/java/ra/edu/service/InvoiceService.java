package ra.edu.service;

import ra.edu.entity.Customer;
import ra.edu.entity.Invoice;
import ra.edu.entity.InvoiceDetail;
import ra.edu.entity.Product;
import ra.edu.repository.InvoiceRepository;
import ra.edu.repository.InvoiceDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service layer xử lý business logic cho Invoice (Hóa đơn).
 * @Service: Đánh dấu đây là Spring Bean, được quản lý bởi Spring Container.
 * @Transactional: Đảm bảo các thao tác trong phương thức hoặc cùng thành công hoặc cùng thất bại.
 *   Nếu có lỗi xảy ra, toàn bộ giao dịch sẽ bị rollback để dữ liệu không bị sai lệch.
 */
@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceDetailRepository invoiceDetailRepository;

    @Autowired
    private ProductService productService;

    /**
     * Lấy danh sách tất cả hóa đơn (không phân trang).
     * Sắp xếp theo ngày tạo mới nhất để hóa đơn mới luôn hiện lên đầu.
     * Sử dụng cho trang danh sách đơn giản.
     * @return Danh sách tất cả hóa đơn.
     */
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAllOrderByCreatedAtDesc();
    }

    /**
     * Lấy danh sách tất cả hóa đơn với phân trang.
     * Sắp xếp theo ngày tạo mới nhất.
     * @param page Số trang hiện tại (bắt đầu từ 0).
     * @param size Số lượng hóa đơn mỗi trang.
     * @return Page chứa danh sách hóa đơn và thông tin phân trang.
     */
    public Page<Invoice> getAllInvoicesPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return invoiceRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    /**
     * Lấy thông tin hóa đơn theo ID.
     * @param id ID của hóa đơn cần lấy.
     * @return Đối tượng Invoice nếu tìm thấy, ngược lại trả về null.
     */
    public Invoice getInvoiceById(Integer id) {
        return invoiceRepository.findById(id).orElse(null);
    }

    /**
     * Lấy danh sách chi tiết sản phẩm của một hóa đơn.
     * @param invoiceId ID của hóa đơn.
     * @return Danh sách InvoiceDetail tương ứng.
     */
    public List<InvoiceDetail> getInvoiceDetails(Integer invoiceId) {
        return invoiceDetailRepository.findByInvoiceId(invoiceId);
    }

    /**
     * Tạo hóa đơn mới với cơ chế giao dịch (Transaction).
     * Quy trình:
     * 1. Kiểm tra tồn kho của từng sản phẩm.
     * 2. Tính tổng tiền hóa đơn.
     * 3. Lưu hóa đơn chính vào database.
     * 4. Lưu chi tiết hóa đơn và giảm tồn kho tương ứng.
     * Nếu có bất kỳ lỗi nào xảy ra, toàn bộ giao dịch sẽ bị rollback.
     *
     * @param customerId ID khách hàng mua hàng.
     * @param productIds Danh sách ID sản phẩm được chọn.
     * @param quantities Danh sách số lượng tương ứng với từng sản phẩm.
     * @return Hóa đơn đã được tạo thành công.
     * @throws RuntimeException nếu sản phẩm không đủ tồn kho.
     */
    @Transactional
    public Invoice createInvoice(Integer customerId, List<Integer> productIds, List<Integer> quantities) {
        // Bước 1: Tính tổng tiền và kiểm tra tồn kho
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (int i = 0; i < productIds.size(); i++) {
            Integer productId = productIds.get(i);
            Integer quantity = quantities.get(i);

            Product product = productService.getProductById(productId).orElse(null);
            if (product == null) {
                throw new RuntimeException("Không tìm thấy sản phẩm có ID: " + productId);
            }
            if (product.getStock() < quantity) {
                throw new RuntimeException("Sản phẩm '" + product.getName() + "' không đủ tồn kho. Hiện có: " + product.getStock());
            }

            // Tính thành tiền = đơn giá * số lượng
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(subtotal);
        }

        // Bước 2: Tạo hóa đơn chính
        Invoice invoice = new Invoice();
        // Load customer từ DB để set vào hóa đơn (tránh lỗi LazyInitializationException)
        Customer customer = new Customer();
        customer.setId(customerId);
        invoice.setCustomer(customer);
        invoice.setTotalAmount(totalAmount);
        invoice.setStatus(Invoice.InvoiceStatus.PENDING);
        invoice = invoiceRepository.save(invoice);

        // Bước 3: Tạo chi tiết hóa đơn và giảm tồn kho
        for (int i = 0; i < productIds.size(); i++) {
            Integer productId = productIds.get(i);
            Integer quantity = quantities.get(i);
            Product product = productService.getProductById(productId).get();

            // Tạo chi tiết hóa đơn
            InvoiceDetail detail = new InvoiceDetail();
            detail.setInvoice(invoice);
            detail.setProduct(product);
            detail.setQuantity(quantity);
            detail.setUnitPrice(product.getPrice()); // Lưu giá tại thời điểm mua
            invoiceDetailRepository.save(detail);

            // Giảm tồn kho sản phẩm
            productService.decreaseStock(productId, quantity);
        }

        return invoice;
    }

    /**
     * Cập nhật trạng thái hóa đơn.
     * Logic đặc biệt khi chuyển sang trạng thái Hủy (CANCELLED):
     * - Nếu từ trạng thái khác chuyển sang CANCELLED: cộng lại tồn kho sản phẩm.
     * - Nếu từ CANCELLED chuyển sang trạng thái khác: trừ lại tồn kho sản phẩm.
     * Điều này đảm bảo số lượng tồn kho luôn chính xác.
     *
     * @param invoiceId ID hóa đơn cần cập nhật.
     * @param newStatus Trạng thái mới (PENDING, CONFIRMED, SHIPPED, CANCELLED).
     * @return true nếu cập nhật thành công, false nếu không tìm thấy hóa đơn.
     */
    @Transactional
    public boolean updateInvoiceStatus(Integer invoiceId, Invoice.InvoiceStatus newStatus) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElse(null);
        if (invoice == null) {
            return false;
        }

        Invoice.InvoiceStatus oldStatus = invoice.getStatus();

        // Không cho phép chuyển trạng thái nếu không có thay đổi
        if (oldStatus == newStatus) {
            return true;
        }

        // Cập nhật trạng thái mới
        invoice.setStatus(newStatus);
        invoiceRepository.save(invoice);

        // Xử lý tồn kho khi chuyển sang CANCELLED (hủy đơn hàng)
        if (newStatus == Invoice.InvoiceStatus.CANCELLED && oldStatus != Invoice.InvoiceStatus.CANCELLED) {
            List<InvoiceDetail> details = invoiceDetailRepository.findByInvoiceId(invoiceId);
            for (InvoiceDetail detail : details) {
                // Cộng lại số lượng vào tồn kho
                productService.increaseStock(detail.getProduct().getId(), detail.getQuantity());
            }
        }

        // Xử lý tồn kho khi từ CANCELLED chuyển sang trạng thái khác (khôi phục đơn)
        if (oldStatus == Invoice.InvoiceStatus.CANCELLED && newStatus != Invoice.InvoiceStatus.CANCELLED) {
            List<InvoiceDetail> details = invoiceDetailRepository.findByInvoiceId(invoiceId);
            for (InvoiceDetail detail : details) {
                // Trừ lại số lượng khỏi tồn kho
                productService.decreaseStock(detail.getProduct().getId(), detail.getQuantity());
            }
        }

        return true;
    }

    /**
     * Tìm kiếm hóa đơn theo tên khách hàng (tìm kiếm gần đúng).
     * Sử dụng JOIN với bảng customer trong repository.
     * @param keyword Từ khóa tìm kiếm (tên khách hàng).
     * @return Danh sách hóa đơn thỏa mãn điều kiện.
     */
    public List<Invoice> searchByCustomerName(String keyword) {
        return invoiceRepository.searchByCustomerName(keyword);
    }

    /**
     * Thống kê doanh thu theo ngày.
     * Chỉ tính các hóa đơn có trạng thái CONFIRMED hoặc SHIPPED.
     * @return Danh sách Object[] với [0] = ngày, [1] = tổng doanh thu.
     */
    public List<Object[]> getRevenueByDay() {
        return invoiceRepository.getRevenueByDay();
    }

    /**
     * Thống kê doanh thu theo tháng.
     * @return Danh sách Object[] với [0] = tháng (yyyy-MM), [1] = tổng doanh thu.
     */
    public List<Object[]> getRevenueByMonth() {
        return invoiceRepository.getRevenueByMonth();
    }

    /**
     * Thống kê doanh thu theo năm.
     * @return Danh sách Object[] với [0] = năm (yyyy), [1] = tổng doanh thu.
     */
    public List<Object[]> getRevenueByYear() {
        return invoiceRepository.getRevenueByYear();
    }
}