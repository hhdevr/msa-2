.PHONY: package build run stop clean re

TAG ?= 1.0

package:
	mvn clean package -DskipTests

build: package
	docker build -t msa2-order:$(TAG) order-service
	docker build -t msa2-payment:$(TAG) payment-service
	docker build -t msa2-delivery:$(TAG) delivery-service

run:
	docker compose up -d

stop:
	docker compose down

clean: stop
	docker rmi msa2-order:$(TAG) msa2-payment:$(TAG) msa2-delivery:$(TAG) || true

re: clean build run
