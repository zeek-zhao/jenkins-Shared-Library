import spock.lang.Specification

/**
 * 基础测试规范类，用于模拟Jenkins管道环境
 */
abstract class JenkinsPipelineSpecification extends Specification {
    // 模拟Jenkins Pipeline环境
    def steps = [:]
    def env = [:]
    def params = [:]
    def currentBuild = [result: 'SUCCESS']
    
    def setup() {
        // 初始化步骤
        steps.echo = { message -> println "ECHO: ${message}" }
        steps.sh = { cmd -> 
            if (cmd instanceof Map) {
                return "执行Shell命令: ${cmd.script}"
            }
            return "执行Shell命令: ${cmd}" 
        }
        steps.error = { message -> throw new Exception(message) }
        steps.git = { config -> return "Git clone: ${config}" }
        
        // 设置一些常用的环境变量
        env.BUILD_NUMBER = '1'
        env.JOB_NAME = 'testJob'
    }
}
