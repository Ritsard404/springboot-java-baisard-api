package com.ritsard.baisard.file.service.v4;

import com.ritsard.baisard.file.dto.v3.video.VideoUploadResponse;
import com.ritsard.baisard.file.entity.v2.FileInfo;
import com.ritsard.baisard.file.entity.v2.VideoFileInfo;
import com.ritsard.baisard.file.service.v3.SpringContext;
import com.ritsard.baisard.file.utils.FileIdUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface FileLoadable<T extends FileInfo> {
    List<T> getFileList();

    void setFileList(List<T> files);

    UUID getId();

    default void setFileIds(List<String> fileIds) throws IOException {
        if (fileIds != null && !fileIds.isEmpty()) {
            Class<?> fileType = this.resolveGenericType();
            UUID entityId = this.getId();
            String entityType = this.getClass().getSimpleName().toLowerCase();
            List<T> infos = new ArrayList();
            if (VideoFileInfo.class.isAssignableFrom(fileType)) {
                VideoUploadService uploadSvc = (VideoUploadService) SpringContext.getBean(VideoUploadService.class);
                VideoAccessService accessSvc = (VideoAccessService) SpringContext.getBean(VideoAccessService.class);

                for (String enc : fileIds) {
                    UUID fileId = FileIdUtils.decode(enc);
                    uploadSvc.attachVideoToEntity(fileId, entityType, entityId);
                    VideoUploadResponse resp = accessSvc.getVideoInfo(enc);
                    infos.add((T) VideoFileInfo.builder().fileId(fileId).filename(resp.getFilename()).originalFilename(resp.getOriginalFilename()).url(resp.getUrl()).mimeType(resp.getMimeType()).fileSize(resp.getFileSize()).fileExtension(resp.getFileExtension()).uploadedAt(resp.getUploadedAt()).duration(resp.getDuration()).build());
                }
            } else {
                EmbeddedFileUploadService<T> svc = (EmbeddedFileUploadService) SpringContext.getBean(EmbeddedFileUploadService.class);
                infos = svc.attachAndMoveFiles(fileIds, entityType, entityId);
            }

            this.setFileList(infos);
        }
    }

    default Class<?> resolveGenericType() {
        for (Type iface : this.getClass().getGenericInterfaces()) {
            if (iface instanceof ParameterizedType pt) {
                if (pt.getRawType().equals(FileLoadable.class)) {
                    Type arg = pt.getActualTypeArguments()[0];
                    if (arg instanceof Class) {
                        return (Class) arg;
                    }
                }
            }
        }

        return FileInfo.class;
    }

    default void addFile(T file) {
        List<T> list = this.getFileList();
        if (list == null) {
            list = new ArrayList();
        }

        list.add(file);
        this.setFileList(list);
    }
}
