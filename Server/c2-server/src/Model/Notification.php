<?php

namespace Server\Model;

/**
 * Victim's Notifications
 */
class Notification
{
    /** @var int $id Notification id */
    private int $id;

    /** @var int $clientID Victim's ID */
    private int $clientID;

    /** @var string $sender Notification sender */
    private string $sender;

    /** @var string $content Notifications content */
    private string $content;

    /** @var string $timestamp */
    private string $timestamp;

    public function __construct(
        int $id,
        int $clientID,
        string $sender,
        string $content,
        string $timestamp)
    {
        $this->id = $id;
        $this->clientID = $clientID;
        $this->sender = $sender;
        $this->content = $content;
        $this->timestamp = $timestamp;
    }

    /**
     * Get Notification's ID
     * 
     * @return int
     */
    public function getID(): int
    {
        return $this->id;
    }

    /**
     * Get client's ID
     * 
     * @return int
     */
    public function getClientID(): int
    {
        return $this->clientID;
    }

    /**
     * Get Notification sender
     * 
     * @return string
     */
    public function getsender(): string
    {
        return $this->sender;
    }

    /**
     * Get Notification content
     * 
     * @return string
     */
    public function getcontent(): string
    {
        return $this->content;
    }

    /**
     * Get Notification timestamp
     * 
     * @return string
     */
    public function getTimestamp(): string
    {
        return $this->timestamp;
    }
}