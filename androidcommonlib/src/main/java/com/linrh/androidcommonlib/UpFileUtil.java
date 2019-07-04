package com.linrh.androidcommonlib;

import com.linrh.easyhttp.EasyHttp;
import com.linrh.easyhttp.JSONUtils;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 作者：created by @author{ John } on 2018/12/21 0021上午 8:53
 * 描述：上传文件到服务器，并发送邮件告知下载链接。
 *      本服务依赖于服务器，不能保证服务器可用，或者地址未改变。
 * 修改备注：
 */

public class UpFileUtil {

    public static String UPLOAD_URL = "https://file-post.net/zc/p0/bin/callback.cgi";
    public static String UPLOAD_URL2 = "https://file-post.net/zc/p0/bin/upload.cgi";

    public static String MAIL_TO_NAME = "linrh";
    public static String MAIL_TO_ADDR = "15920421522@126.com";

    public static String MAIL_FROM_NAME = "send";
    public static String MAIL_FROM_ADDR = "15920421522@126.com";



    /**
     * 下载回调
     */
    public interface Callback
    {
        void onResponse(String downloadLink);
        void onProgress(int per);
    }

    /**
     * 上传文件至服务器
     * @param file        文件。支持文件或者是文件夹。
     * @param callback    回调，返回下载地址。null代表上传失败。
     */
    public static void uploadFile(File file, Callback callback){
        if (!file.exists()){
            System.out.print("file is not exist !");
            if (callback!=null){
                callback.onResponse(null);
            }
        }else{
            try{
                upload(file,callback);
            }catch (Exception e){
                e.printStackTrace();
                callback.onResponse(null);
            }
        }
    }



    /**
     * {"pid": "a533818306","isWin8": "0","max_upload_files": "15","max_upload_size": "500","mode": "setting"}
     */
    private static class SettingBean {
        /**
         *
         * pid : a782422341
         * isWin8 : 0
         * max_upload_files : 15
         * max_upload_size : 500
         * mode : setting
         */
        private String pid;
        private String isWin8;
        private String max_upload_files;
        private String max_upload_size;
        private String mode;

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getIsWin8() {
            return isWin8;
        }

        public void setIsWin8(String isWin8) {
            this.isWin8 = isWin8;
        }

        public String getMax_upload_files() {
            return max_upload_files;
        }

        public void setMax_upload_files(String max_upload_files) {
            this.max_upload_files = max_upload_files;
        }

        public String getMax_upload_size() {
            return max_upload_size;
        }

        public void setMax_upload_size(String max_upload_size) {
            this.max_upload_size = max_upload_size;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }
    }



