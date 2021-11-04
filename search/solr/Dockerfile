FROM solr:8.10.0

USER root

RUN set -ex; \
    apt-get update; \
    apt install -y httpie jq

USER $SOLR_USER
