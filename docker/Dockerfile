FROM ubuntu:22.04

# 设置非交互式安装
ENV DEBIAN_FRONTEND=noninteractive

# 安装必要的依赖
RUN apt-get update && apt-get install -y --no-install-recommends \
    git \
    curl \
    gosu \
    ssh \
    openjdk-11-jdk \
    unzip \
    wget \
    ca-certificates \
    sudo openssh-server \
    && rm -rf /var/lib/apt/lists/*

# 安装 Gradle
ENV GRADLE_VERSION=7.4
ENV GRADLE_HOME=/opt/gradle
RUN set -o errexit -o nounset \
    && echo "Downloading Gradle" \
    && wget --no-verbose --output-document=gradle.zip "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" \
    \
    && echo "Installing Gradle" \
    && unzip -q gradle.zip \
    && rm gradle.zip \
    && mv "gradle-${GRADLE_VERSION}" "${GRADLE_HOME}" \
    \
    && echo "Adding gradle to PATH" \
    && ln -s "${GRADLE_HOME}/bin/gradle" /usr/bin/gradle \
    \
    && echo "Verifying Gradle installation" \
    && gradle --version



# 设置环境变量
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false -Dorg.gradle.parallel=true"
ENV JAVA_OPTS="-Xmx512m"

RUN ( set -ex \
    && mkdir -p /run/sshd \
   && printf '#!/bin/bash -e\n\
                                                                           \n\
log_file=/var/log/boot_log.log                                             \n\
                                                                           \n\
function create_user() {                                                   \n\
    user_group="$1"                                                        \n\
    user_gid="$2"                                                          \n\
    user_account="$3"                                                      \n\
    user_uid="$4"                                                          \n\
    user_home="$5"                                                         \n\
    user_password="$6"                                                     \n\
                                                                           \n\
    if [ -z "${user_account}" ]                                            \n\
    then                                                                   \n\
        echo "(${BASH_SOURCE[0]}: line $LINENO)Error: user_account is empty!" >> ${log_file} \n\
        return 0                                                           \n\
    fi                                                                     \n\
                                                                           \n\
    if [ -z "${user_uid}" ]                                                \n\
    then                                                                   \n\
        echo "(${BASH_SOURCE[0]}: line $LINENO)Error: user_uid is empty!" >> ${log_file} \n\
        return 0                                                           \n\
    fi                                                                     \n\
                                                                           \n\
    if [ -z "${user_password}" ]                                           \n\
    then                                                                   \n\
        echo "(${BASH_SOURCE[0]}: line $LINENO)Error: user_password is empty!" >> ${log_file} \n\
        return 0                                                           \n\
    fi                                                                     \n\
                                                                           \n\
    if [ -z "${user_group}" ]                                              \n\
    then                                                                   \n\
        user_group="${user_account}"                                       \n\
    fi                                                                     \n\
                                                                           \n\
    if [ -z "${user_gid}" ]                                                \n\
    then                                                                   \n\
        user_gid="${user_uid}"                                             \n\
    fi                                                                     \n\
# BEGIN: prepare user group                                                \n\
    if grep -Eq "^${user_group}:" /etc/group                               \n\
    then                                                                   \n\
        if ! grep -E "^${user_group}:x:${user_gid}:" /etc/group            \n\
        then                                                               \n\
            groupmod -g "${user_gid}" "${user_group}"                      \n\
        fi                                                                 \n\
    elif grep -Eq "^[^:]+:[^:]+:${user_gid}:" /etc/group                   \n\
    then                                                                   \n\
        old_group="$(grep -E "^[^:]+:[^:]+:${user_gid}:" /etc/group | grep -Eo "^[^:]+")" \n\
        test -n "${old_group}"                                             \n\
        groupmod -n "${user_group}" "${old_group}"                         \n\
    else                                                                   \n\
        groupadd -g "${user_gid}" "${user_group}"                          \n\
    fi                                                                     \n\
# END: prepare user group                                                  \n\
                                                                           \n\
# BEGIN: Set useradd options                                               \n\
    useradd_options=" -MN"                                                 \n\
    if [ -n "${user_home}" ]                                               \n\
    then                                                                   \n\
        useradd_options="${useradd_options} -d ${user_home}"               \n\
    fi                                                                     \n\
    useradd_options="${useradd_options} -s /bin/bash -G sudo"              \n\
    useradd_options="${useradd_options} -g ${user_group} -u ${user_uid} "  \n\
# END: Set useradd options                                                 \n\
                                                                           \n\
    useradd ${useradd_options} -c "docker user" ${user_account}            \n\
    echo "adding user ${user_account} with options [${useradd_options} -c \"docker user\"] successfully!" >> ${log_file} \n\
    echo "${user_account}:${user_password}" | chpasswd                     \n\
    echo "setting password for ${user_account} successfully!" >> ${log_file} \n\
}                                                                          \n\
                                                                           \n\
function review_user() {                                                   \n\
    group_info="$1"                                                        \n\
    user_info="$2"                                                         \n\
    user_password="$3"                                                     \n\
                                                                           \n\
    user_account="$(echo "${user_info}" | awk -F: '\''{print $1}'\'')"     \n\
    user_uid="$(echo "${user_info}"     | awk -F: '\''{print $3}'\'')"     \n\
    user_gid="$(echo "${user_info}"     | awk -F: '\''{print $4}'\'')"     \n\
    user_home="$(echo "${user_info}"    | awk -F: '\''{print $6}'\'')"     \n\
                                                                           \n\
    user_account=${user_account// /}                                       \n\
    user_uid=${user_uid// /}                                               \n\
    user_gid=${user_gid// /}                                               \n\
    user_home=${user_home// /}                                             \n\
                                                                           \n\
    if [ -z "${user_account}" ]                                            \n\
    then                                                                   \n\
        return 0                                                           \n\
    fi                                                                     \n\
                                                                           \n\
    if grep -q ^${user_account}: /etc/passwd                               \n\
    then                                                                   \n\
        recreate_user=false                                                \n\
                                                                           \n\
        if [ -n "${user_uid}" ]                                            \n\
        then                                                               \n\
            if [ "$(grep ^${user_account}: /etc/passwd | awk -F: '\''{print $3}'\'')" != "${user_uid}" ] \n\
            then                                                           \n\
                recreate_user=true                                         \n\
            fi                                                             \n\
        fi                                                                 \n\
                                                                           \n\
        if [ -n "${user_gid}" ]                                            \n\
        then                                                               \n\
            if [ "$(grep ^${user_account}: /etc/passwd | awk -F: '\''{print $4}'\'')" != "${user_gid}" ] \n\
            then                                                           \n\
                recreate_user=true                                         \n\
            fi                                                             \n\
        fi                                                                 \n\
                                                                           \n\
        if [ -n "${user_home}" ]                                           \n\
        then                                                               \n\
            if [ "$(grep ^${user_account}: /etc/passwd | awk -F: '\''{print $6}'\'')" != "${user_home}" ] \n\
            then                                                           \n\
                recreate_user=true                                         \n\
            fi                                                             \n\
        fi                                                                 \n\
                                                                           \n\
        if ${recreate_user}                                                \n\
        then                                                               \n\
            userdel ${user_account}                                        \n\
        fi                                                                 \n\
    elif awk -F: '\''{print $3}'\'' /etc/passwd | grep -q "^${user_uid}$"  \n\
    then                                                                   \n\
        for exist_user in $(awk -F: '\''{print $1,$3}'\'' /etc/passwd | grep " ${user_uid}$" | awk '\''{print $1}'\'') \n\
        do                                                                 \n\
            userdel ${exist_user}                                          \n\
        done                                                               \n\
    fi                                                                     \n\
                                                                           \n\
    if ! grep -q ^${user_account}: /etc/passwd                             \n\
    then                                                                   \n\
        user_group="$(echo "${group_info}" | awk -F'\''[=()]'\'' '\''{print $3}'\'')" \n\
        create_user "${user_group}" "${user_gid}" "${user_account}" "${user_uid}" "${user_home}" "${user_password}" \n\
    fi                                                                     \n\
}                                                                          \n\
                                                                           \n\
                                                                           \n\
if [ -z "${USER_PASSWORD// /}" ]                                           \n\
then                                                                       \n\
    USER_PASSWORD=123                                                      \n\
fi                                                                         \n\
                                                                           \n\
review_user "${GROUP_INFO}" "${USER_INFO}" "${USER_PASSWORD}"              \n\
$@                                                                         \n\
' >  /usr/sbin/run.sh \
   && sed -i 's/[ \t]*$//g' /usr/sbin/run.sh \
   && chown root:root /usr/sbin/run.sh \
   && chmod a=rx      /usr/sbin/run.sh \
    )

EXPOSE 22/tcp
ENTRYPOINT ["/usr/sbin/run.sh"]
CMD ["/usr/sbin/sshd", "-D"]
