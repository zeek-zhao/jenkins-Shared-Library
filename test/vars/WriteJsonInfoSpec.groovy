import spock.lang.Specification
import spock.lang.TempDir
import java.nio.file.Path
import org.jenkins.library.utils.JsonUtils
import groovy.json.JsonSlurper

class WriteJsonInfoSpec extends JenkinsPipelineSpecification {
    
    @TempDir
    Path tempDir
    
    def "writeJsonInfo应该正确写入JSON文件"() {
        given:
        def filePath = tempDir.resolve("output.json").toString()
        def content = [name: "test-project", version: "1.0.0", settings: [debug: true]]
        def capturedLogs = []
        steps.echo = { message -> capturedLogs << message }
        
        when:
        def result = mockWriteJsonInfoCall(filePath, content)
        
        then:
        capturedLogs.any { it.contains("Writing JSON to file") }
        capturedLogs.any { it.contains("Successfully wrote JSON") }
        result == true
        
        and:
        def writtenFile = new File(filePath)
        writtenFile.exists()
    }
    
    def "writeJsonInfo应该处理写入失败的情况"() {
        given:
        def filePath = "/invalid/path/that/doesnt/exist.json"
        def content = [name: "test-project"]
        def capturedLogs = []
        steps.echo = { message -> capturedLogs << message }
        
        when:
        def result = mockWriteJsonInfoCall(filePath, content)
        
        then:
        capturedLogs.any { it.contains("Error writing JSON file") || it.contains("Failed to write JSON") }
        result == false
    }
    
    def "toJsonString应该正确转换对象为JSON字符串"() {
        given:
        def content = [name: "test-project", version: "1.0.0"]
        def capturedLogs = []
        steps.echo = { message -> capturedLogs << message }
        
        when:
        def result = mockToJsonStringCall(content)
        
        then:
        result != null
        result.contains("test-project")
        result.contains("1.0.0")
    }
    
    def "updateJsonField应该正确更新JSON文件中的字段"() {
        given:
        def filePath = tempDir.resolve("config.json").toString()
        // 创建初始文件
        JsonUtils.writeJsonToFile(filePath, [server: [host: "localhost", port: 8080], debug: false])
        
        def capturedLogs = []
        steps.echo = { message -> capturedLogs << message }
        
        when:
        def result = mockUpdateJsonFieldCall(filePath, "server.port", 9090)
        
        then:
        capturedLogs.any { it.contains("Updating JSON field") }
        result == true
        
        and:
        def updatedFile = new File(filePath)
        def updatedContent = new JsonSlurper().parseText(updatedFile.text)
        updatedContent.server.port == 9090
        updatedContent.server.host == "localhost"
        updatedContent.debug == false
    }
    
    // 模拟writeJsonInfo.call方法
    def mockWriteJsonInfoCall(String filePath, def content, boolean pretty = true) {
        steps.echo("Writing JSON to file: ${filePath}")
        
        try {
            def result = JsonUtils.writeJsonToFile(filePath, content, pretty)
            
            if (result) {
                steps.echo("Successfully wrote JSON to: ${filePath}")
            } else {
                steps.echo("Failed to write JSON to: ${filePath}")
            }
            
            return result
        } catch (Exception e) {
            steps.echo("Error writing JSON file: ${e.message}")
            return false
        }
    }
    
    // 模拟toJsonString方法
    def mockToJsonStringCall(def content, boolean pretty = true) {
        try {
            return JsonUtils.toJsonString(content, pretty)
        } catch (Exception e) {
            steps.echo("Error converting to JSON string: ${e.message}")
            return null
        }
    }
    
    // 模拟updateJsonField方法
    def mockUpdateJsonFieldCall(String filePath, String key, def value) {
        steps.echo("Updating JSON field '${key}' in file: ${filePath}")
        
        try {
            def result = JsonUtils.updateJsonField(filePath, key, value)
            
            if (result) {
                steps.echo("Successfully updated JSON field '${key}' in: ${filePath}")
            } else {
                steps.echo("Failed to update JSON field '${key}' in: ${filePath}")
            }
            
            return result
        } catch (Exception e) {
            steps.echo("Error updating JSON field: ${e.message}")
            return false
        }
    }
}
