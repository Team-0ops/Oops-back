package Oops.backend.config.s3;

import Oops.backend.common.status.ErrorStatus;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3ImageService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    public String upload(MultipartFile image, String keyPrefix, String fileName) {
        if (image.isEmpty() || Objects.isNull(image.getOriginalFilename())) {
            throw new S3Exception(ErrorStatus.EMPTY_FILE_EXCEPTION);
        }
        return this.uploadImage(image, keyPrefix, fileName);
    }

    private String uploadImage(MultipartFile image, String keyPrefix, String fileName) {
        this.validateImageFileExtension(image.getOriginalFilename());
        try {
            return this.uploadImageToS3(image, keyPrefix, fileName);
        } catch (IOException e) {
            throw new S3Exception(ErrorStatus.IO_EXCEPTION_ON_IMAGE_UPLOAD);
        }
    }

    private void validateImageFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new S3Exception(ErrorStatus.NO_FILE_EXTENSION);
        }

        String extension = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtensionList = Arrays.asList("jpg", "jpeg", "png", "gif", "svg");

        if (!allowedExtensionList.contains(extension)) {
            throw new S3Exception(ErrorStatus.INVALID_FILE_EXTENSION);
        }
    }

    private String uploadImageToS3(MultipartFile image, String keyPrefix, String fileName) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new S3Exception(ErrorStatus.EMPTY_FILE_EXCEPTION);
        }

        String originalFilename = image.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new S3Exception(ErrorStatus.NO_FILE_EXTENSION);
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + "_" + fileName + "." + extension; // 유니크 파일명
        String keyName = keyPrefix + "/" + s3FileName;

        InputStream is = image.getInputStream();
        byte[] bytes = IOUtils.toByteArray(is);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/" + extension);
        metadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try {
            System.out.println("Uploading image to S3: " + s3FileName);
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, keyName, byteArrayInputStream, metadata);
            amazonS3.putObject(putObjectRequest);

            System.out.println("S3 업로드 성공: " + s3FileName);
        } catch (Exception e) {
            System.err.println("S3 업로드 실패: " + e.getMessage());
            e.printStackTrace();
            throw new S3Exception(ErrorStatus.PUT_OBJECT_EXCEPTION);
        } finally {
            byteArrayInputStream.close();
            is.close();
        }

        return keyName;
    }


    public void deleteImageFromS3(String imageUrl) {
        String key = getKeyFromImageAddress(imageUrl);
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
        } catch (Exception e) {
            throw new S3Exception(ErrorStatus.IO_EXCEPTION_ON_IMAGE_DELETE);
        }
    }

    private String getKeyFromImageAddress(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
            return decodingKey.substring(1);
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            throw new S3Exception(ErrorStatus.IO_EXCEPTION_ON_IMAGE_DELETE);
        }
    }

    /**
     * S3에서 파일을 가져오는 메소드 ("S3의 사진 URL"을 사용자에게 제공하는 방식)
     * @param key 가져올 사진 파일 값 (DB에 저장된 image 파일 이름)
     * @return S3에 존재하는 이미지에 접근할 수 있는 URL
     */
    public String getPreSignedUrl(String key){

        //유효 시간 5분
        Date expiration = new Date(System.currentTimeMillis()+ 1000 * 60 * 5);

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key)
                                                                    .withMethod(HttpMethod.GET)
                                                                    .withExpiration(expiration);

        return amazonS3.generatePresignedUrl(request).toString();
    }
}