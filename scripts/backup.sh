# 备份项目源代码和构建产物
# 创建日期: $(date +%Y-%m-%d)

#!/bin/bash

# 定义颜色
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # 重置颜色

echo -e "${BLUE}开始备份项目${NC}"

# 获取当前日期和时间作为备份文件名的一部分
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="./backups"
BACKUP_FILE="$BACKUP_DIR/jenkins-shared-library_$TIMESTAMP.zip"

# 确保备份目录存在
if [ ! -d "$BACKUP_DIR" ]; then
    echo -e "${YELLOW}创建备份目录: $BACKUP_DIR${NC}"
    mkdir -p "$BACKUP_DIR"
fi

# 执行备份
echo -e "${BLUE}正在创建备份...${NC}"
zip -r "$BACKUP_FILE" \
    src/ \
    vars/ \
    resources/ \
    docker/ \
    doc/ \
    scripts/ \
    test/ \
    build.gradle \
    gradle.properties \
    settings.gradle \
    README.md \
    -x "*.class" \
    -x "build/" \
    -x ".gradle/" \
    -x "*.bak" \
    -x "*.tmp"

# 检查备份是否成功
if [ $? -eq 0 ]; then
    echo -e "${GREEN}备份成功: $BACKUP_FILE${NC}"
    echo -e "${BLUE}备份文件大小: $(du -h "$BACKUP_FILE" | cut -f1)${NC}"
else
    echo -e "${RED}备份失败${NC}"
    exit 1
fi

# 列出所有备份，只保留最近10个
echo -e "${BLUE}管理备份文件...${NC}"
ls -t "$BACKUP_DIR"/*.zip | tail -n +11 | while read file; do
    echo -e "${YELLOW}删除旧备份: $file${NC}"
    rm "$file"
done

echo -e "${GREEN}备份完成${NC}"
