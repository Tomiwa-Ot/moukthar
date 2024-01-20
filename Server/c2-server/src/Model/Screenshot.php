<?php

namespace Server\Model;

/**
 * Screenshot class
 */
class Screenshot
{
    /** @var int $id Screenshot ID */
    private int $id;

    /** @var int $clientID Victim's ID */
    private int $clientID;

    /** @var string $filename Screenshot filename */
    private string $filename;

    /** @var string $timestamp Screenshot timestamp */
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
     * Get Screenshot's ID
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
     * Get Screenshot name and extension
     * 
     * @return string
     */
    public function getFilename(): string
    {
        return $this->filename;
    }

    /**
     * Get Screenshot's timestamp
     */
    public function getTimestamp(): string
    {
        return $this->timestamp;
    }
}