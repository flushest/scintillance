package com.scintillance.common.web.file;

import com.scintillance.common.annotation.Delegate;

/**
 * Created by Administrator on 2018/3/8 0008.
 */
@Delegate
public interface FileStore {
    boolean store(FileItem fileItem);

    byte[] read(FileItem fileItem);
}
