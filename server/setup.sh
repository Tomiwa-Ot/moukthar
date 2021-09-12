#!/usr/bin/env bash

# Tested on Ubuntu
sudo mv moukthar.service /etc/systemd/system/
sudo systemctl start nginx
sudo systemctl enable nginx
sudo systemctl enable moukthar.service
sudo systemctl start moukthar.service
