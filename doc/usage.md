# Jenkins 共享库使用指南

## 配置共享库

在使用共享库之前，您需要在 Jenkins 中配置它：

1. 登录 Jenkins 管理界面
2. 转到 **系统管理** > **系统配置** > **Global Pipeline Libraries**
3. 点击 **Add**
4. 配置以下信息：
   - **名称**: `jenkins-Shared-Library` (在 Pipeline 中引用的名称)
   - **默认版本**: `main` (或指定其他分支/标签)
   - **检索方式**: 选择 Git
   - **项目仓库**: 输入该共享库的 Git 仓库 URL
   - 根据需要配置凭据

## 在 Pipeline 中引入共享库

有几种方式可以在 Jenkins Pipeline 中引入共享库：

### 在 Jenkinsfile 中声明

```groovy
@Library('jenkins-Shared-Library') _

pipeline {
    agent any
    stages {
        stage('Example') {
            steps {
                helloWorld()
            }
        }
    }
}
```

### 导入特定版本

```groovy
@Library('jenkins-Shared-Library@v1.0.0') _
```

### 导入多个库

```groovy
@Library(['jenkins-Shared-Library', 'other-library']) _
```

## 可用功能

### JSON 处理

#### readJsonInfo

读取并解析 JSON 文件或字符串

```groovy
// 读取文件
def config = readJsonInfo('config.json')
echo "项目名称: ${config.projectName}"

// 读取 JSON 字符串
def jsonString = '{"name": "test", "value": 123}'
def data = readJsonInfo(text: jsonString)
echo "名称: ${data.name}"
```

#### writeJsonInfo

将对象写入 JSON 文件

```groovy
def data = [
    name: 'myProject',
    version: '1.0.0',
    dependencies: [
        [name: 'lib1', version: '2.3.4'],
        [name: 'lib2', version: '1.2.3']
    ]
]

writeJsonInfo(file: 'output.json', json: data)
```

### 示例

#### 基本使用示例

```groovy
@Library('jenkins-Shared-Library') _

pipeline {
    agent any
    stages {
        stage('Example') {
            steps {
                script {
                    // 读取配置
                    def config = readJsonInfo('config.json')
                    
                    // 使用配置
                    echo "构建项目: ${config.name} 版本: ${config.version}"
                    
                    // 更新配置
                    config.lastBuild = new Date().format("yyyy-MM-dd HH:mm:ss")
                    writeJsonInfo(file: 'config.json', json: config)
                }
            }
        }
    }
}
```

## 故障排除

### 常见问题

1. **找不到共享库**
   
   检查 Jenkins 中的共享库配置是否正确，URL 是否可访问。

2. **找不到特定函数**
   
   确认函数名称拼写正确，并且该函数确实存在于共享库中。
   
3. **执行权限问题**
   
   确认 Jenkins 有足够的权限执行脚本和访问资源。

### 日志调试

在 Pipeline 中添加以下代码可以增加调试信息：

```groovy
// 启用详细日志
System.setProperty('org.slf4j.simpleLogger.log.org.jenkinsci.plugins.workflow.libs', 'debug')
```

## 最佳实践

1. 始终指定明确的版本标签而不是分支名称
2. 对共享库的调用使用 try-catch 块进行错误处理
3. 避免在共享库中硬编码环境特定的配置
4. 使用参数化使共享库函数可重用
5. 为复杂操作添加适当的日志
