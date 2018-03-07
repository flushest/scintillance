package com.scintillance.common.web.util;

import lombok.extern.log4j.Log4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * File utilities.
 * @author  HT-LiChuanbin 
 * @version 2017年7月25日 下午3:56:15
 */
//@Slf4j
@Log4j
public class FileUtils {
    private FileUtils() {
    }

    public static void main(String[] arsgs) {
        String filePath = "E:/a/temp";
        File file = new File(filePath);
        String content = "content for testing.";
        FileUtils.write(file, content);
    }

    /**
     * 写入内容到指定文件
     * @param file 输出文件
     * @param content 内容
     */
    // 
    public static void write(File file, String content) {
        FileOutputStream fileOutputStream = null;
        PrintStream printStream = null;
        try {
            fileOutputStream = new FileOutputStream(file, true);
            printStream = new PrintStream(fileOutputStream, true);
            System.setOut(printStream);
            System.out.println(content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                printStream.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 存储MultipartFile文件.
     * @param path 文件所在目录路径.
     * @param bytes
     * @author     HT-LiChuanbin 
     * @version    2017年7月26日 上午11:23:06
     */
    public static boolean writeViaNIO(String path, byte[] bytes) {
        try (FileOutputStream fos = new FileOutputStream(new File(path))) {
            FileChannel fileChannel = fos.getChannel();
            fileChannel.write(ByteBuffer.wrap(bytes));
            return true;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public static boolean write(String path, byte[] bytes) {
        try (FileOutputStream fos = new FileOutputStream(new File(path), true)) {
            // TODO 使用NIO处理.
            // fos.getChannel();
            fos.write(bytes);
            return true;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 文件复制普通方法
     * @param fromFile 源文件
     * @param toFile 目标文件
     * @return 是否复制成功
     * @author Thomas Lee
     * @version 2017年3月28日 上午10:03:29
     */
    public static boolean copy(File fromFile, File toFile) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(fromFile);
            fileOutputStream = new FileOutputStream(toFile);

            byte[] buffer = new byte[1024];
            int length = 0;
            while (-1 != (length = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, length);
            }

            fileOutputStream.flush();
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            return false;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return false;
        } finally {
            if (null != fileInputStream) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
            if (null != fileOutputStream) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return true;
    }

    /**
     * 使用ARM（Automatic Resource Management，自动资源管理）语句，使用try-with-resources语句进行文件复制
     * @param fromFile
     * @param toFile
     * @return
     * @author Thomas Lee
     * @version 2017年3月28日 下午2:59:41
     */
    public static boolean copyWithARM(File fromFile, File toFile) {
        try (FileInputStream fileInputStream = new FileInputStream(fromFile); FileOutputStream fileOutputStream = new FileOutputStream(toFile)) {
            byte[] buffer = new byte[1024];
            int length = 0;
            while (-1 != (length = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, length);
            }
            fileOutputStream.flush();
            return true;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 通过NIO进行复制文件.
     * @param fromFile 读取的文件.
     * @param toFile 写入的文件.
     * @return 是否成功.
     * @author Thomas Lee
     * @version 2017年3月28日 下午3:16:21
     */
    public static void copyWithNIO(File fromFile, File toFile) {
        // 可以把下面的两个ARM语句合并为一个语句块：try (FileInputStream fileInputStream = new FileInputStream(fromFile); FileOutputStream fileOutputStream = new FileOutputStream(toFile)) {
        try (FileInputStream fileInputStream = new FileInputStream(fromFile)) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(toFile)) {
                FileChannel inFileChannel = fileInputStream.getChannel();
                FileChannel outFileChannel = fileOutputStream.getChannel();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                while (-1 != inFileChannel.read(buffer)) {
                    buffer.flip();
                    outFileChannel.write(buffer);
                    buffer.clear();
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 测试文件复制
     * @author Thomas Lee
     * @version 2017年3月28日 上午11:12:59
     */
    public void testFileCopy() {
        // 特别注意，SundryTest.class.getClassLoader()获取的是运行时的类，那.getResource("fromFile.file").getFile()获取的也是相应的运行时候的路径，也就是maven target目录下面的路径
        // File fromFile = new File(SundryTest.class.getClassLoader().getResource("fromFile.file").getFile());
        // File toFile = new File(SundryTest.class.getClassLoader().getResource("toFile.file").getFile());
        File fromFile = new File("C:\\Users\\ucmed\\Desktop\\fromFile.txt");
        File toFile = new File("C:\\Users\\ucmed\\Desktop\\toFile.txt");
        FileUtils.copyWithNIO(fromFile, toFile);
    }

    /**
     * 文件转换为字节流.
     * @param filePath 文件路径.
     * @return         文件流.
     * @author         Thomas Lee
     * @version        2017年7月15日 下午6:25:15
     */
    public static byte[] file2bytes(File file) {
        byte[] buffer = null;
        try {
            byte[] b = new byte[1024];
            try (FileInputStream fis = new FileInputStream(file)) {
                int n;
                try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                    while ((n = fis.read(b)) != -1) {
                        bos.write(b, 0, n);
                    }
                    buffer = bos.toByteArray();
                    bos.close();
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return buffer;
    }

    /**
     * 字节流转换为文件.
     * @param buf      字节流.
     * @param filePath 文件路径.
     * @param fileName 文件名称.
     * @author         Thomas Lee
     * @version        2017年7月15日 下午6:26:14
     */
    public static File bytes2File(byte[] buf, String filePath, String fileName) {
        File dir = new File(filePath);
        if (!dir.exists() && dir.isDirectory()) {
            dir.mkdirs();
        }
        File file = new File(filePath + File.separator + fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            try (BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                bos.write(buf);
            }
            return file;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}