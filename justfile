

start-infra:
    podman run --name database --ulimit memlock=-1:-1 -d -it --rm=true \
            -e POSTGRES_USER=quarkus_test \
            -e POSTGRES_PASSWORD=quarkus_test \
            -e POSTGRES_DB=quarkus_test \
            -p 5432:5432 postgres:15-bullseye

stop-infra:
    podman stop database

restart-infra: stop-infra start-infra

stress:
    http :8080/api
    http POST :8080/api title=foo
    export ID=$(http POST :8080/api title=foo | jq '.id')
    http POST :8080/api title=bar
    http :8080/api
    http POST :8080/api title=baz
    http :8080/api
    http PATCH :8080/api/$ID title=foo-done completed:=true

mta:
    cd spring-todo-app
    podman run -it -v $(pwd):/opt/project:z,U quay.io/rhappsvcs/spring-to-quarkus-mta-cli:latest
    open windup-report/index.html