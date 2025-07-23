# writeJsonInfo

将对象数据序列化为 JSON 并写入文件。

## 语法

```groovy
writeJsonInfo(Map args)
```

## 参数

### 参数映射

- `args.file` - (必填) 要写入的目标文件路径
- `args.json` - (必填) 要序列化为 JSON 的对象(Map、List 或其他可序列化对象)
- `args.pretty` - (可选) 布尔值，是否使用美化格式，默认为 `true`
- `args.overwrite` - (可选) 布尔值，如果文件已存在是否覆盖，默认为 `true`

## 返回值

写入的文件路径。

## 异常

- 如果未提供必要参数，则抛出异常
- 如果文件已存在且 `overwrite` 为 `false`，则抛出异常
- 如果写入过程中发生 IO 错误，则抛出 IOException

## 示例

### 基本用法

```groovy
def data = [
    name: 'myProject',
    version: '1.0.0',
    timestamp: new Date().time,
    config: [
        debug: true,
        logLevel: 'INFO',
        maxRetries: 3
    ]
]

writeJsonInfo(file: 'project-info.json', json: data)
```

### 不使用美化格式

```groovy
writeJsonInfo(
    file: 'compact.json', 
    json: [a: 1, b: 2, c: 3], 
    pretty: false
)
```

### 避免覆盖现有文件

```groovy
try {
    writeJsonInfo(
        file: 'important-data.json', 
        json: data, 
        overwrite: false
    )
} catch (Exception e) {
    echo "文件已存在，未覆盖: ${e.message}"
}
```

## 结合 readJsonInfo 使用

读取、修改然后写回 JSON 文件：

```groovy
// 读取现有配置
def config = readJsonInfo('config.json')

// 修改配置
config.lastUpdated = new Date().format("yyyy-MM-dd HH:mm:ss")
config.buildNumber = env.BUILD_NUMBER
config.environment = params.DEPLOY_ENV

// 写回文件
writeJsonInfo(file: 'config.json', json: config)
```

## 实现细节

该函数内部使用 `JsonUtils` 类进行 JSON 序列化。对于大型对象，考虑内存使用情况。
