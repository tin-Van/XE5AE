package com.poly.service;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.ServletContext;

@Service
public class HomeServiceImpl implements HomeService {
    @Autowired
    private ServletContext app;

    @Override
    public File saveImage(MultipartFile file) {
        // Xác định thư mục lưu tệp
        File dir = new File(app.getRealPath("/img/"));
        System.out.println("Đang lưu tệp vào: " + dir.getAbsolutePath()); // Ghi lại đường dẫn

        // Tạo thư mục nếu nó không tồn tại
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("Không thể tạo thư mục: " + dir.getAbsolutePath());
        }

        // Tạo đối tượng File cho tệp sẽ lưu
        File saveFile = new File(dir, file.getOriginalFilename());

        try {
            // Xóa tệp nếu nó đã tồn tại để ghi đè
            if (saveFile.exists() && !saveFile.delete()) {
                throw new RuntimeException("Không thể xóa tệp cũ: " + saveFile.getAbsolutePath());
            }

            // Ghi tệp vào thư mục
            file.transferTo(saveFile);

            return saveFile;
        } catch (IOException e) {
            // Xử lý lỗi IOException và cung cấp thông báo lỗi chi tiết
            throw new RuntimeException("Lỗi I/O khi lưu tệp: " + e.getMessage(), e);
        } catch (Exception e) {
            // Xử lý lỗi khác
            throw new RuntimeException("Lỗi khi lưu tệp: " + e.getMessage(), e);
        }
    }
}
