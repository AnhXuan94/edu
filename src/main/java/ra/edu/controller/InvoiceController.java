package ra.edu.controller;

import ra.edu.entity.Invoice;
import ra.edu.entity.InvoiceDetail;
import ra.edu.service.InvoiceService;
import ra.edu.service.ProductService;
import ra.edu.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller xử lý các request HTTP liên quan đến Invoice (Hóa đơn).
 */
@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CustomerService customerService;

    /**
     * Hiển thị danh sách tất cả hóa đơn với phân trang.
     */
    @GetMapping
    public String listInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Page<Invoice> invoicePage = invoiceService.getAllInvoicesPaged(page, size);
        model.addAttribute("invoices", invoicePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", invoicePage.getTotalPages());
        model.addAttribute("totalItems", invoicePage.getTotalElements());

        return "invoices/list";
    }

    /**
     * Hiển thị form tạo hóa đơn mới.
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("products", productService.getAllProducts());
        return "invoices/form";
    }

    /**
     * Xử lý tạo hóa đơn mới.
     * Giảm tồn kho sản phẩm tương ứng.
     */
    @PostMapping("/create")
    public String createInvoice(
            @RequestParam Integer customerId,
            @RequestParam List<Integer> productIds,
            @RequestParam List<Integer> quantities,
            RedirectAttributes redirectAttributes) {

        try {
            Invoice invoice = invoiceService.createInvoice(customerId, productIds, quantities);
            if (invoice != null) {
                redirectAttributes.addFlashAttribute("successMessage", "Tạo hóa đơn thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi tạo hóa đơn: " + e.getMessage());
        }
        return "redirect:/invoices";
    }

    /**
     * Hiển thị chi tiết hóa đơn.
     */
    @GetMapping("/{id}")
    public String viewInvoice(@PathVariable Integer id, Model model) {
        Invoice invoice = invoiceService.getInvoiceById(id);
        if (invoice != null) {
            List<InvoiceDetail> details = invoiceService.getInvoiceDetails(id);
            model.addAttribute("invoice", invoice);
            model.addAttribute("details", details);
            return "invoices/detail";
        }
        return "redirect:/invoices";
    }

    /**
     * Cập nhật trạng thái hóa đơn.
     * Nếu hủy đơn, cộng lại tồn kho.
     */
    @PostMapping("/update-status/{id}")
    public String updateInvoiceStatus(
            @PathVariable Integer id,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {

        try {
            Invoice.InvoiceStatus newStatus = Invoice.InvoiceStatus.valueOf(status);
            boolean updated = invoiceService.updateInvoiceStatus(id, newStatus);
            if (updated) {
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy hóa đơn!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
        return "redirect:/invoices";
    }

    /**
     * Tìm kiếm hóa đơn theo tên khách hàng.
     */
    @GetMapping("/search")
    public String searchInvoices(@RequestParam String keyword, Model model) {
        List<Invoice> invoices = invoiceService.searchByCustomerName(keyword);
        model.addAttribute("invoices", invoices);
        model.addAttribute("keyword", keyword);
        return "invoices/list";
    }

    /**
     * Hiển thị thống kê doanh thu theo ngày.
     */
    @GetMapping("/revenue/daily")
    public String revenueByDay(Model model) {
        List<Object[]> revenueData = invoiceService.getRevenueByDay();
        model.addAttribute("revenueData", revenueData);
        model.addAttribute("type", "daily");
        return "invoices/revenue";
    }

    /**
     * Hiển thị thống kê doanh thu theo tháng.
     */
    @GetMapping("/revenue/monthly")
    public String revenueByMonth(Model model) {
        List<Object[]> revenueData = invoiceService.getRevenueByMonth();
        model.addAttribute("revenueData", revenueData);
        model.addAttribute("type", "monthly");
        return "invoices/revenue";
    }

    /**
     * Hiển thị thống kê doanh thu theo năm.
     */
    @GetMapping("/revenue/yearly")
    public String revenueByYear(Model model) {
        List<Object[]> revenueData = invoiceService.getRevenueByYear();
        model.addAttribute("revenueData", revenueData);
        model.addAttribute("type", "yearly");
        return "invoices/revenue";
    }
}