#!/usr/bin/env groovy

import org.jenkins.library.utils.JsonUtils

/**
 * 读取JSON配置文件
 *
 * @param filePath JSON文件路径
 * @param defaultValue 如果文件不存在或读取失败时的默认值
 * @return 解析后的JSON对象
 */
def call(String filePath, def defaultValue = null) {
    def script = this
    
    try {
        script.echo "Reading JSON from file: ${filePath}"
        
        def result
        try {
            def file = new File(filePath)
            if (file.exists()) {
                result = JsonUtils.parseJsonFile(filePath)
            } else {
                result = defaultValue
            }
        } catch (Exception e) {
            result = defaultValue
        }
        
        if (result == defaultValue) {
            script.echo "File not found or invalid: ${filePath}, returning default value"
        }
        
        return result
    } catch (Exception e) {
        script.echo "Error reading JSON file: ${e.message}"
        return defaultValue
    }
}

/**
 * 从字符串读取JSON
 *
 * @param jsonString JSON字符串
 * @param defaultValue 如果解析失败时的默认值
 * @return 解析后的JSON对象
 */
def fromString(String jsonString, def defaultValue = null) {
    def script = this
    
    try {
        def result = JsonUtils.parseJsonString(jsonString, defaultValue)
        
        if (result == defaultValue) {
            script.echo "Failed to parse JSON string, returning default value"
        }
        
        return result
    } catch (Exception e) {
        script.echo "Error parsing JSON string: ${e.message}"
        return defaultValue
    }
}

/**
 * 从URL读取JSON
 * 
 * @param url 要获取JSON的URL
 * @param defaultValue 如果获取失败时的默认值
 * @return 解析后的JSON对象
 */
def fromUrl(String url, def defaultValue = null) {
    def script = this
    
    try {
        script.echo "Fetching JSON from URL: ${url}"
        
        def connection = new URL(url).openConnection()
        connection.requestMethod = 'GET'
        connection.setRequestProperty('Content-Type', 'application/json')
        
        def responseCode = connection.responseCode
        if (responseCode == 200) {
            def jsonString = connection.inputStream.text
            return fromString(jsonString, defaultValue)
        } else {
            script.echo "Failed to fetch JSON from URL: ${url}, HTTP Status: ${responseCode}"
            return defaultValue
        }
    } catch (Exception e) {
        script.echo "Error fetching JSON from URL: ${e.message}"
        return defaultValue
    }
}
