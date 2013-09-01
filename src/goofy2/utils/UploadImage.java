package goofy2.utils;

import android.graphics.Bitmap;
import android.util.Log;

import goofy2.swably.Const;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.CancellationException;


public class UploadImage {

    /*
     * 通过拼接的方式构造请求内容，实现参数传输以及文件传输
     * @param actionUrl
     * @param params
     * @param files
     * @return
     * @throws IOException
     */
    public static String post(String actionUrl, Map<String, String> params, Map<String, File> files)
            throws IOException {

        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--", LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";

        URL uri = new URL(actionUrl);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.setReadTimeout(30 * 1000); // 缓存的最长时间
        conn.setDoInput(true);// 允许输入
        conn.setDoOutput(true);// 允许输出
        conn.setUseCaches(false); // 不允许使用缓存
        conn.setRequestMethod("POST");
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Charsert", "UTF-8");
        conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);

        // 首先组拼文本类型的参数
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINEND);
            sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
            sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
            sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
            sb.append(LINEND);
            sb.append(entry.getValue());
            sb.append(LINEND);
        }

        DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
        outStream.write(sb.toString().getBytes());
        // 发送文件数据
        if (files != null)
            for (Map.Entry<String, File> file : files.entrySet()) {
                StringBuilder sb1 = new StringBuilder();
                sb1.append(PREFIX);
                sb1.append(BOUNDARY);
                sb1.append(LINEND);
                sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                        + file.getKey() + "\"" + LINEND);
                sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
                sb1.append(LINEND);
                outStream.write(sb1.toString().getBytes());

                InputStream is = new FileInputStream(file.getValue());
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }

                is.close();
                outStream.write(LINEND.getBytes());
            }

        // 请求结束标志
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        outStream.write(end_data);
        outStream.flush();
        // 得到响应码
        int res = conn.getResponseCode();
        Log.d("resCode", "***********************upload photo ,result code is: " + res);
        InputStream in = null;
        if (res == 200) {
            in = conn.getInputStream();
            int ch;
            StringBuilder sb2 = new StringBuilder();
            while ((ch = in.read()) != -1) {
                sb2.append((char) ch);
            }
        }
        outStream.close();
        conn.disconnect();
        return "hello";
    }

    /**
     * 直接通过HTTP协议提交数据到服务器,实现表单提交功能
     * 
     * @param actionUrl 上传路径
     * @param params 请求参数 key为参数名,value为参数值
     * @param file 上传文件
     */
    public static String post_2(String actionUrl, Map<String, String> params, FormFile[] files) {
        try {
            String BOUNDARY = "---------7d4a6d158c9"; // 数据分隔线
            String MULTIPART_FORM_DATA = "multipart/form-data";
            
            Log.d("url", "****************************************************url is: " + actionUrl);
            URL url = new URL(actionUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(60000);
            conn.setDoInput(true);// 允许输入
            conn.setDoOutput(true);// 允许输出
            conn.setUseCaches(false);// 不使用Cache
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY);

            StringBuilder sb = new StringBuilder();

            // 上传的表单参数部分，格式请参考文章
            for (Map.Entry<String, String> entry : params.entrySet()) {// 构建表单字段内容
                sb.append("--");
                sb.append(BOUNDARY);
                sb.append("\r\n");
                sb.append("Content-Disposition: form-data; name=\"" + entry.getKey()
                                + "\"\r\n\r\n");
                sb.append(entry.getValue());
                sb.append("\r\n");
            }
            DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
            outStream.write(sb.toString().getBytes());// 发送表单字段数据

            // 上传的文件部分，格式请参考文章
            for (FormFile file : files) {
                StringBuilder split = new StringBuilder();
                split.append("--");
                split.append(BOUNDARY);
                split.append("\r\n");
                split.append("Content-Disposition: form-data; name=\"" + file.getFormname()
                        + "\"; filename=\"" + file.getFilname() + "\"\r\n");
                split.append("Content-Type: " + file.getContentType() + "\r\n\r\n");
                outStream.write(split.toString().getBytes());
                outStream.write(file.getData(), 0, file.getData().length);
                outStream.write("\r\n".getBytes());
            }
            byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();// 数据结束标志
            outStream.write(end_data);
            outStream.flush();
            int cah = conn.getResponseCode();
            if (cah != 200)
            {
                Log.d("cha","********************cah is: " + cah);
                throw new RuntimeException("http failed, code: " + cah);
            }
            InputStream is = conn.getInputStream();
            int ch;
            StringBuilder b = new StringBuilder();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            outStream.close();
            conn.disconnect();
            return b.toString();
        } 
        catch (Exception e) 
        {
            throw new RuntimeException(e);
        }
    }

    public static String post_4(String actionUrl, Map<String, String> params, Map<String, File> files)
    throws IOException {
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--", LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";

        File tmpFile = new File(Const.TMP_FOLDER+"/post.tmp");
        // 首先组拼文本类型的参数
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINEND);
            sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