    private static class SendMailResponse {
        /**
         * pid : a929120574
         * mode : stop finish!
         * upload_time : 1545292914
         * finishPage : %3C%21%2D%2D%20complete%20uploading%2D%2D%3E%0A%3Cbr%3E%0A%3Ch2%20%20class%3D%22h2t%22%3E%E4%B8%8A%E4%BC%A0%E5%AE%8C%E6%88%90%E5%90%8E%E3%80%82%3C%2Fh2%3E%0A%0A%3C%21%2D%2D%20link%20to%20download%20page%20%2D%2D%3E%0A%3Cdiv%20id%3D%22sendInfo%22%3E%0A%09%3Cbr%3E%0A%09%3Ccenter%3E%0A%09%3Cp%3E%E6%82%A8%E5%8F%AF%E4%BB%A5%E8%AE%BF%E9%97%AE%E4%B8%8B%E8%BD%BD%E7%BD%91%E5%9D%80%E3%80%82%3Cbr%3E%28%E6%82%A8%E5%8F%AF%E4%BB%A5%E6%B7%BB%E5%8A%A0%2F%E5%88%A0%E9%99%A4%E6%96%87%E4%BB%B6%E5%A6%82%E6%9E%9C%E6%82%A8%E8%AE%BF%E9%97%AE%E7%9A%84%E7%BD%91%E5%9D%80%E3%80%82%29%3C%2Fp%3E%0A%09%3Cp%3E%3Cspan%20class%3D%22urlLink%22%20%3E%3Ca%20href%3D%22https%3A%2F%2Ffile%2Dpost%2Enet%2Fzc%2Ffp9%2Fd1%2F1545292914_205894105_120%2F%3Fid%3Du5fxeGsZzhBf%22%20%20target%3D%22_blank%22%3E%3Cimg%20src%3D%22%2E%2Fimage%2Fimg%2Darrow%2Epng%22%20alt%3D%22%3E%3E%22%20name%3D%22%3E%3E%22%20width%3D%2220%22%20height%3D%2220%22%3E%20%E8%BF%9B%E5%85%A5%E4%B8%8B%E8%BD%BD%E7%BD%91%E5%9D%80%3C%2Fa%3E%3C%2Fspan%3E%3C%2Fp%3E%0A%09%3C%2Fcenter%3E%0A%3C%2Fdiv%3E%0A%3Cbr%3E%0A%0A%3C%21%2D%2D%20mail%20sending%20massage%20%2D%2D%3E%0A%3Ch3%20class%3D%22h2t%22%20style%3D%22border%2Dbottom%3A%200px%3B%22%3E%E7%94%B5%E5%AD%90%E9%82%AE%E4%BB%B6%E5%B7%B2%E7%BB%8F%E5%8F%91%E9%80%81%E5%88%B0%E6%94%B6%E4%BB%B6%E4%BA%BA%E3%80%82%3C%2Fh3%3E%0A%3Ccenter%3E%0A%3Ctable%20class%3D%22border%20upTable%22%20style%3D%22width%3A75%25%3B%22%3E%0A%3Ctr%3E%3Cth%3E%E6%94%B6%E4%BB%B6%E4%BA%BA%3C%2Fth%3E%3Ctd%3Elinrh%20%2815920421522%40126%2Ecom%29%3C%2Ftd%3E%3C%2Ftr%3E%0A%3Ctr%3E%3Cth%3E%E4%BD%A0%20%28CC%29%3C%2Fth%3E%3Ctd%3Ebroad%20%2815920421522%40126%2Ecom%29%3C%2Ftd%3E%3C%2Ftr%3E%0A%3C%2Ftable%3E%3Cbr%3E%0A%3C%2Fcenter%3E%0A%0A%3C%21%2D%2D%20finish%20massage%20%2D%2D%3E%0A%3Cbr%3E%0A%3Cp%20style%3D%22text%2Dalign%3A%20center%3B%22%3E%E6%84%9F%E8%B0%A2%E6%82%A8%E8%AE%BF%E9%97%AE%E6%88%91%E4%BB%AC%E7%9A%84%E7%BD%91%E7%AB%99%E3%80%82%3C%2Fp%3E%0A%0A%3Cdiv%20style%3D%22width%3A100%25%3B%2Dwebkit%2Doverflow%2Dscrolling%3Atouch%3Bborder%3Asolid%201px%20%23dcdcdc%3B%22%3E%0A%3Ciframe%20height%3D%22600%22%20name%3D%22sample%22%20src%3D%22https%3A%2F%2Ffile%2Dpost%2Enet%2Fzc%2Ffp9%2Fd1%2F1545292914_205894105_120%2F%3Fid%3Du5fxeGsZzhBf%22%20style%3D%22transform%3Ascale%280%2E5%29%3B%2Dmoz%2Dtransform%3Ascale%280%2E5%29%3B%2Dwebkit%2Dtransform%3Ascale%280%2E5%29%3B%2Do%2Dtransform%3Ascale%280%2E5%29%3B%2Dms%2Dtransform%3Ascale%280%2E5%29%3Btransform%2Dorigin%3A0%200%3B%2Dmoz%2Dtransform%2Dorigin%3A0%200%3B%2Dwebkit%2Dtransform%2Dorigin%3A0%200%3B%2Do%2Dtransform%2Dorigin%3A0%200%3B%2Dms%2Dtransform%2Dorigin%3A0%200%3Bborder%3Asolid%201px%20%23dcdcdc%3Bmargin%2Dbottom%3A%2D300px%3Bmargin%2Dright%3A%2D100%25%3Bwidth%3A200%25%3B%22%3EUSER%20PAGE%3C%2Fiframe%3E%0A%3C%2Fdiv%3E%0A%0A
         * finishAdd :
         */

