# 设置开发环境，安装必要的依赖和工具
#!/bin/bash

# 定义颜色
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # 重置颜色

echo -e "${BLUE}设置开发环境${NC}"

# 检查Java是否安装
echo -e "${BLUE}检查Java...${NC}"
if command -v java >/dev/null 2>&1; then
    java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    echo -e "${GREEN}Java已安装: $java_version${NC}"
else
    echo -e "${RED}Java未安装${NC}"
    echo -e "${YELLOW}请安装JDK 8或更高版本${NC}"
fi

# 检查Gradle是否安装
echo -e "${BLUE}检查Gradle...${NC}"
if command -v gradle >/dev/null 2>&1; then
    gradle_version=$(gradle --version | grep Gradle | head -n 1)
    echo -e "${GREEN}Gradle已安装: $gradle_version${NC}"
else
    echo -e "${YELLOW}Gradle未安装，将使用Gradle Wrapper${NC}"
fi

# 更新Gradle Wrapper
echo -e "${BLUE}更新Gradle Wrapper...${NC}"
if [ -f "./gradlew" ]; then
    chmod +x ./gradlew
    ./gradlew wrapper --gradle-version=7.6.1
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}Gradle Wrapper已更新${NC}"
    else
        echo -e "${RED}Gradle Wrapper更新失败${NC}"
    fi
else
    echo -e "${YELLOW}未找到gradlew，尝试创建Gradle Wrapper${NC}"
    if command -v gradle >/dev/null 2>&1; then
        gradle wrapper --gradle-version=7.6.1
        if [ $? -eq 0 ]; then
            echo -e "${GREEN}Gradle Wrapper已创建${NC}"
            chmod +x ./gradlew
        else
            echo -e "${RED}Gradle Wrapper创建失败${NC}"
        fi
    else
        echo -e "${RED}无法创建Gradle Wrapper，请安装Gradle或手动创建Wrapper${NC}"
    fi
fi



# 检查是否有必要的目录
echo -e "${BLUE}检查项目结构...${NC}"
for dir in src vars resources test; do
    if [ ! -d "./$dir" ]; then
        echo -e "${YELLOW}创建目录: $dir${NC}"
        mkdir -p "./$dir"
    fi
done

# 初始化Git Hooks
if [ -d ".git" ]; then
    echo -e "${BLUE}设置Git Hooks...${NC}"
    
    # 创建pre-commit钩子
    PRE_COMMIT=".git/hooks/pre-commit"
    cat > "$PRE_COMMIT" << 'EOF'
#!/bin/bash
echo "运行代码检查..."
./gradlew check -x test

if [ $? -ne 0 ]; then
    echo "代码检查失败，提交终止"
    exit 1
fi

echo "代码检查通过"
exit 0
EOF
    chmod +x "$PRE_COMMIT"
    echo -e "${GREEN}Git Hooks已设置${NC}"
else
    echo -e "${YELLOW}未检测到Git仓库，跳过Git Hooks设置${NC}"
fi

echo -e "${GREEN}开发环境设置完成${NC}"
echo -e "${BLUE}你现在可以使用 './run.sh' 命令运行项目${NC}"
