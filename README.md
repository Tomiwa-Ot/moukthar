## Moukthar
Remote adminitration tool for android

### Features
- Notifications listener
- SMS listener
- Phone call recording
- Image capturing and screenshots
- Persistence 
- Read & write contacts
- List installed applications
- Download & upload files
- Get device location

### Installation
- Start web socket server or deploy as service in linux
  ```console
  php Server/web\ socket/App.php
  # OR
  sudo mv Server/websocket.service /etc/systemd/system/
  sudo systemctl daemon-reload
  sudo systemctl enable websocket.service
  sudo systemctl start websocket.service
  ```
- Execute ```database.sql```
- Set database credentials in ```Server/c2-server/.env``` and ```Server/web socket/.env```
- Deploy C2 server
  ```xml
  # Apache conf
    <Directory /var/www/html/c2-server>
        Options -Indexes
        DirectoryIndex app.php
        AllowOverride All
        Require all granted
    </Directory>
  ```
- Set C2 server and web socket server address in client
- Compile APK using Android Studio

### Screenshots
![Dashboard](screenshots/dashboard.png)

### TODO
- Implement modals for issuing commands
- Fix logging capability on dashboard
