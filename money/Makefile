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

run:
	@./gradlew bootRun
