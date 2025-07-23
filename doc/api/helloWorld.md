# helloWorld

一个简单的 Hello World 函数，打印欢迎消息。

## 语法

```groovy
def result = helloWorld()
```

## 参数

此函数不接受任何参数。

## 返回值

字符串 "Hello World"。

## 异常

此函数不会抛出任何异常。

## 示例

### 基本用法

```groovy
def greeting = helloWorld()
echo "收到的问候: ${greeting}"
```

### 在 Pipeline 中使用

```groovy
pipeline {
    agent any
    stages {
        stage('Greet') {
            steps {
                script {
                    helloWorld()
                }
            }
        }
    }
}
```

## 使用场景

这个函数主要用于:

1. 测试共享库的配置是否正确
2. 作为新开发者的入门示例
3. 作为模板创建其他简单函数

## 实现细节

这个函数非常简单，只使用 Jenkins Pipeline 的 `echo` 步骤打印消息，并返回一个固定字符串。
