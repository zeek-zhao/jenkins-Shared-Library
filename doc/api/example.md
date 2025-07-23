# example

示例函数，演示如何在 Jenkins Pipeline 中使用共享库函数。

## 语法

```groovy
def result = example()
def result = example(Map args)
```

## 参数

### 无参数调用

```groovy
example()
```

使用默认设置执行示例函数。

### 参数映射调用

```groovy
example(Map args)
```

- `args.param1` - (可选) 参数1，默认为 'default'
- `args.debug` - (可选) 是否启用调试模式，默认为 `false`

## 返回值

字符串，包含执行结果信息。

## 异常

此函数不会抛出特定异常，但会在遇到错误时通过 Jenkins 管道记录错误。

## 示例

### 基本用法

```groovy
def result = example()
echo "示例结果: ${result}"
```

### 使用自定义参数

```groovy
def result = example(
    param1: 'customValue',
    debug: true
)
echo "自定义示例结果: ${result}"
```

## 使用场景

这个函数主要用于演示目的，可以作为开发新共享库函数的模板。它展示了:

1. 如何处理参数
2. 如何执行 shell 命令
3. 如何返回结果

## 实现细节

函数内部执行一个简单的 shell 命令并返回其输出。这是为了演示如何在共享库函数中集成系统命令。
