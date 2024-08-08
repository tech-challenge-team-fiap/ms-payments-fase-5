#!/bin/bash

if [ -z "$1" ]; then
  echo "Informe a tag para a imagem."
else
  mvn clean package install && docker build -t api-payments:"$1" .
fi