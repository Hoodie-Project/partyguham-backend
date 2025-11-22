package com.partyguham.infra.s3;

public class S3StorageException extends RuntimeException {

    public S3StorageException(String message) {
        super(message);
    }

    public S3StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}