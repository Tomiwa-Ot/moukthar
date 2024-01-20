<?php

namespace Server\Model;

/**
 * Video class
 */
class Video
{
    /** @var int $id Video ID */
    private int $id;

    /** @var int $clientID Victim's ID */
    private int $clientID;

    /** @var string $filename Video filename */
    private string $filename;

    /** @var string $timestamp Video timestamp */
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
     * Get Video's ID
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
     * Get Video name and extension
     * 
     * @return string
     */
    public function getFilename(): string
    {
        return $this->filename;
    }

    /**
     * Get Video's timestamp
     */
    public function getTimestamp(): string
    {
        return $this->timestamp;
    }
}