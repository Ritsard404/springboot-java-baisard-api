package com.ritsard.baisard.file.service.v4;

import com.ritsard.baisard.file.entity.v2.VideoFileInfo;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

public interface FileStorageService {
    public Path storeTemp(UUID var1, MultipartFile var2) throws IOException;

    public void storeChunk(UUID var1, int var2, MultipartFile var3) throws IOException;

    public Path mergeChunks(UUID var1, int var2) throws IOException;

    public Path moveTempToFinal(UUID var1) throws IOException;

    public void deleteFinal(UUID var1) throws IOException;

    public VideoFileInfo buildFileInfo(UUID var1, Path var2, long var3, String var5);

    public Resource loadAsResource(UUID var1) throws IOException;

    public byte[] readRange(Resource var1, long var2, long var4) throws IOException;

    public Resource loadFrom(String var1, String var2) throws IOException;

    public String getTempRoot();

    public String getFinalRoot();
}

