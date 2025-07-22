#!/usr/bin/env groovy

import org.jenkins.library.utils.JsonUtils

/**
 * 将对象写入JSON文件
 *
 * @param filePath 目标文件路径
 * @param content 要写入的对象/数据
 * @param pretty 是否格式化JSON (默认为true)
 * @return 成功返回true，失败返回false
 */
def call(String filePath, def content, boolean pretty = true) {
    def script = this
    
    try {
        script.echo "Writing JSON to file: ${filePath}"
        
        def result = JsonUtils.writeJsonToFile(filePath, content, pretty)
        
        if (result) {
            script.echo "Successfully wrote JSON to: ${filePath}"
        } else {
            script.echo "Failed to write JSON to: ${filePath}"
        }
        
        return result
    } catch (Exception e) {
        script.echo "Error writing JSON file: ${e.message}"
        return false
    }
}

/**
 * 将对象转换为JSON字符串
 *
 * @param content 要转换的对象/数据
 * @param pretty 是否格式化JSON (默认为true)
 * @return JSON字符串
 */
def toJsonString(def content, boolean pretty = true) {
    try {
        return JsonUtils.toJsonString(content, pretty)
    } catch (Exception e) {
        echo "Error converting to JSON string: ${e.message}"
        return null
    }
}

/**
 * 将JSON对象中的某个字段更新并写回文件
 *
 * @param filePath JSON文件路径
 * @param key 要更新的键名或路径(如"server.host")
 * @param value 新值
 * @return 成功返回true，失败返回false
 */
def updateJsonField(String filePath, String key, def value) {
    def script = this
    
    try {
        script.echo "Updating JSON field '${key}' in file: ${filePath}"
        
        def result = JsonUtils.updateJsonField(filePath, key, value)
        
        if (result) {
            script.echo "Successfully updated JSON field '${key}' in: ${filePath}"
        } else {
            script.echo "Failed to update JSON field '${key}' in: ${filePath}"
        }
        
        return result
    } catch (Exception e) {
        script.echo "Error updating JSON field: ${e.message}"
        return false
    }
}
