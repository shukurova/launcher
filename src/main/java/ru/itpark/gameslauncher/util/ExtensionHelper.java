package ru.itpark.gameslauncher.util;

import org.springframework.http.MediaType;
import ru.itpark.gameslauncher.exception.UnsupportedFileTypeException;

import java.util.Map;
import java.util.Optional;

public class ExtensionHelper {
    private static final Map<String, String> MIME_TYPE_EXT_MAP = Map.of(
            MediaType.IMAGE_JPEG_VALUE, ".jpg",
            MediaType.IMAGE_PNG_VALUE, ".png"
    );

    public static String extensionFromMime(String type) {
        return Optional.ofNullable(MIME_TYPE_EXT_MAP.get(type)).orElseThrow(() ->
                new UnsupportedFileTypeException("This files type don't supported! You can upload .jpg, .png files."));
    }
}