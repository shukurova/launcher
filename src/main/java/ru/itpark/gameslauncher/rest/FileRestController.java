package ru.itpark.gameslauncher.rest;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.itpark.gameslauncher.domain.UploadFileDomain;
import ru.itpark.gameslauncher.service.FileService;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class FileRestController {
    private final FileService fileService;

    @PostMapping
    public UploadFileDomain saveFile(@RequestParam MultipartFile file) {
        return fileService.saveFile(file);
    }
}
