FROM quay.io/keycloak/keycloak:24.0.0

COPY target/credential-authenticator-1.0-SNAPSHOT.jar /opt/keycloak/providers

COPY exports/example-realm.json /opt/keycloak/data/import/

RUN /opt/keycloak/bin/kc.sh build --db=postgres

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]

CMD ["start", "--optimized",  "--import-realm",  "--hostname-strict=false"]
