package ru.itpark.gameslauncher.service;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.itpark.gameslauncher.domain.UploadFileDomain;
import ru.itpark.gameslauncher.exception.FileUploadException;
import ru.itpark.gameslauncher.util.ExtensionHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@Transactional
public class FileService {

    private final Path uploadPath = Path.of(System.getProperty("user.dir") + "/src/tmp/files");

    public FileService() throws IOException {
        if (Files.notExists(uploadPath)) {
            Files.createDirectory(uploadPath);
        }
    }

    public UploadFileDomain saveFile(MultipartFile file) {
        String type;
        String name;
        try {
            type = new Tika().detect(file.getInputStream());
            var ext = ExtensionHelper.extensionFromMime(type);

            name = UUID.randomUUID().toString() + ext;
            file.transferTo(uploadPath.resolve(name));
        } catch (IOException e) {
            throw new FileUploadException("File loading error! Please, try later");
        }

        return new UploadFileDomain(name);
    }
}
