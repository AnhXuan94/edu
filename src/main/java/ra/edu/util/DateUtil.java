package ra.edu.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class chứa các hàm xử lý ngày tháng dùng chung.
 */
public class DateUtil {

    /**
     * Format LocalDateTime thành chuỗi theo định dạng dd/MM/yyyy HH:mm.
     * Ví dụ: 2026-11-05 14:30 → "05/11/2026 14:30"
     *
     * @param dateTime Đối tượng LocalDateTime cần format.
     * @return Chuỗi ngày giờ đã được format.
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateTime.format(formatter);
    }

    /**
     * Format LocalDateTime thành chuỗi theo định dạng dd/MM/yyyy.
     * Ví dụ: 2026-11-05 14:30 → "05/11/2026"
     *
     * @param dateTime Đối tượng LocalDateTime cần format.
     * @return Chuỗi ngày đã được format.
     */
    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return dateTime.format(formatter);
    }
}