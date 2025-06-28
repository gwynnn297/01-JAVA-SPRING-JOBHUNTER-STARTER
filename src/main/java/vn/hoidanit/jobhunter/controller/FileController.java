package vn.hoidanit.jobhunter.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.hoidanit.jobhunter.service.FileService;

@RestController
@RequestMapping("/api/v1")
public class FileController {
    private final FileService fileService;
    @Value("${hoidanit.upload-file.base-uri}")
    private String baseURI;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    public String upload(
            // lấy file upload
            @RequestParam("file") MultipartFile file,
            // lấy folder lưu vào
            @RequestParam("folder") String folder) throws URISyntaxException, IOException {
        // skip validator

        // create a directory if not exits
        this.fileService.createUploadFolder(baseURI + folder);
        //store file
        this.fileService.store(file, folder);
        // return ra tên file ảnh gửi lên và folder lưu ảnh
        return file.getOriginalFilename() + folder;
    }

}
