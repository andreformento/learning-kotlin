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

wait-for-database:
	@until docker run -it -e PGPASSWORD=password --rm --link database:pg postgres:13.3 psql -U user -h pg -c "select 1" -d mydb; do sleep 0.5; done

run: database wait-for-database
	@./gradlew bootRun
