package vn.hoidanit.jobhunter.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.hoidanit.jobhunter.domain.response.file.ResUploadFileDTO;
import vn.hoidanit.jobhunter.service.FileService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.StorageExceotion;

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
    @ApiMessage("upload single file")
    public ResponseEntity<ResUploadFileDTO> upload(
            // lấy file upload
            @RequestParam(name = "file", required = false) MultipartFile file,
            // lấy folder lưu vào
            @RequestParam("folder") String folder)
            throws URISyntaxException, IOException, StorageExceotion {

        // skip validator
        if (file == null || file.isEmpty()) {
            throw new StorageExceotion("File is empty. Please upload a file.");
        }

        String fileName = file.getOriginalFilename();
        // file chấp nhận
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValid = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));

        if (!isValid) {
            throw new StorageExceotion("Invalid file extention. Only allow");
        }
        // create a directory if not exits
        this.fileService.createUploadFolder(baseURI + folder);
        // store file
        String uploadFile = this.fileService.store(file, folder);

        ResUploadFileDTO res = new ResUploadFileDTO(uploadFile, Instant.now());
        // return ra tên file ảnh gửi lên và folder lưu ảnh
        return ResponseEntity.ok().body(res);
    }

}
