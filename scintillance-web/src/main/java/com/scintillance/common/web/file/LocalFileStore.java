package com.scintillance.common.web.file;

import com.scintillance.common.annotation.DelegateElement;
import com.scintillance.common.exception.SciException;
import com.scintillance.common.web.util.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2018/3/12 0012.
 */
@DelegateElement(key = "local")
public class LocalFileStore implements FileStore {
    @Override
    public boolean store(FileItem fileItem) {
        try {
            File storeFile = FileUtils.bytes2File(fileItem.getFile().getBytes(), fileItem.getTempPath(), fileItem.getFileName());
            return storeFile != null;
        } catch (IOException e) {
            throw new SciException(String.format("failed to save file[%s]", fileItem.getTempPath() + File.separator + fileItem.getFileName()), e);
        }
    }

    @Override
    public byte[] read(FileItem fileItem) {
        return FileUtils.file2bytes(fileItem.getTempPath(),fileItem.getFileName());
    }
}
