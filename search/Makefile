destroy:
	@docker-compose stop -t 0
	@docker-compose rm -fa
	@docker volume rm -f search_solr-data

create:
	@docker-compose up -d
	@timeout 30 bash -c 'printf "solr "; while [[ "$$(curl -s -o /dev/null -w ''%{http_code}'' http://localhost:8983/solr/)" != "200" ]]; do sleep 0.5; printf "."; done; echo "Done! http://localhost:8983/solr/"' || (echo "Oops! Something is wrong. See details with 'docker logs solr'" && false)

recreate: destroy create
