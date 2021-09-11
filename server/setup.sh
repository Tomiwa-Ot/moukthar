#!/bin/bash

# Tested on Ubuntu
sudo mv moukthar.service /etc/systemd/system/
sudo systemctl enable moukthar.service
sudo systemctl start moukthar.service