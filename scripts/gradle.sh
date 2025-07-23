#!/bin/bash

# 定义颜色
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # 重置颜色

# 检查gradlew是否存在
if [ ! -f "./gradlew" ]; then
    echo -e "${RED}错误: gradlew文件不存在${NC}"
    echo "请确保您在正确的目录中，或者运行 'gradle wrapper' 生成wrapper文件"
    exit 1
fi

# 确保gradlew有执行权限
if [ ! -x "./gradlew" ]; then
    echo -e "${YELLOW}添加gradlew执行权限...${NC}"
    chmod +x ./gradlew
fi

# 显示帮助信息
show_gradle_help() {
    echo -e "${BLUE}Jenkins共享库 Gradle命令助手${NC}"
    echo "用法: $0 <命令> [选项]"
    echo ""
    echo "常用命令:"
    echo "  test             运行测试"
    echo "  build            构建项目"
    echo "  clean            清理项目"
    echo "  check            运行代码检查"
    echo "  dependencies     显示依赖关系"
    echo "  wrapper          更新Gradle Wrapper"
    echo "  tasks            列出所有可用任务"
    echo "  javadoc          生成Java文档"
    echo "  testReport       生成测试报告"
    echo "  publish          发布项目"
    echo ""
    echo "选项:"
    echo "  --info           显示详细信息"
    echo "  --debug          显示调试信息"
    echo "  --stacktrace     显示完整堆栈跟踪"
    echo ""
    echo "例如:"
    echo "  $0 test          # 运行所有测试"
    echo "  $0 build --info  # 构建项目并显示详细信息"
}

# 运行测试并打开测试报告
run_test_with_report() {
    echo -e "${BLUE}运行测试并生成报告...${NC}"
    ./gradlew test "$@"
    
    # 检查测试是否成功
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}测试成功完成${NC}"
    else
        echo -e "${RED}测试失败${NC}"
    fi
    
    # 检查测试报告是否存在并尝试打开
    local report_path="build/reports/tests/test/index.html"
    if [ -f "$report_path" ]; then
        echo -e "${BLUE}测试报告已生成: $report_path${NC}"
        
        # 根据操作系统打开报告
        if [ "$(uname)" == "Darwin" ]; then  # macOS
            open "$report_path"
        elif [ "$(uname)" == "Linux" ]; then  # Linux
            if [ -n "$(command -v xdg-open)" ]; then
                xdg-open "$report_path"
            else
                echo -e "${YELLOW}无法自动打开报告，请手动打开: $report_path${NC}"
            fi
        elif [[ "$(uname)" == CYGWIN* || "$(uname)" == MINGW* ]]; then  # Windows
            start "$report_path"
        else
            echo -e "${YELLOW}无法自动打开报告，请手动打开: $report_path${NC}"
        fi
    else
        echo -e "${YELLOW}找不到测试报告${NC}"
    fi
}

# 运行构建并分析结果
run_build() {
    echo -e "${BLUE}构建项目...${NC}"
    ./gradlew build "$@"
    
    # 检查构建结果
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}构建成功完成${NC}"
        
        # 检查生成的构建文件
        local build_dir="build/libs"
        if [ -d "$build_dir" ]; then
            echo -e "${BLUE}构建产物:${NC}"
            ls -lh "$build_dir"
        fi
    else
        echo -e "${RED}构建失败${NC}"
    fi
}

# 清理项目
run_clean() {
    echo -e "${BLUE}清理项目...${NC}"
    ./gradlew clean "$@"
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}清理完成${NC}"
    else
        echo -e "${RED}清理失败${NC}"
    fi
}

# 检查代码
run_check() {
    echo -e "${BLUE}运行代码检查...${NC}"
    ./gradlew check "$@"
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}代码检查通过${NC}"
    else
        echo -e "${RED}代码检查失败${NC}"
        
        # 检查检查报告是否存在
        local reports=(
            "build/reports/checkstyle/main.html"
            "build/reports/findbugs/main.html"
            "build/reports/pmd/main.html"
        )
        
        for report in "${reports[@]}"; do
            if [ -f "$report" ]; then
                echo -e "${YELLOW}查看报告: $report${NC}"
            fi
        done
    fi
}

# 显示依赖关系
show_dependencies() {
    echo -e "${BLUE}显示项目依赖关系...${NC}"
    ./gradlew dependencies "$@"
}

# 更新Gradle Wrapper
update_wrapper() {
    echo -e "${BLUE}更新Gradle Wrapper...${NC}"
    ./gradlew wrapper "$@"
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}Wrapper更新完成${NC}"
        # 显示版本信息
        ./gradlew --version | head -n 1
    else
        echo -e "${RED}Wrapper更新失败${NC}"
    fi
}

# 列出可用任务
list_tasks() {
    echo -e "${BLUE}列出可用Gradle任务...${NC}"
    ./gradlew tasks "$@"
}

# 生成Java文档
generate_javadoc() {
    echo -e "${BLUE}生成Java文档...${NC}"
    ./gradlew javadoc "$@"
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}文档生成完成${NC}"
        local doc_dir="build/docs/javadoc"
        if [ -d "$doc_dir" ]; then
            echo -e "${BLUE}文档位置: $doc_dir/index.html${NC}"
        fi
    else
        echo -e "${RED}文档生成失败${NC}"
    fi
}

# 发布项目
publish_project() {
    echo -e "${BLUE}发布项目...${NC}"
    ./gradlew publish "$@"
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}项目发布成功${NC}"
    else
        echo -e "${RED}项目发布失败${NC}"
    fi
}

# 主函数
main() {
    case "$1" in
        test)
            run_test_with_report "${@:2}"
            ;;
        build)
            run_build "${@:2}"
            ;;
        clean)
            run_clean "${@:2}"
            ;;
        check)
            run_check "${@:2}"
            ;;
        dependencies)
            show_dependencies "${@:2}"
            ;;
        wrapper)
            update_wrapper "${@:2}"
            ;;
        tasks)
            list_tasks "${@:2}"
            ;;
        javadoc)
            generate_javadoc "${@:2}"
            ;;
        publish)
            publish_project "${@:2}"
            ;;
        help|--help|-h)
            show_gradle_help
            ;;
        *)
            if [ -z "$1" ]; then
                show_gradle_help
            else
                echo -e "${BLUE}执行自定义Gradle命令: $1${NC}"
                ./gradlew "$@"
            fi
            ;;
    esac
}

# 执行主函数
main "$@"
