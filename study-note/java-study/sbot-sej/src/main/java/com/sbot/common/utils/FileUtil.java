package com.sbot.common.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.*;
import java.net.URLEncoder;

/**
 * <p>
 *
 * @author jintingying
 * @version 1.0
 * @date 2019/11/26
 */
public class FileUtil {
    public static String templatePath = "H:\\Growth-mine\\template";
    public static String uploadPath = "H:\\Growth-mine\\upload";

    /**
     * 创建一个随机名称、指定后缀的文件
     *
     * @return
     * @throws IOException
     */
    public static File createRomdonNameFile(String dir, String suffix) throws IOException {
        String fileName = ToolUtil.randomID35() + "." + suffix;
        File file = createFile(dir, fileName);
        return file;
    }

    /**
     * 删除一个文件
     *
     * @param absolutePath
     * @return
     */
    public static boolean deleteFile(String absolutePath) {
        File file = new File(absolutePath);
        if (file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }

    /**
     * 在目录下添加多个文件
     *
     * @param dir
     * @param filenames
     */
    public static void addFile(String dir, String... filenames) throws IOException {
        for (String filename : filenames) {
            createFile(dir, filename);
        }
    }

    /**
     * 在目录下追加多级目录，最后再追加一个文件
     *
     * @param dir
     * @param filename
     * @param subDirs
     */
    public static File createFile(String dir, String filename, String... subDirs) throws IOException {
        StringBuffer sf = new StringBuffer();
        sf.append(dir);
        if (subDirs != null)
            for (String child : subDirs) {
                sf.append(File.separator);
                sf.append(child);
            }

        File dirFile = new File(sf.toString());
        if (!dirFile.exists())
            dirFile.mkdirs();

        sf.append(filename);
        File file = new File(sf.toString());
        if (!file.exists())
            file.createNewFile();

        return file;
    }

    /**
     * 文件下载（单个）
     */
    public static void downloadFile(String sourceName, String destName) throws IOException {
        File sourceFile = new File(sourceName);
        if (!sourceFile.exists() || !sourceFile.isFile()) {
            return;
        }
        ServletContext servletContext = HttpUtil.getRequest().getServletContext();
        //获取文件的MimeType类型
        String mimeType = servletContext.getMimeType(sourceName);
        //设置文件的输出类型
        HttpUtil.getResponse().setContentType(mimeType);
        // 确定文件是内嵌或弹出下载框
        String filename = URLEncoder.encode(destName + ToolUtil.getFileSuffix(sourceName), HttpUtil.ENCODING_GBK);
        HttpUtil.getResponse().addHeader(HttpUtil.CONTENT_DISCRIPTION, HttpUtil.ATTACHMENT_FILENAME_EQ + filename);
        // 通过Response域，创建Servlet的输出流，输出文件
        byte[] buffer = new byte[1024];
        FileInputStream fis = new FileInputStream(sourceFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        OutputStream os = HttpUtil.getResponse().getOutputStream();
        int size = bis.read(buffer);
        while (size != -1) {
            os.write(buffer, 0, size);
            size = bis.read(buffer);
        }
        //关流，response获得流会自动关闭，因此也可以不用手动关
        os.close();
        bis.close();
        fis.close();
        deleteFile(sourceName);
    }

    /**
     * 文件上传（单个），保存格式：uploadPath + /suffix/yyyy-MM-dd-xxxxxxxxxxxxxx.suffix
     */
    public static String uploadFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty())
            return null;
        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = ToolUtil.getFileSuffix(originalFilename);
        String newFilename = ToolUtil.randomID35() + suffix;
        File dest = createFile(uploadPath, newFilename, suffix.replace(".", ""));
        multipartFile.transferTo(dest);
        return dest.getAbsolutePath();
    }


    public static void readFile(String fileFullPath) {
        File file = new File(fileFullPath);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        File wfile = new File("");

        FileOutputStream ois = null;
        byte[] buffer = new byte[2048];
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            ois = new FileOutputStream(wfile);
            while (bis.available() > 0) {
                int size = bis.read(buffer);
                System.out.print(size + ": ");
                System.out.println(buffer);
                ois.write(buffer);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ois != null) ois.close();
                if (bis != null) bis.close();
                if (fis != null) fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
