package com.ulab.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class FileUtil {
    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    public static void uploadFile(byte[] file, String filePath, String fileName) throws Exception{

        File targetFile = new File(filePath);
        if(!targetFile.exists()){
            targetFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(filePath+fileName);
        out.write(file);
        out.flush();
        out.close();
    }

    public static String readFile2String(String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);
        int length = inputStream.available();
        byte bytes[] = new byte[length];
        inputStream.read(bytes);
        inputStream.close();
        String str =new String(bytes, StandardCharsets.UTF_8);
        return str ;
    }

    public static void writeInputStreamToFile(InputStream is, String folderPath, String fileName) {
        if (is == null) {
            log.warn("input stream is null, ignore. folderPath=" + folderPath + ", fileName=" + fileName);
            return;
        }
        String filePath = makeEmptyFile(folderPath, fileName);
        InputStream2File(is, filePath);
    }

    private static void InputStream2File(InputStream is, String filePath) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
            int bytesWritten = 0;
            int byteCount = 0;

            byte[] bytes = new byte[1024];

            while ((byteCount = is.read(bytes)) != -1)
            {
//                log.debug("write block, doneBytes=" + bytesWritten + ", writingBytes=" + byteCount);
                os.write(bytes, 0, byteCount);
                bytesWritten += byteCount;
            }
            log.info("write file:" + filePath + " done, filesize=" + bytesWritten);
        } catch (Exception e) {
            log.error("error to write file:" + filePath, e);
        } finally {
            try {
                if (is != null) is.close();
            } catch(Exception e) {
                log.error("error to close input stream", e);
            }
            try {
                if (os != null) os.close();
            } catch(Exception e) {
                log.error("error to close output stream" + filePath, e);
            }
        }
    }

    public static String makeEmptyFile(String folderPath, String fileName) {
        String parentFolder;
        if (folderPath == null || folderPath.trim().isEmpty()) {
            parentFolder = "";
        } else {
            parentFolder = folderPath.trim();
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            parentFolder += File.separator;
        }
        String filePath = parentFolder + fileName;
        File file = new File(filePath);
        if (file.exists()) {
            log.warn("file exist, will be overwriten: " + filePath);
            file.delete();
        }
        try {
			file.createNewFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
        return filePath;
    }
    
    public static void delFilesInFolder(String folderPath) {
        File file = new File(folderPath);
        if (!file.exists()) {
        	throw new RuntimeException("folder does not exist:" + folderPath);
        }
        if (!file.isDirectory()) {
        	throw new RuntimeException("Not a folder:" + folderPath);
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
           if (folderPath.endsWith(File.separator)) {
              temp = new File(folderPath + tempList[i]);
           } else {
               temp = new File(folderPath + File.separator + tempList[i]);
           }
           if (temp.isFile()) {
              temp.delete();
           }
        }
      }

    public static String getMd5(String fileName) throws IOException {
    	try (InputStream is = Files.newInputStream(Paths.get(fileName))) {
    	    String md5 = DigestUtils.md5DigestAsHex(is);
    	    return md5;
    	}
    }
    
    public static void writeString2File(String filePath, String contents) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        } else {
        	File parentFile = file.getParentFile();
        	if (!parentFile.exists()) {
        		parentFile.mkdirs();
        	}
        }
        file.createNewFile();
        FileWriter writer;
        writer = new FileWriter(filePath);
        writer.write(contents);
        writer.flush();
        writer.close();
    }

    public static void writeBytes2File(String filePath, byte[] contents) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        } else {
        	File parentFile = file.getParentFile();
        	if (!parentFile.exists()) {
        		parentFile.mkdirs();
        	}
        }
        file.createNewFile();
        OutputStream out = null;
        try {
	        out = new FileOutputStream(filePath);
	        out.write(contents, 0, contents.length);
	        out.flush();
        } finally {
        	try {
        		if (out != null) out.close();
        	} catch(Exception e) {
        	}
        }
    }

    public static void clearFolder(String folderPath) {
    	File folder = new File(folderPath);
    	if (folder.isFile()) {
    		return;
    	} else {
    		clearFolder(folder);
    	}
    }
    
    public static void clearFolder(File folder) {
    	File[] sub = folder.listFiles();
    	for (File subItem : sub) {
    		if (subItem.isFile()) {
    			subItem.delete();
    		} else {
    			clearFolder(subItem);
    			subItem.delete();
    		}
    	}
    }

    public static void removeFolder(String folderPath) {
    	File folder = new File(folderPath);
    	if (folder.isDirectory()) {
    		clearFolder(folder);
    	}
		folder.delete();
    }

    public static void removeFolder(File folder) {
    	if (folder.isDirectory()) {
    		clearFolder(folder);
    		folder.delete();
    	}
    }
    
    public static void moveFolderItems(String srcFolderPath, String tarFolderPath, String[] items) throws IOException {
    	File srcFolder = new File(srcFolderPath);
    	File tarFolder = new File(tarFolderPath);
    	File[] fileItems = null;
    	if (items == null || items.length == 0) {
    		return;
    	} else {
			fileItems = new File[items.length];
			for (int i=0; i< items.length; i++) {
				String itemPath = srcFolderPath + File.separator + items[i];
				fileItems[i] = new File(itemPath);
			}
    	}
    	moveFiles(srcFolder, tarFolder, fileItems);
    }

    public static void moveFolderItems(String srcFolderPath, String tarFolderPath) throws IOException {
    	File srcFolder = new File(srcFolderPath);
    	File tarFolder = new File(tarFolderPath);
    	File[] fileItems = null;
		fileItems = srcFolder.listFiles();
    	moveFiles(srcFolder, tarFolder, fileItems);
    }

    public static void moveFiles(File srcFolder, File tarFolder, File[] items) throws IOException {
    	if (srcFolder == null) {
    		throw new IllegalArgumentException("target folder is null");
    	}
    	if (tarFolder == null) {
    		throw new IllegalArgumentException("target folder is null");
    	}
    	if (!srcFolder.exists()) {
    		return;
    	}
    	if (!tarFolder.exists()) {
    		tarFolder.mkdirs();
    	}
    	if (!tarFolder.isDirectory()) {
    		throw new IllegalArgumentException("source folder is not Directory:" + tarFolder.getPath());
    	}
    	File[] subItems;
    	if (items == null || items.length == 0) {
    		subItems = srcFolder.listFiles();
    	} else {
    		subItems = items;
    	}
    	for (int i=0; i<subItems.length; i++) {
    		File item = subItems[i];
			File renameItem = new File(tarFolder.getAbsolutePath() + File.separator + item.getName());
    		if (item.isDirectory()) {
    			try {
    				item.renameTo(renameItem);
    			} catch(Exception e) {
    				//if sub-folder can not be moved, move files in it 
        			moveFiles(item, renameItem, null);
    			}
    		} else {
    			try {
    				item.renameTo(renameItem);
    			} catch(Exception e) {
    				Files.copy(item.toPath(), renameItem.toPath());
    			}
    		}
    	}
    }
    
    @SuppressWarnings("resource")
	public static void copyFileUsingChannel(File source, File dest) throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } finally {
        	if (sourceChannel != null) {
        		sourceChannel.close();
			}
            if (destChannel != null) {
                destChannel.close();
            }
        }
    }
    
    public static byte[] readFile2Bytes(String filePath) throws IOException {
        File file = new File(filePath);
        return readFile2Bytes(file);
    }

    public static byte[] readFile2Bytes(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        int length = inputStream.available();
        byte bytes[] = new byte[length];
        inputStream.read(bytes);
        inputStream.close();
        return bytes;
    }

    public static ByteBuf readFile2ByteBuf(String filePath) throws IOException {
        File file = new File(filePath);
        return readFile2ByteBuf(file);
    }

    public static ByteBuf readFile2ByteBuf(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        int length = inputStream.available();
        byte bytes[] = new byte[length];
        inputStream.read(bytes);
        inputStream.close();
        return Unpooled.wrappedBuffer(bytes);
    }
}