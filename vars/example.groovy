#!/usr/bin/env groovy

/**
 * 示例管道脚本
 *
 * @param args 参数映射
 * @return 结果
 */
def call(Map args = [:]) {
    // 获取参数，如果没有提供则使用默认值
    def param1 = args.param1 ?: 'default'
    
    // 打印消息
    echo "Running example script with param1=${param1}"
    
    // 执行一些操作
    def result = sh(script: "echo 'This is an example'", returnStdout: true).trim()
    
    return "Example completed: ${result}"
}
