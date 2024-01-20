<?php

namespace Server\Model;

/**
 * Recording class
 */
class Recording
{
    /** @var int $id Recording ID */
    private int $id;

    /** @var int $clientID Victim's ID */
    private int $clientID;

    /** @var string $filename Recording filename */
    private string $filename;

    /** @var string $timestamp Recording timestamp */
    private string $timestamp;
    
    public function __construct(
        int $id,
        int $clientID,
        string $filename,
        string $timestamp)
    {
        $this->id = $id;
        $this->clientID = $clientID;
        $this->filename = $filename;
        $this->timestamp = $timestamp;
    }

    /**
     * Get Recording's ID
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
     * Get Recording name and extension
     * 
     * @return string
     */
    public function getFilename(): string
    {
        return $this->filename;
    }

    /**
     * Get Recording's timestamp
     */
    public function getTimestamp(): string
    {
        return $this->timestamp;
    }
}