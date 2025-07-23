# JsonUtils

JSON 处理工具类，提供了读取、解析、转换和保存 JSON 数据的功能。

## 导入

```groovy
import org.jenkins.library.utils.JsonUtils
```

## 静态方法

### parseJson

解析 JSON 字符串为对象。

```groovy
static Object parseJson(String jsonString)
```

**参数:**
- `jsonString` - 要解析的 JSON 字符串

**返回值:**
解析后的对象 (通常是 Map 或 List)

**示例:**
```groovy
def jsonString = '{"name":"test","value":123}'
def data = JsonUtils.parseJson(jsonString)
echo "Name: ${data.name}, Value: ${data.value}"
```

### parseJsonFile

解析 JSON 文件为对象。

```groovy
static Object parseJsonFile(String filePath)
```

**参数:**
- `filePath` - JSON 文件路径

**返回值:**
解析后的对象

**示例:**
```groovy
def config = JsonUtils.parseJsonFile('config.json')
echo "App name: ${config.appName}"
```

### toJsonString

将对象转换为 JSON 字符串。

```groovy
static String toJsonString(Object obj)
```

**参数:**
- `obj` - 要转换的对象

**返回值:**
格式化的 JSON 字符串

**示例:**
```groovy
def data = [name: 'project', version: '1.0.0']
def jsonString = JsonUtils.toJsonString(data)
echo jsonString
// 输出:
// {
//     "name": "project",
//     "version": "1.0.0"
// }
```

### saveToJsonFile

将对象保存为 JSON 文件。

```groovy
static void saveToJsonFile(Object obj, String filePath)
```

**参数:**
- `obj` - 要保存的对象
- `filePath` - 文件保存路径

**示例:**
```groovy
def data = [name: 'project', version: '1.0.0', timestamp: new Date().time]
JsonUtils.saveToJsonFile(data, 'output.json')
```

### updateJsonFile

更新 JSON 文件中的内容。

```groovy
static void updateJsonFile(String filePath, Closure updater)
```

**参数:**
- `filePath` - 文件路径
- `updater` - 更新逻辑闭包，接收解析后的对象作为参数并返回更新后的对象

**示例:**
```groovy
JsonUtils.updateJsonFile('config.json') { config ->
    config.lastUpdated = new Date().time
    config.buildNumber = env.BUILD_NUMBER
    return config
}
```

## 最佳实践

1. **异常处理**

   JSON 处理中的常见错误是格式错误。请始终使用 try-catch 块:

   ```groovy
   try {
       def data = JsonUtils.parseJson(jsonString)
       // 处理数据
   } catch (Exception e) {
       error "JSON 解析失败: ${e.message}"
   }
   ```

2. **大文件处理**

   对于大型 JSON 文件，考虑内存使用:

   ```groovy
   // 处理大型 JSON 文件时，避免在内存中保留多个副本
   JsonUtils.updateJsonFile('large-file.json') { data ->
       data.timestamp = new Date().time
       return data
   }
   ```

3. **路径处理**

   确保文件路径处理正确:

   ```groovy
   def workspace = pwd()
   def configPath = "${workspace}/config.json"
   def data = JsonUtils.parseJsonFile(configPath)
   ```
