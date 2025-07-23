#!/bin/bash
set -e

# 项目根目录
PROJECT_ROOT=$(dirname "$(readlink -f "$0")") && export PROJECT_ROOT
SCRIPTS_DIR="$PROJECT_ROOT/scripts" && export SCRIPTS_DIR

# 定义颜色
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # 重置颜色


# 清屏函数
clear_screen() {
    clear
}

# 获取脚本描述
get_script_description() {
    local script="$1"
    if [ -f "$script" ]; then
        # 尝试从脚本的第一行注释中提取描述
        description=$(head -n 3 "$script" | grep "^#" | sed -e 's/^#[[:space:]]*//' | head -n 1)
        echo "$description"
    else
        echo "未找到脚本描述"
    fi
}

# 列出所有脚本
list_scripts() {
    clear_screen
    echo -e "${BLUE}============================================${NC}"
    echo -e "${BLUE}              可用脚本列表${NC}"
    echo -e "${BLUE}============================================${NC}"
    echo ""
    
    if [ -d "$SCRIPTS_DIR" ]; then
        # 检查目录是否为空
        if [ -z "$(ls -A $SCRIPTS_DIR 2>/dev/null)" ]; then
            echo -e "${YELLOW}没有找到可用脚本。使用 '创建新脚本' 选项创建脚本。${NC}"
            return
        fi
        
        # 列出所有脚本及其描述
        for script in "$SCRIPTS_DIR"/*.sh; do
            if [ -f "$script" ]; then
                script_name=$(basename "$script" .sh)
                description=$(get_script_description "$script")
                
                echo -e "${GREEN}$script_name${NC}"
                if [ -n "$description" ]; then
                    echo -e "  ${CYAN}描述:${NC} $description"
                fi
                
                # 显示最后修改时间
                last_modified=$(date -r "$script" "+%Y-%m-%d %H:%M:%S")
                echo -e "  ${CYAN}最后修改:${NC} $last_modified"
                
                # 显示文件大小
                size=$(du -h "$script" | cut -f1)
                echo -e "  ${CYAN}大小:${NC} $size"
                
                echo ""
            fi
        done
    else
        echo -e "${RED}错误: 脚本目录不存在${NC}"
    fi
}

docker_scripts() {
    cmd="$1"
    source "$SCRIPTS_DIR/docker.sh"
    case "$cmd" in
        build)
            docker_build
            ;;
        start)
            docker_start_env
            ;;
        *)
            echo -e "${RED}未知 Docker 命令: $cmd${NC}"
            echo "使用 'docker help' 查看可用命令"
            exit 1
            ;;
    esac
}
backup_scripts() {
    "$SCRIPTS_DIR/backup.sh"
}
init_gradle_scripts() {
    "$SCRIPTS_DIR/init-gradle.sh"
}
setup_dev_scripts() {
    "$SCRIPTS_DIR/setup-dev.sh"
}
interactive_mode(){
    echo -e "${BLUE}============================================${NC}"
    echo -e "${BLUE}              交互式脚本管理${NC}"
    echo -e "${BLUE}============================================${NC}"
}
# 命令行模式
command_line_mode() {
    echo -e "${BLUE}============================================${NC}"
    echo -e "${BLUE}              命令行脚本管理${NC}"
    echo -e "${BLUE}============================================${NC}"
    case "$1" in
        list)
            list_scripts
            ;;
        docker)
            docker_scripts "$2"
            ;;
        backup)
            backup_scripts
            ;;
        init-gradle)
            init_gradle_scripts
            ;;
        setup-dev)
            setup_dev_scripts
            ;;
        *)
            echo -e "${RED}未知命令: $1${NC}"
            echo "使用 '$0 help' 查看可用命令"
            exit 1
            ;;
    esac
}

# 主函数
main() {
    # 如果没有参数，进入交互式模式
    if [ $# -eq 0 ]; then
        interactive_mode
    else
        # 否则，使用命令行模式
        command_line_mode "$@"
    fi
}

# 执行主函数
main "$@"
