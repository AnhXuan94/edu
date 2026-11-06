package ra.edu.service;

import ra.edu.entity.Product;
import ra.edu.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service layer xử lý business logic cho Product.
 * @Service: Đánh dấu đây là Spring Bean, được quản lý bởi Spring Container.
 * Đóng vai trò trung gian giữa Controller và Repository.
 */
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Lấy danh sách tất cả sản phẩm chưa bị xóa (soft delete).
     * @return Danh sách Product.
     */
    public List<Product> getAllProducts() {
        return productRepository.findByIsDeletedFalse();
    }

    /**
     * Lấy sản phẩm theo ID.
     * @param id ID của sản phẩm.
     * @return Optional chứa Product nếu tìm thấy và chưa bị xóa.
     */
    public Optional<Product> getProductById(Integer id) {
        return productRepository.findById(id)
                .filter(product -> !product.getIsDeleted());
    }

    /**
     * Thêm mới sản phẩm.
     * @param product Sản phẩm cần thêm.
     * @return Sản phẩm đã được lưu (có ID được generate).
     */
    public Product addProduct(Product product) {
        product.setIsDeleted(false);
        product.setIsActive(true); // Mặc định sản phẩm mới là Active
        return productRepository.save(product);
    }

    /**
     * Cập nhật thông tin sản phẩm (không cập nhật ID).
     * @param id ID của sản phẩm cần cập nhật.
     * @param productDetails Thông tin mới của sản phẩm.
     * @return Sản phẩm đã cập nhật, hoặc null nếu không tìm thấy.
     */
    public Product updateProduct(Integer id, Product productDetails) {
        return productRepository.findById(id)
                .filter(product -> !product.getIsDeleted())
                .map(product -> {
                    product.setName(productDetails.getName());
                    product.setBrand(productDetails.getBrand());
                    product.setPrice(productDetails.getPrice());
                    product.setStock(productDetails.getStock());
                    product.setImage(productDetails.getImage());
                    return productRepository.save(product);
                })
                .orElse(null);
    }

    /**
     * Xóa mềm sản phẩm (soft delete).
     * Không xóa hẳn khỏi database mà chỉ đánh dấu is_deleted = true.
     * @param id ID của sản phẩm cần xóa.
     * @return true nếu xóa thành công, false nếu không tìm thấy.
     */
    public boolean deleteProduct(Integer id) {
        return productRepository.findById(id)
                .filter(product -> !product.getIsDeleted())
                .map(product -> {
                    product.setIsDeleted(true);
                    productRepository.save(product);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Chuyển đổi trạng thái Active/Deactivate của sản phẩm.
     * Nếu đang Active → chuyển sang Deactivate và ngược lại.
     * @param id ID của sản phẩm.
     */
    public void toggleStatus(Integer id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null && !product.getIsDeleted()) {
            product.setIsActive(!product.getIsActive()); // Đảo ngược true/false
            productRepository.save(product);
        }
    }

    /**
     * Tìm kiếm sản phẩm theo từ khóa (tên hoặc brand).
     * @param keyword Từ khóa tìm kiếm.
     * @return Danh sách sản phẩm thỏa mãn.
     */
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByIsDeletedFalseAndNameContainingOrBrandContaining(keyword, keyword);
    }

    /**
     * Tìm kiếm sản phẩm theo khoảng giá.
     * @param minPrice Giá thấp nhất.
     * @param maxPrice Giá cao nhất.
     * @return Danh sách sản phẩm trong khoảng giá.
     */
    public List<Product> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceRange(minPrice, maxPrice);
    }

    /**
     * Tìm kiếm sản phẩm theo số lượng tồn kho tối thiểu.
     * @param minStock Số lượng tồn kho tối thiểu.
     * @return Danh sách sản phẩm có tồn kho >= minStock.
     */
    public List<Product> searchByStock(Integer minStock) {
        return productRepository.findByStockGreaterThanEqual(minStock);
    }

    /**
     * Giảm số lượng tồn kho khi tạo đơn hàng.
     * Sử dụng synchronized để tránh race condition khi nhiều request cùng lúc.
     * @param productId ID sản phẩm.
     * @param quantity Số lượng cần giảm.
     * @return true nếu giảm thành công, false nếu không đủ tồn kho.
     */
    public synchronized boolean decreaseStock(Integer productId, Integer quantity) {
        return productRepository.findById(productId)
                .filter(product -> !product.getIsDeleted() && product.getStock() >= quantity)
                .map(product -> {
                    product.setStock(product.getStock() - quantity);
                    productRepository.save(product);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Tăng số lượng tồn kho khi hủy đơn hàng.
     * @param productId ID sản phẩm.
     * @param quantity Số lượng cần tăng.
     */
    public void increaseStock(Integer productId, Integer quantity) {
        productRepository.findById(productId).ifPresent(product -> {
            product.setStock(product.getStock() + quantity);
            productRepository.save(product);
        });
    }
}