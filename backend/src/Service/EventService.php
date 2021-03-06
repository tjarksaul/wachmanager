<?php
declare(strict_types=1);


namespace App\Service;


use App\Entity\DailyStats;
use App\Entity\Event;
use App\Entity\EventType;
use App\Interfaces\EventReader;
use App\Interfaces\EventWriter;
use App\Interfaces\StationNotFoundException;
use App\Interfaces\StationReader;
use Doctrine\DBAL\Exception\UniqueConstraintViolationException;

class EventService {
    private EventReader $eventReader;
    private EventWriter $eventWriter;
    private StationReader $stationReader;

    public function __construct(EventReader $eventReader, EventWriter $eventWriter, StationReader $stationReader) {
        $this->eventReader = $eventReader;
        $this->eventWriter = $eventWriter;
        $this->stationReader = $stationReader;
    }

    /**
     * @throws
     */
    public function create(string $stationId, EventType $type, string $id = null, \DateTimeInterface $date = null): string {
        $station = $this->stationReader->getStation($stationId);
        if (!$station) {
            throw new StationNotFoundException();
        }

        $event = new Event($station, $type, $date, $id);

        try {
            return $this->eventWriter->create($event);
        } catch (UniqueConstraintViolationException $e) {
            return $event->getId();
        }
    }

    public function get(\DateTimeInterface $date): DailyStats {
        return $this->eventReader->get($date);
    }

    /** @throws  */
    public function getByStation(\DateTimeInterface $date, string $stationId) {
        $station = $this->stationReader->getStation($stationId);
        if (!$station) {
            throw new StationNotFoundException();
        }

        return $this->eventReader->getByStation($date, $stationId);
    }
}
