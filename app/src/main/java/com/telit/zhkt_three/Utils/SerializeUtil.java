package com.telit.zhkt_three.Utils;

import java.io.*;

/**
 * 序列化工具类
 */
public class SerializeUtil {

    /**
     * @param object 要序列化的对象
     */
    public static byte[] toSerialize(Object object) {

        if (object == null) {
            throw new IllegalArgumentException("序列化对象为空");
        }

        if (!(object instanceof Serializable)) {
            throw new IllegalArgumentException(SerializeUtil.class.getSimpleName() + " requires a Serializable payload " +
                    "but received an object of type [" + object.getClass().getName() + "]");
        }

        byte[] result = null;

        try {
            ByteArrayOutputStream byteArrayOutputStream = null;
            ObjectOutputStream objectOutputStream = null;
            try {
                byteArrayOutputStream = new ByteArrayOutputStream();
                objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(object);
                result = byteArrayOutputStream.toByteArray();
            } catch (IOException e) {
                throw new Exception("failed to serialize", e);
            } finally {
                if (byteArrayOutputStream != null) {
                    objectOutputStream.close();
                    byteArrayOutputStream.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * @param object        要序列化的对象
     * @param savedFilePath 将要序列化的对象保存到的本地文件地址
     */
    public static boolean toSerializeToFile(Object object, String savedFilePath) {
        if (savedFilePath == null || "".equals(savedFilePath)) {
            throw new IllegalArgumentException("写入序列化对象的文件路径不能为空");
        }

        if (object == null) {
            throw new IllegalArgumentException("序列化对象为空");
        }

        if (!(object instanceof Serializable)) {
            throw new IllegalArgumentException(SerializeUtil.class.getSimpleName() + " requires a Serializable payload " +
                    "but received an object of type [" + object.getClass().getName() + "]");
        }

        try {
            File file = new File(savedFilePath);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    throw new Exception("创建保存序列化对象的文件失败", (Throwable) e);
                }
            }
            FileOutputStream fileOutputStream = null;
            ByteArrayOutputStream byteArrayOutputStream = null;
            ObjectOutputStream objectOutputStream = null;

            try {
                fileOutputStream = new FileOutputStream(file);
                byteArrayOutputStream = new ByteArrayOutputStream();
                objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(object);
                byte[] result = byteArrayOutputStream.toByteArray();
                fileOutputStream.write(result);

                return true;
            } catch (IOException e) {
                throw new Exception("failed to serialize object to file", e);
            } finally {
                if (fileOutputStream != null) {
                    objectOutputStream.close();
                    byteArrayOutputStream.close();
                    fileOutputStream.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param bytes 要序列化的字节数组
     */
    public static Object deSerialize(byte[] bytes) {

        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("反序列化的字节数组为空");
        }

        Object object = null;

        try {
            ByteArrayInputStream byteArrayInputStream = null;
            ObjectInputStream objectInputStream = null;

            try {
                byteArrayInputStream = new ByteArrayInputStream(bytes);
                objectInputStream = new ObjectInputStream(byteArrayInputStream);
                object = objectInputStream.readObject();
            } catch (IOException e) {
                throw new Exception("failed to deSerialize", e);
            } catch (ClassNotFoundException e) {
                throw new Exception("failed to deSerialize to Object type", e);
            } finally {
                if (byteArrayInputStream != null) {
                    objectInputStream.close();
                    byteArrayInputStream.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }


    /**
     * @param filePath 要反序列化的文件路径
     */
    public static Object deSerializeFromFile(String filePath) {

        if (filePath == null || "".equals(filePath)) {
            throw new IllegalArgumentException("文件路径名[filePath]不存在");
        }

        Object object = null;

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new Exception("反序列化的文件路径不存在");
            }
            FileInputStream fileInputStream = null;
            ObjectInputStream objectInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                objectInputStream = new ObjectInputStream(fileInputStream);
                object = objectInputStream.readObject();
            } catch (IOException e) {
                throw new Exception("failed to deSerialize", e);
            } catch (ClassNotFoundException e) {
                throw new Exception("failed to deSerialize to Object type", e);
            } finally {
                if (fileInputStream != null) {
                    objectInputStream.close();
                    fileInputStream.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

}
