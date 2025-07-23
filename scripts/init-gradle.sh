# 该脚本用于初始化Gradle项目并创建Gradle Wrapper
#!/bin/bash


set -e

echo "初始化Gradle项目..."

# 检查build.gradle文件
if [ ! -f "build.gradle" ]; then
    echo "警告: build.gradle 不存在，创建默认文件..."
    cat > build.gradle << 'EOL'
plugins {
    id 'groovy'
    id 'java'
    id 'jacoco'
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.codehaus.groovy:groovy-all:3.0.9'
    
    testImplementation 'junit:junit:4.13.2'
    testImplementation 'com.lesfurets:jenkins-pipeline-unit:1.9'
    testImplementation 'org.mockito:mockito-core:3.12.4'
}

test {
    reports {
        html.required = true
        junitXml.required = true
    }
}

jacoco {
    toolVersion = "0.8.7"
}

jacocoTestReport {
    reports {
        xml.required = true
        html.required = true
    }
    
    dependsOn test
}
EOL
fi

# 使用Docker初始化Gradle Wrapper
echo "使用Docker初始化Gradle Wrapper..."
docker run --rm -v "$(pwd)":/home/gradle/project -w /home/gradle/project gradle:7.6.1-jdk11 gradle wrapper --no-daemon

echo "Gradle Wrapper初始化成功！"
echo "现在可以使用 ./gradlew 或 ./run-tests-docker.sh 来运行测试"
