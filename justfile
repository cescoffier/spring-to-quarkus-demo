

start-infra:
    podman run --name database -d -it --rm=true \
            -e POSTGRES_USER=quarkus_test \
            -e POSTGRES_PASSWORD=quarkus_test \
            -e POSTGRES_DB=quarkus_test \
            -p 5432:5432 postgres:15-bullseye

stop-infra:
    podman stop database

restart-infra: stop-infra start-infra

stress:
    #!/usr/bin/env bash
    set -euxo pipefail
    http --ignore-stdin :8080/api
    http --ignore-stdin POST :8080/api title=foo
    ID=$(http --ignore-stdin POST :8080/api title=foo | jq '.id')
    echo $ID
    http --ignore-stdin  POST :8080/api title=bar
    http --ignore-stdin :8080/api
    http --ignore-stdin POST :8080/api title=baz
    http --ignore-stdin :8080/api
    http --ignore-stdin PATCH :8080/api/$ID title=foo-done completed:=true

mta:
    cd spring-todo-app
    podman run -it -v $(pwd):/opt/project:z,U quay.io/rhappsvcs/spring-to-quarkus-mta-cli:latest
    open windup-report/index.html

build-spring-java:
    cd spring-todo-app  && ./mvnw clean package -DskipTests

build-quarkus-java:
    cd quarkus-todo-app  && ./mvnw clean package -DskipTests

build-quarkus-spring-java:
    cd quarkus-spring-todo-app && ./mvnw clean package -DskipTests

build-spring-native:
    cd spring-todo-app  && ./mvnw clean native:compile -Pnative -DskipTests

build-quarkus-native:
    cd quarkus-todo-app  && ./mvnw clean package -DskipTests -Dnative

build-quarkus-spring-native:
    cd quarkus-spring-todo-app && ./mvnw clean package -DskipTests -Dnative

rss-spring-java: build-spring-java start-infra  && stop-infra
    #!/usr/bin/env bash
    set -euxo pipefail
    java -Xmx256M -Xms256M -jar spring-todo-app/target/spring-todo-app-0.0.1-SNAPSHOT.jar &
    PID=$!
    sleep 15
    just stress
    RSS=$(ps aux | grep -i java | grep -i spring | grep -v grep | grep -v bash | awk {'print $2'} | xargs ps -o pid,rss,command -p | awk '{$2=int($2/1024)"M";}{ print;}' | tail -n 1)
    echo $RSS
    echo $RSS > rss-spring-java.txt
    kill $PID

rss-quarkus-spring-java: build-quarkus-spring-java start-infra  && stop-infra
    #!/usr/bin/env bash
    set -euxo pipefail
    java -Xmx256M -Xms256M -jar quarkus-spring-todo-app/target/quarkus-app/quarkus-run.jar &
    PID=$!
    sleep 5
    just stress
    RSS=$(ps aux | grep -i java | grep -i quarkus | grep -v grep | grep -v bash | awk {'print $2'} | xargs ps -o pid,rss,command -p | awk '{$2=int($2/1024)"M";}{ print;}' | tail -n 1)
    echo $RSS
    echo $RSS > rss-quarkus-spring-java.txt
    kill $PID

rss-quarkus-java: build-quarkus-java start-infra  && stop-infra
    #!/usr/bin/env bash
    set -euxo pipefail
    java -Xmx256M -Xms256M -jar quarkus-todo-app/target/quarkus-app/quarkus-run.jar &
    PID=$!
    sleep 5
    just stress
    RSS=$(ps aux | grep -i java | grep -i quarkus | grep -v grep | grep -v bash  | awk {'print $2'} | xargs ps -o pid,rss,command -p | awk '{$2=int($2/1024)"M";}{ print;}' | tail -n 1)
    echo $RSS
    echo $RSS > rss-quarkus-java.txt
    kill $PID

rss-spring-native: start-infra build-spring-native  && stop-infra
    #!/usr/bin/env bash
    set -euxo pipefail
    ./spring-todo-app/target/spring-todo-app -Xmx256M -Xms256M &
    PID=$!
    sleep 10
    just stress
    RSS=$(ps aux | grep -i spring-todo-app | grep -v grep | grep -v bash  | awk {'print $2'} | xargs ps -o pid,rss,command -p | awk '{$2=int($2/1024)"M";}{ print;}' | tail -n 1)
    echo $RSS
    echo $RSS > rss-spring-native.txt
    kill $PID

rss-quarkus-spring-native: start-infra build-quarkus-spring-native  && stop-infra
    #!/usr/bin/env bash
    set -euxo pipefail
    ./quarkus-spring-todo-app/target/quarkus-spring-todo-app-1.0.0-SNAPSHOT-runner -Xmx256M -Xms256M &
    PID=$!
    sleep 10
    just stress
    RSS=$(ps aux | grep -i quarkus-spring-todo-app | grep -v grep | grep -v bash | awk {'print $2'} | xargs ps -o pid,rss,command -p | awk '{$2=int($2/1024)"M";}{ print;}' | tail -n 1)
    echo $RSS
    echo $RSS > rss-quarkus-spring-native.txt
    kill $PID

rss-quarkus-native: start-infra build-quarkus-native  && stop-infra
    #!/usr/bin/env bash
    set -euxo pipefail
    ./quarkus-todo-app/target/quarkus-todo-app-1.0.0-SNAPSHOT-runner -Xmx256M -Xms256M &
    PID=$!
    sleep 10
    just stress
    RSS=$(ps aux | grep -i quarkus-todo-app | grep -v grep | grep -v bash | awk {'print $2'} | xargs ps -o pid,rss,command -p | awk '{$2=int($2/1024)"M";}{ print;}' | tail -n 1)
    echo $RSS
    echo $RSS > rss-quarkus-native.txt
    kill $PID