        private String pid;
        private String mode;
        private String upload_time;
        private String finishPage;
        private String finishAdd;

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getUpload_time() {
            return upload_time;
        }

        public void setUpload_time(String upload_time) {
            this.upload_time = upload_time;
        }

        public String getFinishPage() {
            return finishPage;
        }

        public void setFinishPage(String finishPage) {
            this.finishPage = finishPage;
        }

        public String getFinishAdd() {
            return finishAdd;
        }

        public void setFinishAdd(String finishAdd) {
            this.finishAdd = finishAdd;
        }
    }

    private static class UploadResponse {
        //成功返回：
        //{"files": [{"pid": "a937989882","name":"data.mdb","size":45056,"upstatus":"completed","end":"end"}]}
        //失败返回：
        //{"files": [{"pid": "a233079803","error":1,"upstatus":"error","message":"14: Invalid Process ID: Please reload this page...","end":"end"}], "error":1}
        //未上传文件：
        //{"files": [{"pid":"a256415811","error":1,"upstatus":"error","message":"08: No files uploaded","end":"end"}], "error":1}

        private int error;
        private List<FilesBean> files;

        public int getError() {
            return error;
        }

        public void setError(int error) {
            this.error = error;
        }

        public List<FilesBean> getFiles() {
            return files;
        }

        public void setFiles(List<FilesBean> files) {
            this.files = files;
        }

        public static class FilesBean {
            /**
             * pid : a256415811
             * error : 1
             * upstatus : error
             * message : 08: No files uploaded
             * end : end
             */

            private String pid;
            private int error;
            private String upstatus;
            private String message;
            private String end;

            private String name;
            private String size;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getSize() {
                return size;
            }

            public void setSize(String size) {
                this.size = size;
            }

            public String getPid() {
                return pid;
            }

            public void setPid(String pid) {
                this.pid = pid;
            }

            public int getError() {
                return error;
            }

            public void setError(int error) {
                this.error = error;
            }

            public String getUpstatus() {
                return upstatus;
            }

            public void setUpstatus(String upstatus) {
                this.upstatus = upstatus;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public String getEnd() {
                return end;
            }

            public void setEnd(String end) {
                this.end = end;
            }
        }
    }






