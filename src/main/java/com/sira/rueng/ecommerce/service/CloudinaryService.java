package com.sira.rueng.ecommerce.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Upload an image to Cloudinary
     *
     * @param file The image file to upload
     * @return Map containing upload results including the URL
     * @throws IOException If upload fails
     */
//    public Map uploadImage(MultipartFile file) throws IOException {
//        return cloudinary.uploader().upload(
//                file.getBytes(),
//                ObjectUtils.asMap(
//                        "public_id", "product_images/" + UUID.randomUUID(), // Generate a unique ID
//                        "overwrite", true,
//                        "resource_type", "auto"
//                )
//        );
//    }
    public Map uploadImage(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
//        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = "product_" + UUID.randomUUID().toString();
        Map params = ObjectUtils.asMap(
                "public_id" , newFileName,
                "use_filename", true,
                "unique_filename", false,
                "overwrite", true
        );
        System.out.println("hello");
        return cloudinary.uploader().upload(file.getBytes(), params);
    }

    /**
     * Get details of an uploaded image
     *
     * @param publicId The public ID of the image
     * @return Map containing image details
     * @throws Exception If retrieval fails
     */
    public Map getImageDetails(String publicId) throws Exception {
        return cloudinary.api().resource(
                publicId,
                ObjectUtils.emptyMap()
        );
    }

    /**
     * Generate a transformed URL for an image
     *
     * @param publicId The public ID of the image
     * @param width    Width for the transformation
     * @param height   Height for the transformation
     * @return The transformed image URL
     */
    public String getTransformedImageUrl(String publicId, int width, int height) {
        return cloudinary.url()
                .transformation(
                        new com.cloudinary.Transformation()
                                .width(width)
                                .height(height)
                                .crop("fill")
                )
                .generate(publicId);
    }
}