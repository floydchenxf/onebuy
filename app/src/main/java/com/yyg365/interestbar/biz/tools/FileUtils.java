package com.yyg365.interestbar.biz.tools;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/**
 * Created by floyd on 15-11-29.
 */
public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    private static final String ENTER = "\r\n";

    /**
     * 读取序列化对象
     *
     * @param pathName 文件路径+名字
     * @return
     */
    public static Object readObject(String pathName) {
        InputStream in = FileUtils.readFile(pathName);
        ObjectInputStream ois = null;
        Object obj = null;
        if (in != null) {
            try {
                ois = new ObjectInputStream(in);
                obj = ois.readObject();
            } catch (StreamCorruptedException e) {
                Log.w(TAG, e);
                Log.e("WxException", e.getMessage(), e);
            } catch (IOException e) {
                Log.w(TAG, e);
                Log.e("WxException", e.getMessage(), e);
            } catch (ClassNotFoundException e) {
                Log.w(TAG, e);
                Log.e("WxException", e.getMessage(), e);
            } catch (Exception e) {
                Log.w(TAG, e);
                Log.e("WxException", e.getMessage(), e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                        in = null;
                    } catch (IOException e) {
                        Log.e("WxException", e.getMessage(), e);
                    }
                }
                if (ois != null) {
                    try {
                        ois.close();
                        ois = null;
                    } catch (IOException e) {
                        Log.e("WxException", e.getMessage(), e);
                    }
                }
            }
        }
        return obj;
    }

    /**
     * 将序列化对象写文件
     *
     * @param path 文件路径
     * @param name 文件名字
     * @param obj  序列化的对象
     */
    public static void writeObject(String path, String name, Object obj) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        File _file = new File(path, name);
        if (_file.exists()) {
            _file.delete();
        }
        try {
            fos = new FileOutputStream(_file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            oos.flush();
        } catch (FileNotFoundException e) {
            Log.w(TAG, e);
            Log.e("WxException", e.getMessage(), e);
        } catch (IOException e) {
            Log.w(TAG, e);
            Log.e("WxException", e.getMessage(), e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                    fos = null;
                } catch (IOException e) {
                    Log.e("WxException", e.getMessage(), e);
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                    oos = null;
                } catch (IOException e) {
                    Log.e("WxException", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * @param context
     * @return 应用目录
     */
    public static String getAppPath(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    /**
     * 读取文件
     *
     * @param pathName 文件路径+名字
     * @return 文件流
     */
    public static InputStream readFile(String pathName) {
        File file = new File(pathName);
        if (!file.exists()) {
            File p = file.getParentFile();
            if (!p.exists()) {
                p.mkdir();
            }
        }

        FileInputStream fis = null;
        if (file.exists() && file.isFile()) {
            try {
                fis = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                Log.w(TAG, e);
                Log.e("WxException", e.getMessage(), e);
            }
        }
        return fis;
    }


    /**
     * 拷贝文件目录内容，包含所有子文件
     *
     * @param srcDir
     * @param toDir
     * @return
     */
    public static boolean copyDirectory(File srcDir, File toDir) {
        if (!srcDir.exists() || !srcDir.isDirectory()) {
            return false;
        }

        if (!toDir.exists()) {
            toDir.mkdir();
        }

        File[] subFiles = srcDir.listFiles();
        for (File file : subFiles) {
            String fileName = file.getName();
            File to = new File(toDir.getAbsoluteFile() + File.separator
                    + fileName);
            if (file.isFile()) {
                FileUtils.copyFile(file, to);
            } else {
                copyDirectory(file, to);
            }
        }

        return true;
    }

    /**
     * 拷贝文件
     *
     * @param from 拷贝来源
     * @param to   拷贝去向
     * @return
     */
    private static boolean copyFile(File from, File to) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            to.createNewFile();// 删除清空文件，如果存在的话
            fis = new FileInputStream(from);
            if (fis != null) {
                fos = new FileOutputStream(to);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();
                return true;
            }
        } catch (IOException e) {
            Log.w(TAG, e);
            Log.e("WxException", e.getMessage(), e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    Log.e("WxException", e.getMessage(), e);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e("WxException", e.getMessage(), e);
                }
            }
        }
        return false;
    }

    /**
     * 将二进制数据写文件
     *
     * @param path 文件路径
     * @param name 文件名
     * @param data 二进制流
     */
    public static void writeFile(String path, String name, byte[] data) {
        if (data == null || data.length <= 0) {
            return;
        }
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        File _file = new File(path, name);
        if (_file.exists()) {
            _file.delete();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(_file);
            fos.write(data);
            fos.flush();
        } catch (FileNotFoundException e) {
            Log.w(TAG, e);
            Log.e("WxException", e.getMessage(), e);
        } catch (IOException e) {
            Log.w(TAG, e);
            Log.e("WxException", e.getMessage(), e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e("WxException", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 读取文本文件（UTF-8格式）
     *
     * @param filePath
     * @return
     */
    public static String readTextFile(String filePath) {
        String res = "";
        try {
            FileInputStream fin = new FileInputStream(filePath);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];
            int size = 0;
            while ((size = fin.read(buffer)) != -1) {
                out.write(buffer, 0, size);
            }
            fin.close();
            byte[] content = out.toByteArray();
            res = new String(content, "utf-8");
        } catch (Exception e) {
            Log.w(TAG, e);
            res = "";
        }
        return res;
    }

    public static long getFileSize(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSize(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }

    public static void write(File file, String content) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e("LogWriter", "createNewFile cause error:" + e.getMessage());
                return;
            }
        }

        FileWriter writer = null;
        try {
            writer = new FileWriter(file, true);
            writer.write(content);
            writer.write(ENTER);
        } catch (IOException e) {
            Log.e("LogWriter", "write content cause error:" + e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 删除文件，包含所有子文件
     * @param file
     */
    public static void deleteFile(File file) {
        if (!file.exists()) {
            return;
        }

        boolean isDirectory = file.isDirectory();
        if (!isDirectory) {
            file.delete();
            return;
        }

        File[] subFiles = file.listFiles();
        for (File subFile : subFiles) {
            deleteFile(subFile);
        }

        file.delete();
    }

    public static boolean rename(File file, String name) {
        String target = file.getParentFile().getAbsolutePath();
        File targetFile = new File(target + File.separator + name);
        return file.renameTo(targetFile);
    }


}
