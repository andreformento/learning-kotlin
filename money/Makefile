database:
	@docker rm -f database || true
	@docker run -d \
	            --name=database \
	            -p 5432:5432 \
	            -e POSTGRES_DB=mydb \
	            -e POSTGRES_USER=user \
	            -e POSTGRES_PASSWORD=password \
	            -v $$(pwd)/migrations/:/docker-entrypoint-initdb.d/:ro \
	            postgres:13.3

wait-for-database: database
	@until docker run -it -e PGPASSWORD=password --rm --link database:pg postgres:13.3 psql -U user -h pg -c "select 1" -d mydb; do sleep 0.5; done

run: wait-for-database
	@./gradlew bootRun

signup:
	@curl -v -X POST 'http://localhost:8080/user/auth/signup' \
          -H 'Content-Type: application/json' \
          --data-raw '{"name":"Batman", "email":"a@a.com", "password":"pass123"}'

login:
	@curl -v -c cookies.txt -X POST 'http://localhost:8080/user/auth/login' \
          -H 'Content-Type: application/json' \
          --data-raw '{"email":"a@a.com", "password":"pass123"}'

get-user:
	@curl -v -b cookies.txt 'http://localhost:8080/user'
