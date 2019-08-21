package ru.itpark.gameslauncher.service;

import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.itpark.gameslauncher.domain.upload.UploadDomain;
import ru.itpark.gameslauncher.domain.upload.UploadFileDomain;
import ru.itpark.gameslauncher.exception.FileUploadException;
import ru.itpark.gameslauncher.repository.FileRepository;
import ru.itpark.gameslauncher.util.ExtensionHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@Transactional
public class FileService {

    private FileRepository fileRepository;
    private final Path uploadPath = Path.of("/tmp/files");

    public FileService() throws IOException {
        fileRepository = new FileRepository();
        if (Files.notExists(uploadPath)) {
            Files.createDirectory(uploadPath);
        }
    }

    public UploadFileDomain save(UploadDomain domain) {
        String type;
        String name;
        try {
            MultipartFile file = domain.getFile();
            type = new Tika().detect(domain.getFile().getInputStream());
            var ext = ExtensionHelper.extensionFromMime(type);

            name = UUID.randomUUID().toString() + ext;
            file.transferTo(uploadPath.resolve(name));
            fileRepository.saveFileData();
        } catch (IOException e) {
            throw new FileUploadException("File loading error! Please, try later");
        }

        return new UploadFileDomain(name);
    }
}
