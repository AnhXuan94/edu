package ra.edu.controller;

import ra.edu.entity.Product;
import ra.edu.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controller xử lý các request HTTP liên quan đến Product (Sản phẩm).
 * @Controller: Đánh dấu đây là Spring MVC Controller, trả về tên view (HTML).
 * @RequestMapping("/products"): Ánh xạ tất cả request có path /products đến controller này.
 */
@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * Hiển thị danh sách tất cả sản phẩm chưa bị xóa.
     * GET /products
     * @param model Model để truyền dữ liệu sang view Thymeleaf.
     * @return Tên template "products/list".
     */
    @GetMapping
    public String listProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "products/list";
    }

    /**
     * Hiển thị form thêm mới sản phẩm (tích hợp Modal trên trang danh sách).
     * GET /products/add
     * Truyền danh sách sản phẩm để render bảng nền, và object Product rỗng cho form.
     * Biến isEdit = false để JavaScript tự động mở Modal thêm mới.
     * @param model Model để truyền dữ liệu sang view.
     * @return Tên template "products/list".
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("isEdit", false);
        model.addAttribute("products", productService.getAllProducts());
        return "products/list";
    }

    /**
     * Xử lý submit form thêm mới sản phẩm với upload ảnh.
     * POST /products/add
     * @param product Dữ liệu product từ form (tự động bind từ request parameters).
     * @param imageFile File ảnh upload từ form (có thể null nếu không chọn file).
     * @param redirectAttributes Dùng để flash message sau khi redirect.
     * @return Redirect về danh sách sản phẩm (để tránh submit trùng lặp - PRG pattern).
     */
    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             RedirectAttributes redirectAttributes) {
        try {
            // Xử lý upload ảnh nếu có file được chọn
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = saveUploadedImage(imageFile);
                product.setImage("/uploads/products/" + fileName);
            } else if (product.getImage() == null || product.getImage().trim().isEmpty()) {
                // Nếu không upload ảnh và không có URL, dùng ảnh mặc định
                product.setImage("https://via.placeholder.com/150");
            }

            productService.addProduct(product);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm sản phẩm: " + e.getMessage());
        }
        return "redirect:/products";
    }

    /**
     * Hiển thị form chỉnh sửa sản phẩm (tích hợp Modal trên trang danh sách).
     * GET /products/edit/{id}
     * @param id ID của sản phẩm cần sửa (lấy từ path variable).
     * @param model Model để truyền product sang form.
     * @return Tên template "products/list" với Modal chỉnh sửa được kích hoạt.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Product product = productService.getProductById(id).orElse(null);
        if (product != null) {
            model.addAttribute("product", product);
            model.addAttribute("isEdit", true);
            model.addAttribute("products", productService.getAllProducts());
            return "products/list";
        }
        // Nếu không tìm thấy sản phẩm hoặc đã bị xóa, redirect về danh sách
        return "redirect:/products";
    }

    /**
     * Xử lý submit form cập nhật sản phẩm với upload ảnh.
     * POST /products/update/{id}
     * @param id ID của sản phẩm cần cập nhật.
     * @param product Dữ liệu product mới từ form.
     * @param imageFile File ảnh upload mới (có thể null nếu không thay đổi ảnh).
     * @param redirectAttributes Flash message sau redirect.
     * @return Redirect về danh sách sản phẩm.
     */
    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Integer id,
                                @ModelAttribute Product product,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                RedirectAttributes redirectAttributes) {
        try {
            // Xử lý upload ảnh mới nếu có file được chọn
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = saveUploadedImage(imageFile);
                product.setImage("/uploads/products/" + fileName);
            }

            Product updatedProduct = productService.updateProduct(id, product);
            if (updatedProduct != null) {
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật sản phẩm thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật sản phẩm: " + e.getMessage());
        }
        return "redirect:/products";
    }

    /**
     * Xử lý xóa sản phẩm (soft delete - xóa mềm).
     * GET /products/delete/{id}
     * @param id ID của sản phẩm cần xóa.
     * @param redirectAttributes Flash message.
     * @return Redirect về danh sách sản phẩm.
     */
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            boolean deleted = productService.deleteProduct(id);
            if (deleted) {
                redirectAttributes.addFlashAttribute("successMessage", "Xóa sản phẩm thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm hoặc đã bị xóa!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa sản phẩm: " + e.getMessage());
        }
        return "redirect:/products";
    }

    /**
     * Chuyển đổi trạng thái Active/Deactivate của sản phẩm.
     * GET /products/toggle-status/{id}
     * @param id ID của sản phẩm.
     * @param redirectAttributes Flash message.
     * @return Redirect về danh sách sản phẩm.
     */
    @GetMapping("/toggle-status/{id}")
    public String toggleProductStatus(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            productService.toggleStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã thay đổi trạng thái sản phẩm!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/products";
    }

    /**
     * Tìm kiếm sản phẩm theo từ khóa (tên hoặc brand).
     * GET /products/search?keyword=...
     * @param keyword Từ khóa tìm kiếm từ form.
     * @param model Model để truyền kết quả sang view.
     * @return Tên template "products/list".
     */
    @GetMapping("/search")
    public String searchProducts(@RequestParam String keyword, Model model) {
        List<Product> products = productService.searchProducts(keyword);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "products/list";
    }

    /**
     * Tìm kiếm sản phẩm theo khoảng giá (chức năng mở rộng - điểm khá/giỏi).
     * GET /products/search-by-price?minPrice=...&maxPrice=...
     * @param minPrice Giá thấp nhất.
     * @param maxPrice Giá cao nhất.
     * @param model Model để truyền kết quả.
     * @return Tên template "products/list".
     */
    @GetMapping("/search-by-price")
    public String searchByPrice(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Model model) {

        if (minPrice != null && maxPrice != null) {
            List<Product> products = productService.searchByPriceRange(minPrice, maxPrice);
            model.addAttribute("products", products);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);
        }
        return "products/list";
    }

    /**
     * Tìm kiếm sản phẩm theo số lượng tồn kho (chức năng mở rộng - điểm khá/giỏi).
     * GET /products/search-by-stock?minStock=...
     * @param minStock Số lượng tồn kho tối thiểu.
     * @param model Model để truyền kết quả.
     * @return Tên template "products/list".
     */
    @GetMapping("/search-by-stock")
    public String searchByStock(@RequestParam Integer minStock, Model model) {
        List<Product> products = productService.searchByStock(minStock);
        model.addAttribute("products", products);
        model.addAttribute("minStock", minStock);
        return "products/list";
    }

    /**
     * Lưu file ảnh upload vào thư mục uploads/products/.
     * Tên file được tạo ngẫu nhiên dựa trên timestamp để tránh trùng lặp.
     * Ví dụ: product_1699123456789.jpg
     *
     * @param file File ảnh cần lưu (MultipartFile từ form upload).
     * @return Tên file đã lưu (ví dụ: "product_1234567890.jpg").
     * @throws IOException nếu có lỗi khi ghi file vào ổ đĩa.
     */
    private String saveUploadedImage(MultipartFile file) throws IOException {
        // Lấy tên file gốc và extension (ví dụ: "anh.jpg" → extension = ".jpg")
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Tạo tên file duy nhất: product_ + timestamp + extension
        String fileName = "product_" + System.currentTimeMillis() + extension;

        // Tạo thư mục uploads/products/ nếu chưa tồn tại
        File uploadDir = new File("uploads/products/");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs(); // mkdirs() tạo cả thư mục cha nếu cần
        }

        // Tạo file đích và lưu file upload vào
        File dest = new File(uploadDir, fileName);
        file.transferTo(dest); // transferTo() là phương thức của Spring để lưu file

        return fileName;
    }
}