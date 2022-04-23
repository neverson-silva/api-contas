package com.dersaun.apicontas.services.impl.storage;

import com.dersaun.apicontas.services.StorageService;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.common.io.ByteStreams;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FirebaseStorageServiceImpl implements StorageService {

    @Value("${FIREBASE_BUCKET_NAME}")
    private String bucketName;

    @Autowired
    private Logger logger;

    @Autowired
    private Storage storage;

    @Override
    public String upload(MultipartFile file, String filename) throws IOException {
        String fileName = filename + "." + com.google.common.io.Files.getFileExtension(file.getOriginalFilename());
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
        Blob blob = storage.create(blobInfo, file.getBytes());

        return blob.getMediaLink();
    }

    @Override
    public Object download(String filename) throws IOException {

        Blob blob = storage.get(BlobId.of(bucketName, filename));
        ReadChannel reader = blob.reader();
        InputStream inputStream = Channels.newInputStream(reader);

        byte[] content = null;
        logger.info("File downloaded successfully.");

        content = ByteStreams.toByteArray(inputStream);

        ByteArrayResource byteArrayResource = new ByteArrayResource(content);

        return byteArrayResource;
    }
}
