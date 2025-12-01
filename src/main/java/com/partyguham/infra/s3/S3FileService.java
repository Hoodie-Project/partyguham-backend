package com.partyguham.infra.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * AWS S3 업로드/삭제를 담당하는 서비스
 *
 * - 컨트롤러/도메인 서비스에서는 이 클래스만 의존하도록 두는 편이 깔끔함
 * - key 는 "folder/타임스탬프-UUID.확장자" 형태로 생성
 * - 예외는 모두 S3StorageException 으로 래핑해서 바깥으로 던짐
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class S3FileService {

    private final S3Client s3Client;
    private final S3Properties props; // aws.s3.* 바인딩

    /**
     * 파일 업로드
     *
     * @param file   업로드할 MultipartFile
     * @param folder 논리 폴더 (예: "banner", "user/profile")
     *               null/blank 이면 yml에 설정한 base-folder 사용
     * @return S3 object key (예: banner/1731480000000-uuid.jpg)
     */
    public String upload(MultipartFile file, S3Folder folder) {
        if (file == null || file.isEmpty()) {
            throw new S3StorageException("업로드할 파일이 없습니다.");
        }

        String s3Folder = folder.path();
        String key = buildObjectKey(s3Folder, file.getOriginalFilename());

        // S3 PutObject
        try (InputStream is = file.getInputStream()) {
            PutObjectRequest putReq = PutObjectRequest.builder()
                    .bucket(props.getS3().getBucket())
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    putReq,
                    RequestBody.fromInputStream(is, file.getSize())
            );

            return key; // DB에는 key를 저장해두고 사용

        } catch (IOException e) {
            // 파일 스트림 관련 문제
            throw new S3StorageException("S3 업로드 중 I/O 오류가 발생했습니다.", e);
        } catch (S3Exception | SdkClientException e) {
            // AWS SDK / 네트워크 등 문제
            throw new S3StorageException("S3 업로드 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 파일 삭제
     *
     * @param key S3 object key (업로드 시 리턴 받은 key 그대로)
     */
    public void delete(String key) {
        if (key == null || key.isBlank()) {
            return; // 삭제할 key 없으면 그냥 무시
        }

        try {
            DeleteObjectRequest delReq = DeleteObjectRequest.builder()
                    .bucket(props.getS3().getBucket())
                    .key(key)
                    .build();

            s3Client.deleteObject(delReq);

        } catch (S3Exception | SdkClientException e) {
            // 보통 삭제 실패는 치명적이지 않아서 로그만 찍고 넘기는 경우도 많음
            // 여기서는 일단 예외를 래핑해서 던지도록 구현
            throw new S3StorageException("S3 삭제 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 롤백 방지 파일 삭제
     *
     * @param key S3 object key
     */
    public void deleteSafely(String key) {
        try {
            delete(key);
        } catch (Exception e) {
            // 여기서는 로그만 찍고 무시 (레코드 삭제는 그대로 진행)
            log.warn("[S3] 파일 삭제 실패 key={}", key, e);
        }
    }

    /**
     * S3 key 로 접근 가능한 URL 생성
     * - CloudFront 를 쓰면 여기서 도메인만 바꿔주면 됨
     */
    public String getUrl(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        // 예: https://{bucket}.s3.{region}.amazonaws.com/{key}
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                props.getS3().getBucket(),
                props.getS3().getRegion(),
                key
        );
    }

    /**
     * 업로드용 Object Key 생성 헬퍼
     * - folder/타임스탬프-UUID.ext
     */
    private String buildObjectKey(String folder, String originalFilename) {
        String cleanFolder = folder.replaceAll("^/+", "").replaceAll("/+$", "");

        String ext = extractExtension(originalFilename);
        String randomName = System.currentTimeMillis() + "-" + UUID.randomUUID();

        return cleanFolder.isEmpty()
                ? randomName + ext
                : cleanFolder + "/" + randomName + ext;
    }

    /** 파일 확장자 추출 (없으면 빈 문자열) */
    private String extractExtension(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "";
        }
        int dot = originalFilename.lastIndexOf('.');
        if (dot == -1 || dot == originalFilename.length() - 1) {
            return "";
        }
        return originalFilename.substring(dot); // ".jpg" 같은 형태
    }
}