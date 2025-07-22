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
}
