# Jenkins 共享库开发指南

本文档为共享库的开发者提供指导。如果您想为共享库添加新功能或修复问题，请遵循以下指南。

## 环境设置

### 必要工具

- JDK 8 或更高版本
- Git
- Gradle (可选，项目包含 Gradle Wrapper)
- IDE (推荐 IntelliJ IDEA 或 Visual Studio Code)

### 本地开发环境

1. 克隆仓库
   ```bash
   git clone <repository-url>
   cd jenkins-Shared-Library
   ```

2. 构建项目
   ```bash
   ./gradlew clean build
   ```

3. 运行测试
   ```bash
   ./gradlew test
   ```

## 代码结构

### 添加全局变量脚本

全局变量脚本位于 `vars/` 目录中。每个脚本都会在 Pipeline 中暴露一个同名函数。

要创建一个新的全局变量脚本，请遵循以下模板：

```groovy
#!/usr/bin/env groovy

/**
 * 函数的简短描述
 *
 * 详细描述，解释功能和使用场景
 *
 * @param param1 参数1的描述
 * @param param2 参数2的描述
 * @return 返回值的描述
 *
 * @example
 * // 使用例子
 * myFunction(param1: 'value1', param2: 'value2')
 */
def call(Map params = [:]) {
    // 函数实现
}
```

### 添加类库

类库位于 `src/` 目录中，按照包结构组织。

创建新类时，请遵循以下模板：

```groovy
package org.jenkins.library.utils

/**
 * 类的简短描述
 *
 * 详细描述，解释类的用途和使用场景
 */
class MyClass implements Serializable {
    
    private static final long serialVersionUID = 1L
    
    /**
     * 方法的简短描述
     *
     * @param param1 参数1的描述
     * @return 返回值的描述
     */
    def myMethod(String param1) {
        // 方法实现
    }
}
```

## 测试

我们使用 Spock 框架和 JenkinsPipelineUnit 进行测试。

### 测试全局变量脚本

为 `vars/` 目录中的每个脚本创建一个测试类，放在 `test/vars/` 目录中。

示例：

```groovy
import spock.lang.Specification

class MyFunctionSpec extends JenkinsPipelineSpecification {
    
    def "测试基本功能"() {
        given:
        def capturedOutput = []
        steps.echo = { message -> capturedOutput << message }
        
        when:
        def result = callPipelineMethod("myFunction", [param1: 'value1'])
        
        then:
        result == "预期结果"
        capturedOutput.size() > 0
    }
    
    def callPipelineMethod(String name, Map params = [:]) {
        // 方法实现
    }
}
```

### 测试类库

为 `src/` 目录中的每个类创建一个测试类，放在 `test/src/` 目录中。

示例：

```groovy
import org.jenkins.library.utils.MyClass
import spock.lang.Specification

class MyClassSpec extends Specification {
    
    def "测试myMethod方法"() {
        given:
        def myClass = new MyClass()
        
        when:
        def result = myClass.myMethod("testParam")
        
        then:
        result == "预期结果"
    }
}
```

## 编码规范

1. **命名约定**
   - 类名使用 PascalCase (如 `JsonUtils`)
   - 方法名和变量名使用 camelCase (如 `parseJson`)
   - 常量使用大写下划线 (如 `MAX_RETRY_COUNT`)

2. **代码风格**
   - 使用 4 空格缩进
   - 在操作符周围添加空格
   - 最大行长度为 100 字符
   - 使用单引号定义字符串，除非字符串包含变量插值

3. **文档**
   - 所有公开的方法和类必须有 JavaDoc 风格的注释
   - 包含参数、返回值和异常的文档
   - 提供使用示例

## 调试技巧

1. **本地调试**
   - 使用 `println` 语句输出调试信息
   - 在测试中设置断点

2. **远程调试**
   - 使用 Jenkins 的重放功能测试修改
   - 添加临时日志语句 `echo "DEBUG: ${variable}"`

## 发布流程

1. **版本控制**
   - 主要版本: 不兼容的 API 更改
   - 次要版本: 向后兼容的功能添加
   - 补丁版本: 向后兼容的问题修复

2. **标签和发布**
   - 使用语义化版本对发布进行标记
   - 包含详细的更新日志

3. **发布检查清单**
   - 所有测试通过
   - 文档已更新
   - 代码审查已完成
   - 更新日志已准备好
