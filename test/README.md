# Jenkins Shared Library 测试

本目录包含了针对Jenkins共享库的单元测试，使用Groovy和Spock框架实现。

## 测试结构

- `JenkinsPipelineSpecification.groovy`：基础测试规范类，提供Jenkins管道环境的模拟
- `vars/`：包含针对`vars`目录下各个脚本的测试

## 如何运行测试

使用Gradle运行测试：

```bash
./gradlew test
```

## 编写新测试

为`vars`目录下的每个脚本创建一个对应的测试类：

1. 在`test/vars`目录下创建一个名为`[ScriptName]Spec.groovy`的文件
2. 扩展`JenkinsPipelineSpecification`基类
3. 实现`loadScript()`方法，调用`loadVarsScript('[scriptName]')`
4. 添加测试方法，使用Spock框架的given/when/then结构