    private static void upload(final File file , final Callback callback){

        Observable.create(new ObservableOnSubscribe<SettingBean>() {

            @Override
            public void subscribe(final ObservableEmitter<SettingBean> emitter) throws Exception {

                Map<String, String> params = new HashMap<>();
                Map<String, File[]> files = new HashMap<>();
                params.put("id","");
                params.put("mode", "setting");
                params.put("type","entry");

                /**
                 * 获取上传的ID
                 */

                String string = EasyHttp.httpReq(UPLOAD_URL,
                        params,
                        null, "post", false, false,
                        null,null,null);
                if (string!=null){
                    System.out.println(string);
                    SettingBean settingBean = JSONUtils.jsonStringToBeanUseGson(string, SettingBean.class);
                    emitter.onNext(settingBean);
                }
            }
        }).subscribeOn(Schedulers.io()).map(new Function<SettingBean, UploadResponse>() {
            @Override
            public UploadResponse apply(SettingBean settingBean) throws Exception {
                String pid = settingBean.getPid();

                Map<String, File[]> files = new HashMap<>();
                File[] f = new File[1];
                f[0] = file;
                files.put("files",f);

                Map<String, String> params = new HashMap<>();
                params.put("sid",pid);
                params.put("name_from", MAIL_FROM_NAME);
                params.put("mail_from",MAIL_FROM_ADDR);
                params.put("name_to",MAIL_TO_NAME);
                params.put("keisho","1");
                params.put("mail_to",MAIL_TO_ADDR);
                params.put("abKei","1");
                params.put("mail_sub",f[0].getName());
                params.put("message", f[0].getName());
                params.put("dlpass","");
                params.put("passInMail","yes");
                params.put("expiry_date","720");
                params.put("mail_by","mail_by_filepost2");
                params.put("tZone","8");
                params.put("QV","m");

                final long[] maxsize = {0};
                /**
                 * 上传文件
                 */
                String string = EasyHttp.httpReq(UPLOAD_URL2 + "?sid=" + pid,
                        params,
                        files, "post", false, false, null, null,  new EasyHttp.ProgressListener() {
                            @Override
                            public void onStart(long size) {
                                maxsize[0] = size;
                            }

                            @Override
                            public void onProgress(long index) {
                                if (callback!=null&&maxsize[0]>0){
                                    callback.onProgress((int) (index*100/maxsize[0]));
                                }
                            }

                            @Override
                            public void onFailure(Throwable throwable) {

                            }

                            @Override
                            public void onDone(File file) {

                            }

                        }
                );
                if (string!=null){
                    System.out.println(string);
                    UploadResponse uploadResponse = JSONUtils.jsonStringToBeanUseGson(string, UploadResponse.class);
                    return uploadResponse;
                }else{
                    return new UploadResponse();
                }

            }
        }).map(new Function<UploadResponse, SendMailResponse>() {
            @Override
            public SendMailResponse apply(UploadResponse uploadResponse) throws Exception {
                if (uploadResponse.getFiles()==null||uploadResponse.getFiles().size()<1){
                    return new SendMailResponse();
                }
                UploadResponse.FilesBean filesBean = uploadResponse.getFiles().get(0);

                if (filesBean.getUpstatus().equals("completed")){

                    String pid = filesBean.getPid();

                    Map<String, String> params = new HashMap<>();
                    params.put("sid",pid);
                    params.put("name_from", MAIL_FROM_NAME);
                    params.put("mail_from",MAIL_FROM_ADDR);
                    params.put("name_to",MAIL_TO_NAME);
                    params.put("keisho","1");
                    params.put("mail_to",MAIL_TO_ADDR);
                    params.put("abKei","1");
                    params.put("mail_sub",filesBean.getName());
                    params.put("message",filesBean.getName());
                    params.put("dlpass","");
                    params.put("passInMail","yes");
                    params.put("expiry_date","720");
                    params.put("mail_by","mail_by_filepost2");
                    params.put("tZone","8");
                    params.put("QV","m");
                    params.put("mode","stop");
                    params.put("fileStatus",URLEncoder.encode("<->"+filesBean.getName()+"<>"+filesBean.getSize(),"UTF8"));

                    Map<String, String> header = new HashMap<>();
                    header.put("Content-Type","application/x-www-form-urlencoded");

                    /**
                     * 发送邮件
                     */
                    String string = EasyHttp.httpReq(UPLOAD_URL,
                            params, null, "post", false, false,
                            null, header, null);

                    if (string!=null){
                        System.out.println(string);

                        SendMailResponse sendMailResponse = JSONUtils.jsonStringToBeanUseGson(string,SendMailResponse.class);

                        return sendMailResponse;
                    }


                }else{
                    String error = filesBean.getMessage();
                    System.out.println(error);

                }
                return new SendMailResponse();
            }
        }).subscribe(new Consumer<SendMailResponse>() {
            @Override
            public void accept(SendMailResponse sendMailResponse) throws Exception {

                /**
                 * 获取下载链接
                 */
                if (sendMailResponse.getPid()!=null){

                    String htmlpage = sendMailResponse.getFinishPage();
                    String url = htmlpage.substring(htmlpage.indexOf("https"),htmlpage.length());
                    url = url.substring(0,url.indexOf("%22"));
                    System.out.println(url);


                    String keyWord = URLDecoder.decode(url, "UTF8");
                    System.out.println(keyWord);

                    callback.onResponse(keyWord);
                }else{
                    callback.onResponse(null);
                }
            }
        });
    }





}
