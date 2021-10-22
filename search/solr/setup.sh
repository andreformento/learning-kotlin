#!/bin/bash
set -e

wait_for_solr() {
  SOLR_URL="$1"

  RETRY_COUNT=0
  while [[ "$(curl -s -o /dev/null -w ''%{http_code}'' $SOLR_URL)" != "200" ]]; do
    if [ $RETRY_COUNT -gt 60 ]; then
       echo "Oops! Something is wrong. See details with 'docker-compose logs -f solr3'"
       exit 1
    fi

    sleep 0.5
    echo "retry $SOLR_URL $RETRY_COUNT"
    ((RETRY_COUNT+=1))
  done;

  echo "Done! $SOLR_URL"
}

wait_for_solr "http://solr1:8983/solr/"
wait_for_solr "http://solr2:8983/solr/"
wait_for_solr "http://solr3:8983/solr/"

curl -X POST "http://solr3:8983/solr/admin/collections?action=CREATE&name=products&numShards=2&replicationFactor=1&wt=json"

wait_for_solr "http://solr3:8983/solr/products/select?q.op=OR&q=*:*"

curl -X POST \
        -H 'Content-type:application/json' \
        -d @/solr/config/request-handler.json \
        "http://solr3:8983/api/collections/products/config"

curl -X POST \
        -H 'Content-type:application/json' \
        -d @/solr/sample/products.json \
        "http://solr3:8983/solr/products/update?commit=true"

echo "Solr configured with success"
