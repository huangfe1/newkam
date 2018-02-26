package com.dreamer.util;

import com.dreamer.domain.user.Agent;
import com.wxjssdk.util.DateUtil;
import ps.mx.otter.exception.ApplicationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Created by huangfei on 03/07/2017.
 */
public class CommonUtil {

    public static Object getFirst(List list) {
        if (list == null || list.size() == 0) return null;
        return list.get(0);
    }

    //创建单号
    public static String createNo() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    //创建单号
    public static String createNoByTime() {
        Date date = new Date();
        String str = DateUtil.formatDate(date, "yyyyMMddHHmmssSSS");
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            str += random.nextInt(10);
        }
//        System.out.println(str.length());
        return str;
    }

    public static void main(String[] args) {
//        System.out.println(createNoByTime());
        String str = createNoByTime() + "_zmz123456";
        System.out.println(str.substring(str.indexOf("_") + 1, str.length()));
        Double v = 9.9;

    }

    //创建文件名
    public static String generateFileName(String imgType) {
        String s = UUID.randomUUID().toString();
        StringBuilder sbd = new StringBuilder();
        sbd.append(s.substring(0, 8)).append(s.substring(9, 13)).append(s.substring(14, 18)).append(s.substring(19, 23)).append(s.substring(24)).append(imgType);
        return sbd.toString();
    }

    //将一个map 加入另外一个map
    public static void putAll(Map<Agent, HashMap<String, Object>> mapAll, Map<Agent, HashMap<String, Object>> mapTemp) {
        for (Agent key : mapTemp.keySet()) {
            HashMap<String, Object> nowSbMap = mapTemp.get(key);
            Double nowTem = (Double) nowSbMap.get("v");
            String nowMore = (String) nowSbMap.get("s");
            Double sum;
            String sm;
            if (mapAll.containsKey(key)) {//包含
                Map<String, Object> sbMap = mapAll.get(key);
                Double tem = (Double) sbMap.get("v");
                String more = (String) sbMap.get("s");
                sum = PreciseComputeUtil.add(tem, nowTem);
                sm = more + nowMore;
            } else {//不包含
                sum = nowTem;
                sm = nowMore;
            }
            nowSbMap.put("v", sum);
            nowSbMap.put("s", sm);
            mapAll.put(key, nowSbMap);
        }
    }


    //将一个map 加入另外一个map
    public static void putAllN(Map<String, HashMap<Agent, Integer>> mapAll, Map<String, HashMap<Agent, Integer>> mapTemp) {
        for (String key : mapTemp.keySet()) {
            HashMap<Agent, Integer> tem = mapTemp.get(key);
            if(mapAll.containsKey(key)){
                HashMap<Agent, Integer> all = mapAll.get(key);
                for (Agent k : tem.keySet()) {
                    Integer nowQ = tem.get(k);
                    if (all.containsKey(k)) {
                        all.put(k, nowQ + all.get(k));
                    } else {
                        all.put(k, nowQ);
                    }
                }
            }else {
                mapAll.put(key,tem);
            }
        }

    }

    //将一个map 加入另外一个map
    public static void putAll(Map<Agent, Double> mapAll, Map<Agent, Double> mapTemp, Boolean isAdd) {
        for (Agent key : mapTemp.keySet()) {
            Double nowTem = mapTemp.get(key);
            Double sum;
            if (mapAll.containsKey(key)) {//包含
                Double tem = mapAll.get(key);
                if (isAdd) {
                    sum = PreciseComputeUtil.add(tem, nowTem);
                } else {
                    sum = PreciseComputeUtil.sub(tem, nowTem);
                }
            } else {//不包含
                if (isAdd) {
                    sum = nowTem;
                } else {
                    sum = -nowTem;
                }
            }
            mapAll.put(key, sum);
        }
    }


    //写入文件
    public static void writeImgFile(byte[] imgBytes, String fileName, String pathStr) {
        try {
            Path path = Paths.get(pathStr);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            Path imgFile = path.resolve(fileName);
            if (!Files.exists(imgFile)) {
                Files.createFile(imgFile);
            }
            Files.write(imgFile, imgBytes, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException iop) {
            throw new ApplicationException(iop);
        }
    }


}
