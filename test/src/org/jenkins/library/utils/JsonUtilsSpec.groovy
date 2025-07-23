package org.jenkins.library.utils

import spock.lang.Specification
import spock.lang.TempDir
import java.nio.file.Path
import groovy.json.JsonSlurper

class JsonUtilsSpec extends Specification {
    
    @TempDir
    Path tempDir
    
    def "parseJson应该正确解析JSON字符串"() {
        given:
        def jsonString = '{"name":"test","value":123}'
        
        when:
        def result = JsonUtils.parseJson(jsonString)
        
        then:
        result instanceof Map
        result.name == "test"
        result.value == 123
    }
    
    def "parseJsonFile应该正确读取和解析JSON文件"() {
        given:
        def jsonFile = tempDir.resolve("test.json").toFile()
        jsonFile.text = '{"name":"test","version":"1.0.0"}'
        
        when:
        def result = JsonUtils.parseJsonFile(jsonFile.path)
        
        then:
        result instanceof Map
        result.name == "test"
        result.version == "1.0.0"
    }
    
    def "parseJsonFile应该在文件不存在时返回默认值"() {
        given:
        def nonExistentFile = tempDir.resolve("non-existent.json").toString()
        def defaultValue = [error: "file not found"]
        
        when:
        // 使用正确的方法名 parseJsonFileWithDefault 而不是 parseJsonFile
        def result = JsonUtils.parseJsonFileWithDefault(nonExistentFile, defaultValue)
        
        then:
        result == defaultValue
    }
    
    def "toJsonString应该正确序列化对象为JSON"() {
        given:
        def data = [name: "project", version: "1.0.0", numbers: [1, 2, 3]]
        
        when:
        def jsonString = JsonUtils.toJsonString(data)
        def parsedBack = new JsonSlurper().parseText(jsonString)
        
        then:
        jsonString.contains("project")
        jsonString.contains("1.0.0")
        parsedBack.name == "project"
        parsedBack.version == "1.0.0"
        parsedBack.numbers == [1, 2, 3]
    }
    
    def "saveToJsonFile应该将对象保存为JSON文件"() {
        given:
        def data = [name: "project", version: "1.0.0"]
        def outputFile = tempDir.resolve("output.json").toFile()
        
        when:
        JsonUtils.saveToJsonFile(data, outputFile.path)
        def content = outputFile.text
        def parsedBack = new JsonSlurper().parseText(content)
        
        then:
        outputFile.exists()
        content.contains("project")
        content.contains("1.0.0")
        parsedBack.name == "project"
        parsedBack.version == "1.0.0"
    }
    
    def "updateJsonFile应该正确更新JSON文件的内容"() {
        given:
        def initialData = [name: "project", version: "1.0.0"]
        def jsonFile = tempDir.resolve("config.json").toFile()
        JsonUtils.saveToJsonFile(initialData, jsonFile.path)
        
        when:
        JsonUtils.updateJsonFile(jsonFile.path) { config ->
            config.version = "1.1.0"
            config.updated = true
            return config
        }
        def updatedData = JsonUtils.parseJsonFile(jsonFile.path)
        
        then:
        updatedData.name == "project"
        updatedData.version == "1.1.0"
        updatedData.updated == true
    }
    
    def "updateJsonField应该正确更新JSON文件中的特定字段"() {
        given:
        def initialData = [name: "project", config: [host: "localhost", port: 8080]]
        def jsonFile = tempDir.resolve("server-config.json").toFile()
        JsonUtils.saveToJsonFile(initialData, jsonFile.path)
        
        when:
        def result = JsonUtils.updateJsonField(jsonFile.path, "config.port", 9090)
        def updatedData = JsonUtils.parseJsonFile(jsonFile.path)
        
        then:
        result == true
        updatedData.config.port == 9090
        updatedData.name == "project"
        updatedData.config.host == "localhost"
    }
}
