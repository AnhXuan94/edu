package ra.edu.service;

import ra.edu.entity.Customer;
import ra.edu.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer xử lý business logic cho Customer.
 */
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Lấy danh sách tất cả khách hàng chưa bị xóa.
     */
    public List<Customer> getAllCustomers() {
        return customerRepository.findByIsDeletedFalse();
    }

    /**
     * Lấy danh sách khách hàng với phân trang.
     */
    public Page<Customer> getCustomersPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return customerRepository.findByIsDeletedFalse(pageable);
    }

    /**
     * Lấy khách hàng theo ID.
     */
    public Optional<Customer> getCustomerById(Integer id) {
        return customerRepository.findById(id)
                .filter(customer -> !customer.getIsDeleted());
    }

    /**
     * Thêm mới khách hàng.
     * Kiểm tra số điện thoại đã tồn tại chưa trước khi thêm.
     */
    public Customer addCustomer(Customer customer) {
        customer.setIsDeleted(false);
        return customerRepository.save(customer);
    }

    /**
     * Cập nhật thông tin khách hàng (không cập nhật ID).
     */
    public Customer updateCustomer(Integer id, Customer customerDetails) {
        return customerRepository.findById(id)
                .filter(customer -> !customer.getIsDeleted())
                .map(customer -> {
                    customer.setName(customerDetails.getName());
                    customer.setPhone(customerDetails.getPhone());
                    customer.setEmail(customerDetails.getEmail());
                    customer.setAddress(customerDetails.getAddress());
                    return customerRepository.save(customer);
                })
                .orElse(null);
    }

    /**
     * Xóa mềm khách hàng.
     */
    public boolean deleteCustomer(Integer id) {
        return customerRepository.findById(id)
                .filter(customer -> !customer.getIsDeleted())
                .map(customer -> {
                    customer.setIsDeleted(true);
                    customerRepository.save(customer);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Kiểm tra số điện thoại đã tồn tại chưa.
     * @param phone Số điện thoại cần kiểm tra.
     * @param excludeId ID cần loại trừ (khi sửa, không so sánh với chính nó).
     * @return true nếu số điện thoại đã tồn tại.
     */
    public boolean isPhoneExists(String phone, Integer excludeId) {
        return customerRepository.findByPhoneAndIsDeletedFalse(phone)
                .map(customer -> !customer.getId().equals(excludeId))
                .orElse(false);
    }
}