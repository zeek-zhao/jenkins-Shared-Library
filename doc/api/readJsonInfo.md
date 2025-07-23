# readJsonInfo

读取并解析 JSON 文件或字符串。

## 语法

```groovy
def jsonObject = readJsonInfo(String filePath)
def jsonObject = readJsonInfo(Map args)
```

## 参数

### 文件路径方式

```groovy
readJsonInfo(String filePath)
```

- `filePath` - 要读取的 JSON 文件路径

### 参数映射方式

```groovy
readJsonInfo(Map args)
```

- `args.file` - (可选) 要读取的 JSON 文件路径
- `args.text` - (可选) 要解析的 JSON 字符串
- `args.url` - (可选) 要读取的 JSON URL

**注意**: 必须至少提供 `file`、`text` 或 `url` 中的一个。

## 返回值

解析后的 JSON 对象，通常是一个 Map 或 List。

## 异常

- 如果未提供文件路径、文本或 URL，则抛出异常
- 如果文件不存在，则抛出 FileNotFoundException
- 如果 JSON 格式无效，则抛出 JsonException

## 示例

### 读取 JSON 文件

```groovy
def config = readJsonInfo('config.json')
echo "应用名称: ${config.appName}"
echo "版本: ${config.version}"
```

### 解析 JSON 字符串

```groovy
def jsonString = '''
{
    "name": "myProject",
    "version": "1.0.0",
    "dependencies": [
        {"name": "lib1", "version": "2.3.4"},
        {"name": "lib2", "version": "1.2.3"}
    ]
}
'''

def data = readJsonInfo(text: jsonString)
echo "项目名称: ${data.name}"
echo "依赖数量: ${data.dependencies.size()}"
```

### 从 URL 读取 JSON

```groovy
def apiData = readJsonInfo(url: 'https://api.example.com/data.json')
echo "API 状态: ${apiData.status}"
```

## 实现细节

该函数内部使用 `JsonUtils` 类处理 JSON 数据。对于大型 JSON 文件，考虑使用流式处理以避免内存问题。
