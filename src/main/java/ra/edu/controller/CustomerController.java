package ra.edu.controller;

import org.springframework.data.domain.Page;
import ra.edu.entity.Customer;
import ra.edu.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller xử lý các request HTTP liên quan đến Customer (Khách hàng).
 * @RequestMapping("/customers"): Tất cả request có path /customers sẽ được xử lý ở đây.
 */
@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * Hiển thị danh sách tất cả khách hàng chưa bị xóa.
     * GET /customers
     *
     * @param model Model để truyền danh sách khách hàng sang view.
     * @return Tên template "customers/list".
     */
    @GetMapping
    public String listCustomers(Model model) {
        List<Customer> customers = customerService.getAllCustomers();
        model.addAttribute("customers", customers);
        return "customers/list";
    }

    /**
     * Hiển thị form thêm mới khách hàng (Modal).
     * GET /customers/add
     *
     * @param model Model để truyền object Customer rỗng cho form.
     * @return Tên template "customers/list" với Modal thêm mới được kích hoạt.
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("customer", new Customer());
        model.addAttribute("isEdit", false);
        model.addAttribute("customers", customerService.getAllCustomers());
        return "customers/list";
    }

    /**
     * Xử lý submit form thêm mới khách hàng.
     * POST /customers/add
     * Kiểm tra số điện thoại đã tồn tại trước khi thêm.
     *
     * @param customer           Dữ liệu customer từ form.
     * @param redirectAttributes Flash message sau redirect.
     * @return Redirect về danh sách khách hàng.
     */
    @PostMapping("/add")
    public String addCustomer(@ModelAttribute Customer customer, RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra số điện thoại đã tồn tại chưa
            if (customerService.isPhoneExists(customer.getPhone(), null)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Số điện thoại đã tồn tại!");
                return "redirect:/customers";
            }

            customerService.addCustomer(customer);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm khách hàng: " + e.getMessage());
        }
        return "redirect:/customers";
    }

    /**
     * Hiển thị form chỉnh sửa khách hàng (Modal).
     * GET /customers/edit/{id}
     *
     * @param id    ID của khách hàng cần sửa.
     * @param model Model để truyền customer sang form.
     * @return Tên template "customers/list" với Modal chỉnh sửa được kích hoạt.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Customer customer = customerService.getCustomerById(id).orElse(null);
        if (customer != null) {
            model.addAttribute("customer", customer);
            model.addAttribute("isEdit", true);
            model.addAttribute("customers", customerService.getAllCustomers());
            return "customers/list";
        }
        return "redirect:/customers";
    }

    /**
     * Xử lý submit form cập nhật khách hàng.
     * POST /customers/update/{id}
     *
     * @param id                 ID của khách hàng cần cập nhật.
     * @param customer           Dữ liệu customer mới từ form.
     * @param redirectAttributes Flash message sau redirect.
     * @return Redirect về danh sách khách hàng.
     */
    @PostMapping("/update/{id}")
    public String updateCustomer(@PathVariable Integer id, @ModelAttribute Customer customer,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra số điện thoại (loại trừ chính khách hàng đang sửa)
            if (customerService.isPhoneExists(customer.getPhone(), id)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Số điện thoại đã tồn tại!");
                return "redirect:/customers";
            }

            Customer updatedCustomer = customerService.updateCustomer(id, customer);
            if (updatedCustomer != null) {
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật khách hàng thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy khách hàng!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật khách hàng: " + e.getMessage());
        }
        return "redirect:/customers";
    }

    /**
     * Xử lý xóa khách hàng (soft delete).
     * GET /customers/delete/{id}
     *
     * @param id                 ID của khách hàng cần xóa.
     * @param redirectAttributes Flash message.
     * @return Redirect về danh sách khách hàng.
     */
    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            boolean deleted = customerService.deleteCustomer(id);
            if (deleted) {
                redirectAttributes.addFlashAttribute("successMessage", "Xóa khách hàng thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy khách hàng!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa khách hàng: " + e.getMessage());
        }
        return "redirect:/customers";
    }

    /**
     * Hiển thị danh sách khách hàng với phân trang.
     * GET /customers/paged?page=0&size=5
     *
     * @param page  Số trang hiện tại.
     * @param size  Số lượng khách hàng mỗi trang.
     * @param model Model để truyền dữ liệu sang view.
     * @return Tên template "customers/list-paged".
     */
    @GetMapping("/paged")
    public String listCustomersPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        Page<Customer> customerPage = customerService.getCustomersPaged(page, size);
        model.addAttribute("customers", customerPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", customerPage.getTotalPages());
        model.addAttribute("totalItems", customerPage.getTotalElements());

        return "customers/list-paged";
    }
}