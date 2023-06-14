#! /bin/bash
echo "💚 API 서버에 접속 합니다. \n"

PROJECT_PATH=/home/ubuntu/NPEC
PROJECT_NAME=npec
PROJECT_BUILD_PATH=build/libs

echo "💚SpringBoot 프로젝트 빌드를 시작합니다."

./gradlew build --exclude-task test

CURRENT_PID=$(pgrep -f ${PROJECT_NAME}-.*.jar | head -n 1)

if [ -z "$CURRENT_PID" ]; then
  echo "💚 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "💚 구동중인 애플리케이션을 종료했습니다. (pid : $CURRENT_PID)"
  kill -15 $CURRENT_PID
fi

echo "💚 SpringBoot 애플리케이션을 실행합니다."

JAR_PATH=$(ls $PROJECT_PATH/$PROJECT_BUILD_PATH/ | grep SNAPSHOT.jar)

sudo -E nohup java -jar -Dspring.profiles.active=prod $PROJECT_PATH/$PROJECT_BUILD_PATH/$JAR_PATH &


echo "💚 완료! "
