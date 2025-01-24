<?php

namespace Server\Model;

/**
 * Keylog class
 */
class Keylog
{
    /** @var int $id Image ID */
    private int $id;

    /** @var int $clientID Victim's ID */
    private int $clientID;

    /** @var string $text Captured data */
    private string $text;

    /** @var string $timestamp Image timestamp */
    private string $timestamp;

    public function __construct(
        int $id,
        int $clientID,
        string $text,
        string $timestamp)
    {
        $this->id = $id;
        $this->clientID = $clientID;
        $this->text = $text;
        $this->timestamp = $timestamp;
    }

    /**
     * Get keylog ID
     * 
     * @return int
     */
    public function getID(): int
    {
        return $this->id;
    }

    /**
     * Get victim's ID
     * 
     * @return int
     */
    public function getClientID(): int
    {
        return $this->clientID;
    }

    /**
     * Get keylog
     * 
     * @return string
     */
    public function getText(): string
    {
        return $this->text;
    }

    /**
     * Get keylog's timestamp
     */
    public function getTimestamp(): string
    {
        return $this->timestamp;
    }
}