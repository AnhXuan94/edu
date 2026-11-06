package ra.edu.util;

/**
 * Utility class chứa các hàm xử lý chuỗi dùng chung.
 * Các phương thức trong class này là static, có thể gọi trực tiếp không cần khởi tạo object.
 */
public class StringUtil {

    /**
     * Kiểm tra chuỗi có phải là null hoặc rỗng (chỉ chứa khoảng trắng) hay không.
     * @param str Chuỗi cần kiểm tra.
     * @return true nếu chuỗi null hoặc rỗng, false nếu có nội dung.
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Kiểm tra password có hợp lệ không theo yêu cầu:
     * - Tối thiểu 6 ký tự
     * - Có ít nhất 1 chữ hoa
     * - Có ít nhất 1 chữ thường
     * - Có ít nhất 1 chữ số
     * - Có ít nhất 1 ký tự đặc biệt (@$!%*?&)
     *
     * @param password Mật khẩu cần kiểm tra.
     * @return true nếu password hợp lệ, false nếu không hợp lệ.
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        // Biểu thức chính quy (Regex) kiểm tra các yêu cầu
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$";
        return password.matches(regex);
    }

    /**
     * Format số tiền thành chuỗi có dấu phẩy ngăn cách hàng nghìn.
     * Ví dụ: 29990000 → "29,990,000"
     *
     * @param amount Số tiền cần format.
     * @return Chuỗi đã được format.
     */
    public static String formatCurrency(double amount) {
        return String.format("%,.0f", amount);
    }
}