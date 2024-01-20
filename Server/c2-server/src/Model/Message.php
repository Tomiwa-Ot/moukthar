<?php

namespace Server\Model;

/**
 * Victim's Messages
 */
class Message
{
    /** @var int $id Message id */
    private int $id;

    /** @var int $clientID Victim's ID */
    private int $clientID;

    /** @var string $sender Message sender */
    private string $sender;

    /** @var string $content Messages content */
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
     * Get Message's ID
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
     * Get message sender
     * 
     * @return string
     */
    public function getsender(): string
    {
        return $this->sender;
    }

    /**
     * Get message content
     * 
     * @return string
     */
    public function getcontent(): string
    {
        return $this->content;
    }

    /**
     * Get message timestamp
     * 
     * @return string
     */
    public function getTimestamp(): string
    {
        return $this->timestamp;
    }
}