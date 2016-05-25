#!/bin/bash
cd dev/dockerfiles/edge
sudo docker build -t recipes-rss-edge .
sudo docker login --username="rssfachstudie" --password="performance-aware" --email="rssfachstudie@gmail.com"
sudo docker tag -f recipes-rss-edge rssfachstudie/recipes-rss:edge
sudo docker push rssfachstudie/recipes-rss