//            sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
//            sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
            sb.append(LINEND);
            sb.append(entry.getValue());
            sb.append(LINEND);
        }

        FileOutputStream outStream = new FileOutputStream(tmpFile);
        outStream.write(sb.toString().getBytes());
        // 发送文件数据
        if (files != null)
            for (Map.Entry<String, File> file : files.entrySet()) {
                StringBuilder sb1 = new StringBuilder();
                sb1.append(PREFIX);
                sb1.append(BOUNDARY);
                sb1.append(LINEND);
                sb1.append("Content-Disposition: form-data; name=\""+file.getKey()+"\"; filename=\""
                        + file.getValue().getName() + "\"" + LINEND);
                sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
                sb1.append(LINEND);
                outStream.write(sb1.toString().getBytes());

                InputStream is = new FileInputStream(file.getValue());
                //byte[] buffer = new byte[1024];
                byte[] buffer = new byte[1024*10];
                int len = 0;
                int sum = 0;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                    //outStream.flush();
                    sum+=buffer.length;
Log.d("", "UploadImage tmp " + sum + " bytes");                    
                }

                is.close();
                outStream.write(LINEND.getBytes());
            }

        // 请求结束标志
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        outStream.write(end_data);
        outStream.flush();
Log.d("", "UploadImage tmped: " + tmpFile.length());                    
        // 得到响应码
		URL uri = new URL(actionUrl);
		URLConnection c = uri.openConnection();
Log.d("","UploadImage connection: "+c.getClass().toString());
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setReadTimeout(30 * 1000); // 缓存的最长时间
		conn.setDoInput(true);// 允许输入
		conn.setDoOutput(true);// 允许输出
		conn.setUseCaches(false); // 不允许使用缓存
		conn.setFixedLengthStreamingMode((int) tmpFile.length());
		conn.setRequestMethod("POST");
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charsert", "UTF-8");
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);

        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
        InputStream is = new FileInputStream(tmpFile);
        //byte[] buffer = new byte[1024];
        byte[] buffer = new byte[1024*10];
        int len = 0;
        int sum = 0;
        while ((len = is.read(buffer)) != -1) {
            dos.write(buffer, 0, len);
            dos.flush();
            sum+=buffer.length;
Log.d("", "UploadImage post " + dos.size() + " bytes");                    
        }
        //dos.flush();
        is.close();

int res = conn.getResponseCode();
Log.d("resCode", "UploadImage result code is: " + res);
        InputStream in = null;
        StringBuilder sb2 = new StringBuilder();
        if (res == 200) {
            in = conn.getInputStream();
            int ch;
            while ((ch = in.read()) != -1) {
                sb2.append((char) ch);
            }
        }
        dos.close();
        conn.disconnect();
Log.d("", "UploadImage got respond: " + sb2.toString());                    
        return sb2.toString();
    }

    //upload big files if server supports chunked encoding, support cancelling
    public static String post_3(String actionUrl, Map<String, String> params, Map<String, File> files, boolean isChunked, int bufferSize, ParamRunnable progressCallback) throws Exception{
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--", LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";

        URL uri = new URL(actionUrl);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.setReadTimeout(300 * 1000); // 缓存的最长时间
        conn.setDoInput(true);// 允许输入
        conn.setDoOutput(true);// 允许输出
        conn.setUseCaches(false); // 不允许使用缓存
        if(isChunked) conn.setChunkedStreamingMode(0);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Charsert", "UTF-8");
        conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);

        // 首先组拼文本类型的参数
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINEND);
            sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
