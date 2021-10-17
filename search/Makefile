solr-destroy:
	@docker-compose stop -t 0
	@docker-compose rm -f

solr-create:
	@docker-compose up -d
	@docker-compose logs -f solr-schema

solr-recreate: solr-destroy solr-create
