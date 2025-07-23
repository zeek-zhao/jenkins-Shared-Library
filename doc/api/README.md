# API 参考文档

本文档提供了 Jenkins 共享库中所有可用 API 的详细参考。

## 全局变量 (vars)

这些是可以直接在 Pipeline 中调用的全局函数：

| 函数名 | 描述 | 文档链接 |
|--------|------|----------|
| `readJsonInfo` | 读取并解析 JSON 数据 | [查看文档](readJsonInfo.md) |
| `writeJsonInfo` | 将数据写入 JSON 文件 | [查看文档](writeJsonInfo.md) |
| `example` | 示例函数 | [查看文档](example.md) |
| `helloWorld` | 简单的 Hello World 函数 | [查看文档](helloWorld.md) |

## 实用工具 (src)

这些是可以在全局变量脚本中导入和使用的类：

| 类名 | 描述 | 文档链接 |
|------|------|----------|
| `JsonUtils` | JSON 处理工具类 | [查看文档](JsonUtils.md) |

## 资源文件

这些是可以在 Pipeline 中使用 `libraryResource` 步骤加载的资源文件：

| 文件名 | 描述 | 文档链接 |
|--------|------|----------|
| (暂无可用资源文件) | | |
