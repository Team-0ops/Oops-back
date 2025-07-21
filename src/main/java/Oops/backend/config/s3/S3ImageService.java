package capstone.mju.backend.global.s3;

import capstone.mju.backend.domain.common.error.ErrorCode;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3ImageService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    public String upload(MultipartFile image) {
        if (image.isEmpty() || Objects.isNull(image.getOriginalFilename())) {
            throw new S3Exception(ErrorCode.EMPTY_FILE_EXCEPTION);
        }
        return this.uploadImage(image);
    }

    private String uploadImage(MultipartFile image) {
        this.validateImageFileExtension(image.getOriginalFilename());
        try {
            return this.uploadImageToS3(image);
        } catch (IOException e) {
            throw new S3Exception(ErrorCode.IO_EXCEPTION_ON_IMAGE_UPLOAD);
        }
    }

    private void validateImageFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new S3Exception(ErrorCode.NO_FILE_EXTENSION);
        }

        String extension = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtensionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (!allowedExtensionList.contains(extension)) {
            throw new S3Exception(ErrorCode.INVALID_FILE_EXTENSION);
        }
    }

    private String uploadImageToS3(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new S3Exception(ErrorCode.EMPTY_FILE_EXCEPTION);
        }

        String originalFilename = image.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new S3Exception(ErrorCode.NO_FILE_EXTENSION);
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + "_" + originalFilename; // 유니크 파일명

        InputStream is = image.getInputStream();
        byte[] bytes = IOUtils.toByteArray(is);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/" + extension);
        metadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try {
            System.out.println("Uploading image to S3: " + s3FileName);
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, metadata);
                           // .withCannedAcl(CannedAccessControlList.PublicRead);
            amazonS3.putObject(putObjectRequest); // S3에 이미지 업로드

            System.out.println("S3 업로드 성공: " + s3FileName);
        } catch (Exception e) {
            System.err.println("S3 업로드 실패: " + e.getMessage());
            e.printStackTrace();
            throw new S3Exception(ErrorCode.PUT_OBJECT_EXCEPTION);
        } finally {
            byteArrayInputStream.close();
            is.close();
        }

        return amazonS3.getUrl(bucketName, s3FileName).toString();
    }


    public void deleteImageFromS3(String imageAddress) {
        String key = getKeyFromImageAddress(imageAddress);
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
        } catch (Exception e) {
            throw new S3Exception(ErrorCode.IO_EXCEPTION_ON_IMAGE_DELETE);
        }
    }

    private String getKeyFromImageAddress(String imageAddress) {
        try {
            URL url = new URL(imageAddress);
            String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
            return decodingKey.substring(1);
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            throw new S3Exception(ErrorCode.IO_EXCEPTION_ON_IMAGE_DELETE);
        }
    }
}