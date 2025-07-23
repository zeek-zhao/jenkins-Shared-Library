package org.jenkins.library.utils

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

/**
 * JSON工具类，提供JSON相关的操作方法
 */
class JsonUtils implements Serializable {
    
    private static final long serialVersionUID = 1L
    
    /**
     * 解析JSON字符串为对象
     * 
     * @param jsonString 要解析的JSON字符串
     * @return 解析后的对象
     */
    static Object parseJson(String jsonString) {
        def jsonSlurper = new JsonSlurper()
        return jsonSlurper.parseText(jsonString)
    }
    
    /**
     * 解析JSON字符串为对象，提供默认值
     *
     * @param jsonString 要解析的JSON字符串
     * @param defaultValue 解析失败时的默认值
     * @return 解析后的对象或默认值
     */
    static Object parseJsonString(String jsonString, def defaultValue = null) {
        try {
            return parseJson(jsonString)
        } catch (Exception e) {
            return defaultValue
        }
    }
    
    /**
     * 解析JSON文件为对象
     * 
     * @param filePath JSON文件路径
     * @return 解析后的对象
     */
    static Object parseJsonFile(String filePath) {
        def jsonSlurper = new JsonSlurper()
        def fileContent = new File(filePath).text
        return jsonSlurper.parseText(fileContent)
    }
    
    /**
     * 解析JSON文件为对象，提供默认值
     *
     * @param filePath JSON文件路径
     * @param defaultValue 文件不存在或解析失败时的默认值
     * @return 解析后的对象或默认值
     */
    static Object parseJsonFileWithDefault(String filePath, def defaultValue) {
        try {
            def file = new File(filePath)
            if (!file.exists()) {
                return defaultValue
            }
            return parseJsonFile(filePath)
        } catch (Exception e) {
            return defaultValue
        }
    }
    
    /**
     * 将对象转换为JSON字符串
     * 
     * @param obj 要转换的对象
     * @return JSON字符串
     */
    static String toJsonString(Object obj) {
        def jsonBuilder = new JsonBuilder(obj)
        return jsonBuilder.toPrettyString()
    }
    
    /**
     * 将对象转换为JSON字符串
     *
     * @param obj 要转换的对象
     * @param pretty 是否格式化输出
     * @return JSON字符串
     */
    static String toJsonString(Object obj, boolean pretty) {
        def jsonBuilder = new JsonBuilder(obj)
        return pretty ? jsonBuilder.toPrettyString() : jsonBuilder.toString()
    }
    
    /**
     * 将对象保存为JSON文件
     * 
     * @param obj 要保存的对象
     * @param filePath 文件保存路径
     */
    static void saveToJsonFile(Object obj, String filePath) {
        def jsonContent = toJsonString(obj)
        new File(filePath).write(jsonContent)
    }
    
    /**
     * 将对象写入JSON文件
     *
     * @param filePath 目标文件路径
     * @param obj 要写入的对象
     * @param pretty 是否格式化JSON
     * @return 成功返回true，失败返回false
     */
    static boolean writeJsonToFile(String filePath, Object obj, boolean pretty = true) {
        try {
            def jsonContent = toJsonString(obj, pretty)
            new File(filePath).write(jsonContent)
            return true
        } catch (Exception e) {
            return false
        }
    }
    
    /**
     * 更新JSON文件中的内容
     * 
     * @param filePath 文件路径
     * @param updater 更新逻辑闭包，接收解析后的对象作为参数并返回更新后的对象
     */
    static void updateJsonFile(String filePath, Closure updater) {
        def file = new File(filePath)
        if (file.exists()) {
            def jsonObj = parseJsonFile(filePath)
            def updatedObj = updater(jsonObj)
            saveToJsonFile(updatedObj, filePath)
        }
    }
    
    /**
     * 更新JSON文件中的特定字段
     *
     * @param filePath 文件路径
     * @param key 字段路径，例如 "server.host"
     * @param value 新值
     * @return 成功返回true，失败返回false
     */
    static boolean updateJsonField(String filePath, String key, def value) {
        try {
            def file = new File(filePath)
            if (!file.exists()) {
                return false
            }
            
            def jsonObj = parseJsonFile(filePath)
            
            // 处理嵌套路径
            if (key.contains(".")) {
                def parts = key.split("\\.")
                def current = jsonObj
                
                // 遍历路径除最后一部分
                for (int i = 0; i < parts.length - 1; i++) {
                    if (current[parts[i]] == null) {
                        current[parts[i]] = [:]
                    }
                    current = current[parts[i]]
                }
                
                // 设置最后一部分的值
                current[parts[-1]] = value
            } else {
                // 直接字段
                jsonObj[key] = value
            }
            
            saveToJsonFile(jsonObj, filePath)
            return true
        } catch (Exception e) {
            return false
        }
    }
}
