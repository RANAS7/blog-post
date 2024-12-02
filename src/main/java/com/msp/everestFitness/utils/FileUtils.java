package com.msp.everestFitness.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
public class FileUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    private final Cloudinary cloudinary;

    @Value("${CLOUDINARY_FOLDER_NAME}")
    private String cloudinaryFolderName;

    public FileUtils(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    // Uploads a file to Cloudinary
    public String uploadFileToCloudinary(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            log.info("Attempted to upload an empty file");
            return null;
        }

        // Upload the file to Cloudinary and specify the folder
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", cloudinaryFolderName // Specify the folder name here
                )
        );

        String url = (String) uploadResult.get("url");
        log.info("Uploaded file to Cloudinary folder '{}': {}", cloudinaryFolderName, url);
        return url;
    }

    // Deletes a file from Cloudinary by public ID, which includes the folder path
    public void deleteFileFromCloudinary(String publicId) throws IOException {
        if (publicId == null || publicId.isEmpty()) {
            log.error("Invalid publicId. Cannot delete file from Cloudinary.");
            return;
        }

        // Delete the file from Cloudinary using the publicId (which includes the folder path)
        Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));

        // Check the result for confirmation and log accordingly
        if ("ok".equals(result.get("result"))) {
            log.info("Successfully deleted file from Cloudinary: {}", publicId);
        } else {
            log.error("Failed to delete file from Cloudinary: {}", publicId);
        }
    }

    // Generates a unique file name using UUID
    public String generateFileName(MultipartFile file) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename()); // Clean the original filename
        String extension = getFileExtension(originalFilename); // Extract the file extension
        String uuidPart = UUID.randomUUID().toString().substring(0, 20); // Generate a truncated UUID
        String generatedFileName = uuidPart + "." + extension; // Concatenate UUID part with the extension
        return generatedFileName;
    }

    // Extracts public ID from Cloudinary URL
    public String extractPublicIdFromUrl(String url) {
        if (url == null || url.isEmpty()) return null;
        // Assuming URL format like https://res.cloudinary.com/{cloud_name}/image/upload/{public_id}.{format}
        int lastSlash = url.lastIndexOf('/');
        int dotIndex = url.lastIndexOf('.');
        return (lastSlash != -1 && dotIndex != -1) ? url.substring(lastSlash + 1, dotIndex) : null;
    }

    // Extracts the file extension from the filename
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf(".");
        return (lastDot != -1) ? filename.substring(lastDot + 1) : "";
    }
}
