solr-destroy:
	@docker-compose -f docker-compose.yml -f docker-compose.dev.yml stop -t 0
	@docker-compose -f docker-compose.yml -f docker-compose.dev.yml rm -f

solr-create:
	@docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d --build
	@docker-compose -f docker-compose.yml -f docker-compose.dev.yml logs -f solr-schema

solr-recreate: solr-destroy solr-create
