package com.bjpowernode.p2p.util;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @ProjectName: p2p
 * @Package: com.bjpowernode.p2p.util
 * @Description: java类作用描述
 * @Author: 王少伟
 * @CreateDate: 2020/12/23 16:09
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 */
public class FileUtil {

    public static void uploadFile(byte[] file, String filePath, String fileName) throws Exception {
        File targetFile = new File(filePath);
        if(!targetFile.exists()){
            targetFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(filePath+fileName);
        out.write(file);
        out.flush();
        out.close();
    }
}
