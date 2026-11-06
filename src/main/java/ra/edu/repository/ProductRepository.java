package ra.edu.repository;

import ra.edu.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface cho bảng Product.
 * Kế thừa JpaRepository<Integer, Product> để được cung cấp sẵn CRUD methods.
 * Spring Data JPA tự động implement interface này tại runtime.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    /**
     * Tìm sản phẩm chưa bị xóa (is_deleted = false).
     * Phương thức đặt tên theo convention sẽ tự động generate query.
     */
    List<Product> findByIsDeletedFalse();

    /**
     * Tìm sản phẩm chưa bị xóa với phân trang.
     * Pageable: chứa thông tin page number, page size, sort.
     */
    Page<Product> findByIsDeletedFalse(Pageable pageable);

    /**
     * Tìm kiếm sản phẩm theo tên hoặc brand (tìm kiếm gần đúng).
     * %: wildcard trong SQL LIKE, tìm kiếm chứa từ khóa.
     */
    List<Product> findByIsDeletedFalseAndNameContainingOrBrandContaining(
            String nameKeyword, String brandKeyword);

    /**
     * Tìm kiếm sản phẩm theo khoảng giá.
     * @Query: Viết JPQL tùy chỉnh khi phương thức đặt tên không đủ.
     * :minPrice, :maxPrice: Named parameters, truyền từ @Param.
     */
    @Query("SELECT p FROM Product p WHERE p.isDeleted = false AND p.price BETWEEN :minPrice AND :maxPrice ORDER BY p.price ASC")
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Tìm kiếm sản phẩm theo số lượng tồn kho tối thiểu.
     */
    @Query("SELECT p FROM Product p WHERE p.isDeleted = false AND p.stock >= :minStock ORDER BY p.stock DESC")
    List<Product> findByStockGreaterThanEqual(@Param("minStock") Integer minStock);

    /**
     * Đếm số sản phẩm chưa bị xóa (dùng cho phân trang).
     */
    long countByIsDeletedFalse();
    /**
     * Tìm sản phẩm chưa bị xóa với phân trang và sắp xếp theo trường bất kỳ.
     * @param sortBy Trường cần sắp xếp (name, price, stock).
     * @param direction Hướng sắp xếp (ASC hoặc DESC).
     * @param pageable Thông tin phân trang.
     * @return Page chứa danh sách sản phẩm đã sắp xếp.
     */
    @Query("SELECT p FROM Product p WHERE p.isDeleted = false ORDER BY " +
            "CASE WHEN :sortBy = 'name' AND :direction = 'ASC' THEN p.name END ASC, " +
            "CASE WHEN :sortBy = 'name' AND :direction = 'DESC' THEN p.name END DESC, " +
            "CASE WHEN :sortBy = 'price' AND :direction = 'ASC' THEN p.price END ASC, " +
            "CASE WHEN :sortBy = 'price' AND :direction = 'DESC' THEN p.price END DESC, " +
            "CASE WHEN :sortBy = 'stock' AND :direction = 'ASC' THEN p.stock END ASC, " +
            "CASE WHEN :sortBy = 'stock' AND :direction = 'DESC' THEN p.stock END DESC")
    Page<Product> findByIsDeletedFalseWithSort(@Param("sortBy") String sortBy,
                                               @Param("direction") String direction,
                                               Pageable pageable);
}