//            sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
//            sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
            sb.append(LINEND);
            sb.append(entry.getValue());
            sb.append(LINEND);
        }

        DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
        outStream.write(sb.toString().getBytes());
        // 发送文件数据
        if (files != null)
            for (Map.Entry<String, File> file : files.entrySet()) {
                StringBuilder sb1 = new StringBuilder();
                sb1.append(PREFIX);
                sb1.append(BOUNDARY);
                sb1.append(LINEND);
                sb1.append("Content-Disposition: form-data; name=\""+file.getKey()+"\"; filename=\""
                        + file.getValue().getName() + "\"" + LINEND);
                sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
                sb1.append(LINEND);
                outStream.write(sb1.toString().getBytes());

                InputStream is = new FileInputStream(file.getValue());
                //byte[] buffer = new byte[1024];
                byte[] buffer = new byte[bufferSize];
                int len = 0;
                int sum = 0;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
Log.v("", "UploadImage post " + outStream.size() + " bytes");
					progressCallback.param = ""+outStream.size(); 
					progressCallback.run();
					if((Boolean) progressCallback.param){
						throw new CancellationException();
					}
                }

                is.close();
                outStream.write(LINEND.getBytes());
            }

        // 请求结束标志
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        outStream.write(end_data);
        outStream.flush();
//Log.v("", "UploadImage posted");                    
		StringBuilder sb2 = new StringBuilder();
        InputStream in = null;
        int res = conn.getResponseCode();
//Log.v("resCode", "UploadImage result code is: " + res);
		if (res == 200) {
	        in = conn.getInputStream();
		}else{
	        in = conn.getErrorStream();
        }
        int ch;
        while ((ch = in.read()) != -1) {
            sb2.append((char) ch);
        }
        //outStream.close();
        conn.disconnect();
		if (res == 200) 
	        return sb2.toString();
		else
			throw new Exception(""+res+": "+sb2.toString());
//Log.d("", "UploadImage got respond: " + sb2.toString());                    
    }

    public void test()
    {
//        File file = new File("/sdcard/gg.jpg");
//        String contentType = "";
//        FileInputStream fis = new FileInputStream(file);
//        byte[] data = fis.
//        FormFile formFile = new FormFile("/sdcard/gg.jpg", data, "file1", contentType);
    }

    /**
     * 文件转化为字节数组
     * 
     * @param file
     * @return
     */
    public static byte[] getBytesFromFile(File file) {
        byte[] ret = null;
        try {
            if (file == null) {
                // log.error("helper:the file is null!");
                return null;
            }
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
            byte[] b = new byte[4096];
            int n;
            while ((n = in.read(b)) != -1) {
                out.write(b, 0, n);
            }
            in.close();
            out.close();
            ret = out.toByteArray();
        } catch (IOException e) {
            // log.error("helper:get bytes from file process error!");
            e.printStackTrace();
        }
        return ret;
    }
    /** *//**
     * 把字节数组保存为一个文件
     * @Author Sean.guo
     * @EditTime 2007-8-13 上午11:45:56
     */
    public static File getFileFromBytes(byte[] b, String outputFile) {
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(outputFile);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null){
                try {
                    stream.close();
                } catch (IOException e1){
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }

    /** *//**
     * 从字节数组获取对象
     * @Author Sean.guo
     * @EditTime 2007-8-13 上午11:46:34
     */
    public static Object getObjectFromBytes(byte[] objBytes) throws Exception{
        if (objBytes == null || objBytes.length == 0){
            return null;
        }
        ByteArrayInputStream bi = new ByteArrayInputStream(objBytes);
        ObjectInputStream oi = new ObjectInputStream(bi);
        return oi.readObject();
   }

    /** *//**
     * 从对象获取一个字节数组
     * @Author Sean.guo
     * @EditTime 2007-8-13 上午11:46:56
     */
    public static byte[] getBytesFromObject(Serializable obj) throws Exception{
        if (obj == null) {
            return null;
        }
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(obj);
        return bo.toByteArray();
    } 
    
    public static void copyFile(File file) 
    {
        byte[] bytes = getBytesFromFile(file);
        
        getFileFromBytes(bytes, "/sdcard/ggcopy.jpeg");
        
        Log.d("copy", "**********************copy suc");
    }

}
