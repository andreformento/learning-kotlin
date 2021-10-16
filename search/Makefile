solr-destroy:
	@docker-compose stop -t 0
	@docker-compose rm -f

solr-create:
	@docker-compose up -d
	@timeout 30 bash -c 'printf "solr1 "; while [[ "$$(curl -s -o /dev/null -w ''%{http_code}'' http://localhost:8981/solr/)" != "200" ]]; do sleep 0.5; printf "."; done; echo "Done! http://localhost:8981/solr/"' || (echo "Oops! Something is wrong. See details with 'docker logs solr1'" && false)
	@timeout 30 bash -c 'printf "solr2 "; while [[ "$$(curl -s -o /dev/null -w ''%{http_code}'' http://localhost:8982/solr/)" != "200" ]]; do sleep 0.5; printf "."; done; echo "Done! http://localhost:8982/solr/"' || (echo "Oops! Something is wrong. See details with 'docker logs solr2'" && false)
	@timeout 30 bash -c 'printf "solr3 "; while [[ "$$(curl -s -o /dev/null -w ''%{http_code}'' http://localhost:8983/solr/)" != "200" ]]; do sleep 0.5; printf "."; done; echo "Done! http://localhost:8983/solr/"' || (echo "Oops! Something is wrong. See details with 'docker logs solr3'" && false)
	@curl -X POST 'http://localhost:8983/solr/admin/collections?action=CREATE&name=products&numShards=2&replicationFactor=1&wt=json'
	@curl -X POST \
          -H 'Content-type:application/json' \
          -d @solr/config/01_request-handler.json \
          http://localhost:8983/api/collections/products/config
	@docker run --rm -v ${PWD}/solr/sample:/solr/sample --network=host solr:8.10.0 post -c products /solr/sample/products.csv

solr-recreate: solr-destroy solr-create
