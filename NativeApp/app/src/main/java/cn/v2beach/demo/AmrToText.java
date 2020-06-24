//package cn.v2beach.demo;
//
//import com.huawei.sis.bean.SisConfig;
//import com.huawei.sis.bean.SisConstant;
//import com.huawei.sis.bean.request.AsrCustomShortRequest;
//import com.huawei.sis.bean.response.AsrCustomShortResponse;
//import com.huawei.sis.bean.AuthInfo;
//import com.huawei.sis.client.AsrCustomizationClient;
//import com.huawei.sis.exception.SisException;
//import com.huawei.sis.util.IOUtils;
//import java.util.List;
//
//
///**
// * 定制语音识别
// *
// * Copyright 2020 Huawei Technologies Co.,Ltd.
// */
//public class AmrToText {
//    private static final int SLEEP_TIME = 500;
//    private static final int MAX_POLLING_NUMS = 1000;
//
//    private String ak = "4UK6AWSKAEXXMU0FY8HT";
//    private String sk = "l5UJ5TL2Z8ri3xtMQZVDcfeYoE4F6XStXCQDqfwK";
//    private String region = "cn-north-4";    // 区域，如cn-north-1、cn-north-4终端节点sis-ext.cn-north-4.myhuaweicloud.com
//    private String projectId = "08b82b361e80f2db2fc7c01818dcb293"; // 项目id，在我的凭证查看。
//    // 一句话识别参数
//    private String path = "";             // 音频文件路径，如D:/test.wav等，sdk会将音频文件转化为base64编码
//    private String pathAudioFormat = "amr";  // 文件格式，如wav等，支持格式详见api文档
//    private String pathProperty = "chinese_8k_common";     // 属性字符串，language_sampleRate_domain, 如chinese_8k_common, 详见api文档
//
//    public AmrToText(String path){
//        this.path = path;
//    }
//
//    /**
//     * 设置一句话识别参数，所有参数均有默认值，不配置也可使用
//     *
//     * @param request 一句话识别请求
//     */
//    private void setShortParameter(AsrCustomShortRequest request) {
//
//        // 设置是否添加标点，默认是no
//        request.setAddPunc("yes");
//    }
//
//    /**
//     * 定义config，所有参数可选，设置超时时间等。
//     *
//     * @return SisConfig
//     */
//    private SisConfig getConfig() {
//        SisConfig config = new SisConfig();
//        // 设置连接超时，默认10000ms
//        config.setConnectionTimeout(SisConstant.DEFAULT_CONNECTION_TIMEOUT);
//        // 设置请求超时，默认10000ms
//        config.setRequestTimeout(SisConstant.DEFAULT_CONNECTION_REQUEST_TIMEOUT);
//        // 设置socket超时，默认10000ms
//        config.setSocketTimeout(SisConstant.DEFAULT_SOCKET_TIMEOUT);
//        // 设置代理, 一定要确保代理可用才启动此设置。 代理初始化也可用不加密的代理，new ProxyHostInfo(host, port);
//        // ProxyHostInfo proxy = new ProxyHostInfo(host, port, username, password);
//        // config.setProxy(proxy);
//        return config;
//    }
//
//    /**
//     * 打印一句话语音识别结果
//     *
//     * @param response 一句话识别响应
//     */
//    private void printAsrShortResponse(AsrCustomShortResponse response) {
//        System.out.println("traceId=" + response.getTraceId());
//        System.out.println("text=" + response.getText());
//        System.out.println("score=" + response.getScore());
//        System.out.println("\n");
//    }
//
//    /**
//     * 一句话识别demo
//     */
//    public String DEMO() {
//        try {
//            // 1. 初始化AsrCustomizationClient
//            // 定义authInfo，根据ak，sk，region，projectId
//            AuthInfo authInfo = new AuthInfo(ak, sk, region, projectId);
//            // 设置config，主要与超时有关
//            SisConfig config = getConfig();
//            // 根据authInfo和config，构造AsrCustomizationClient
//            AsrCustomizationClient asr = new AsrCustomizationClient(authInfo, config);
//
//            // 2. 配置请求
//            String data = IOUtils.getEncodeDataByPath(path);
//            AsrCustomShortRequest request = new AsrCustomShortRequest(data, pathAudioFormat, pathProperty);
//            // 设置请求参数，所有参数均为可选
//            setShortParameter(request);
//
//            // 3. 发送请求，获取响应
//            AsrCustomShortResponse response = asr.getAsrShortResponse(request);
//            // 打印结果
//            return response.getText();
//
//        } catch (SisException e) {
//            e.printStackTrace();
//            System.out.println("error_code:" + e.getErrorCode() + "\nerror_msg:" + e.getErrorMsg());
//        }
//        return null;
//    }
//}
//
////    AsrCustomizationDemo demo = new AsrCustomizationDemo();
////    demo.DEMO();