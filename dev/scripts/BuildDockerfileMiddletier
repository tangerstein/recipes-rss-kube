#!/bin/bash
cd dev/dockerfiles/middletier
sudo docker build -t recipes-rss-middletier .
sudo docker login --username="rssfachstudie" --password="performance-aware" --email="rssfachstudie@gmail.com"
sudo docker tag -f recipes-rss-edge rssfachstudie/recipes-rss:middletier
sudo docker push rssfachstudie/recipes-rss
