#!/bin/bash
set -e

request() {
  if http --check-status --ignore-stdin -j $@; then
      echo 'OK!'
  else
      case $? in
          2) echo 'Request timed out!' ;;
          3) echo 'Unexpected HTTP 3xx Redirection!' ;;
          4) echo 'HTTP 4xx Client Error!' ;;
          5) echo 'HTTP 5xx Server Error!' ;;
          6) echo 'Exceeded --max-redirects=<n> redirects!' ;;
          *) echo 'Other Error!' ;;
      esac

      exit 1
  fi

}

wait_for_http() {
  SOLR_URL="$1"
  echo "waiting $SOLR_URL"

  RETRY_COUNT=0
  while [[ "$(curl -s -o /dev/null -w ''%{http_code}'' $SOLR_URL)" != "200" ]]; do
    if [ $RETRY_COUNT -gt 60 ]; then
       echo "Oops! Something is wrong. See details with 'docker-compose logs -f solr'"
       exit 1
    fi

    sleep 0.5
    echo -n "."
    ((RETRY_COUNT+=1))
  done;

  echo "Done! $SOLR_URL"
}

wait_for_document() {
  SOLR_URL="$1/products/select?indent=true&q.op=OR&q=*%3A*"
  echo "waiting documents $SOLR_URL"

  RETRY_COUNT=0
  while [[ "$(curl $SOLR_URL | grep -c 'First')" -le 0 ]]; do
    if [ $RETRY_COUNT -gt 30 ]; then
       echo "Oops! Documents not indexed. See details with 'docker-compose logs -f solr'"
       exit 1
    fi

    sleep 0.5
    echo -n "."
    ((RETRY_COUNT+=1))
  done;

  echo "Documents indexed $SOLR_URL with success"
}

solr_host="http://solr:8983"
wait_for_http "${solr_host}/solr/"

request "${solr_host}/solr/admin/collections?action=CREATE&name=products&numShards=1&replicationFactor=1&wt=json"

wait_for_http "${solr_host}/solr/products/select?indent=true&q.op=OR&q=*%3A*"

# request "${solr_host}/api/collections/products/config" < @/solr/config/request-handler.json

curl -X POST \
     -o /dev/null \
     -H 'Content-type:application/json' \
     -d @/solr/config/request-handler.json \
     "${solr_host}/api/collections/products/config"

# request "${solr_host}/solr/products/update?commit=true" @/solr/sample/products.json

curl -X POST \
     -o /dev/null \
     -H 'Content-type:application/json' \
     -d @/solr/sample/products.json \
     "${solr_host}/solr/products/update?commit=true"

wait_for_document "${solr_host}/solr"
