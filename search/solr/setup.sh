#!/bin/bash
set -e

wait_for_solr() {
  SOLR_HOST="$1"
  SOLR_URL="http://$SOLR_HOST/solr/"

  RETRY_COUNT=0
  while [[ "$(curl -s -o /dev/null -w ''%{http_code}'' $SOLR_URL)" != "200" ]]; do
    if [ $RETRY_COUNT -gt 60 ]; then
       echo "Oops! Something is wrong. See details with 'docker-compose logs -f solr3'"
       exit 1
    fi

    sleep 0.5
    echo "retry $SOLR_HOST $RETRY_COUNT"
    ((RETRY_COUNT+=1))
  done;

  echo "Done! $SOLR_HOST"
}

wait_for_solr "solr1:8983"
wait_for_solr "solr2:8983"
wait_for_solr "solr3:8983"

curl -X POST "http://solr3:8983/solr/admin/collections?action=CREATE&name=products&numShards=2&replicationFactor=1&wt=json"

curl -X POST \
        -H 'Content-type:application/json' \
        -d @/solr/config/request-handler.json \
        "http://solr3:8983/api/collections/products/config"

curl -X POST \
        -H 'Content-type:application/json' \
        -d @/solr/sample/products.json \
        "http://solr3:8983/solr/products/update/json"

echo "Solr configured with success"
