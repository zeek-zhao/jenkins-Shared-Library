# 编译 启动 jenkins 三方库docker 环境

#!/bin/bash
set -e

DOCKER_IMAGE=${DOCKER_IMAGE:-jenkins-shared-lib-test} && export DOCKER_IMAGE
DOCKER_PROT=${DOCKER_PROT:-10555} && export DOCKER_PROT
DOCKER_CONTAINER_NAME=${DOCKER_CONTAINER_NAME:-jenkins} && export DOCKER_CONTAINER_NAME

function docker_build {
    docker build -t $DOCKER_IMAGE -f "$PROJECT_ROOT/docker/Dockerfile" "$PROJECT_ROOT"
}


function docker_start_env {
    docker run -itd \
        -h "jenkins" \
        -e GROUP_INFO="$(id | awk '{print $2}')" \
        -e USER_INFO="$(cat /etc/passwd | grep ^${USER}:)" \
        --restart always \
        --name "$DOCKER_CONTAINER_NAME" \
        -p "$DOCKER_PROT":22 \
        -v "${HOME}:${HOME}" \
        -w "$PROJECT_ROOT" \
        "$DOCKER_IMAGE"
    echo "You can switch to ${DOCKER_CONTAINER_NAME} root user with the following command:"
    echo "ssh -p ${DOCKER_PROT} root@localhost"
    echo "Please log in to ${DOCKER_CONTAINER_NAME} with passwd:123:"
    echo "ssh -p ${DOCKER_PROT} ${USER}@localhost"
    echo ""
}

#if [ "$1" == "build" ]; then
#    docker_build
#elif [ "$1" == "start" ]; then
#    docker_start_env
#else
#    echo "Usage: $0 {build|start}"
#    exit 1
#fi
