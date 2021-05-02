package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

  @Autowired
  MeetingService meetingService;

  @Autowired
  ParticipantService participantService;

  @RequestMapping(value = "", method = RequestMethod.GET)
  public ResponseEntity<?> getMeetings() {
    Collection<Meeting> meetings = meetingService.getAll();
    return new ResponseEntity<>(meetings, HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<?> getMeeting(@PathVariable("id") long meetingId) {
    Meeting meeting = meetingService.findById(meetingId);
    if (meeting == null) {
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(meeting, HttpStatus.OK);
  }

  @RequestMapping(value = "searchMeeting/{searchPhrase}", method = RequestMethod.GET)
  public ResponseEntity<?> searchMeetingByTitleOrDescription(@PathVariable("searchPhrase") String searchPhrase) {
    Collection<Meeting> meetings = meetingService.searchMeetingByTitleOrDescription(searchPhrase);
    return new ResponseEntity<>(meetings, HttpStatus.OK);
  }

  @RequestMapping(value = "getForUser/{userLogin}", method = RequestMethod.GET)
  public ResponseEntity<?> getMeetingsForUser(@PathVariable("userLogin") String userLogin) {
    Participant participant = participantService.findByLogin(userLogin);
    if (participant == null) {
      return new ResponseEntity<>("Participant with login \"" + userLogin
          + "\" doesn't exist", HttpStatus.NOT_FOUND);
    }
    Collection<Meeting> meetings = meetingService.getMeetingsForUser(participant);
    return new ResponseEntity<>(meetings, HttpStatus.OK);
  }

  @RequestMapping(value = "", method = RequestMethod.POST)
  public ResponseEntity<?> createMeeting(@Valid @RequestBody Meeting meeting) {
    meetingService.add(meeting);
    return new ResponseEntity<>(meeting, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/{meetingId}", method = RequestMethod.POST)
  public ResponseEntity<?> addParticipants(@PathVariable long meetingId, @RequestBody List<String> participantsToAdd) {
    if (participantsToAdd.isEmpty()) {
        return new ResponseEntity<>("There is no participants to add", HttpStatus.NO_CONTENT);
    }
    Collection<Participant> participants = new ArrayList<>();
    for (String participantToAdd: participantsToAdd) {
      Participant participant = participantService.findByLogin(participantToAdd);
      if (participant == null) {
        return new ResponseEntity<>("Participant with login \"" + participantToAdd
            + "\" doesn't exist", HttpStatus.NOT_FOUND);
      }
      participants.add(participant);
    }
    Meeting meeting = meetingService.findById(meetingId);
    if (meeting == null) {
      return new ResponseEntity<>("Meeting with id \"" + meetingId
          + "\" doesn't exist", HttpStatus.NOT_FOUND);
    }
    meetingService.addParticipants(meeting, participants);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/participants/{meetingId}", method = RequestMethod.GET)
  public ResponseEntity<?> getParticipants(@PathVariable long meetingId) {
    Meeting meeting = meetingService.findById(meetingId);
    if (meeting == null) {
      return new ResponseEntity<>("Meeting with id \"" + meetingId
          + "\" doesn't exist", HttpStatus.NOT_FOUND);
    }
    List<String> participants = meeting.getParticipants().stream()
        .map(Participant::getLogin)
        .collect(Collectors.toList());
    return new ResponseEntity<>(participants, HttpStatus.OK);
  }

  @RequestMapping(value = "/{meetingId}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteMeeting(@PathVariable("meetingId") long meetingId) {
    Meeting meeting = meetingService.findById(meetingId);
    if (meeting == null) {
      return new ResponseEntity<>("Meeting with id \"" + meetingId
          + "\" doesn't exist", HttpStatus.NOT_FOUND);
    }
    meetingService.delete(meeting);
    return new ResponseEntity(HttpStatus.NO_CONTENT);
  }

  @RequestMapping(value = "/{meetingId}", method = RequestMethod.PUT)
  public ResponseEntity<?> updateMeeting(@PathVariable("meetingId") long meetingId, @RequestBody Meeting meeting) {
    Meeting foundMeeting = meetingService.findById(meetingId);
    if (foundMeeting == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    meetingService.update(meeting);
    return new ResponseEntity<>(foundMeeting, HttpStatus.OK);
  }

  @RequestMapping(value = "/removeParticipants/{meetingId}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteParticipants(@PathVariable long meetingId, @RequestBody List<String> participantsToRemove) {
    if (participantsToRemove.isEmpty()) {
      return new ResponseEntity<>("There is no participants to add", HttpStatus.NO_CONTENT);
    }
    Meeting meeting = meetingService.findById(meetingId);
    if (meeting == null) {
      return new ResponseEntity<>("Meeting with id \"" + meetingId
          + "\" doesn't exist", HttpStatus.NOT_FOUND);
    }
    Collection<Participant> participants = new ArrayList<>();
    for (String participantToRemove: participantsToRemove) {
      Participant participant = participantService.findByLogin(participantToRemove);
      if (participant == null) {
        return new ResponseEntity<>("Participant with login \"" + participantToRemove
            + "\" doesn't exist", HttpStatus.NOT_FOUND);
      }
      if (!meeting.getParticipants().contains(participant)) {
        return new ResponseEntity<>("Participant with login \"" + participantToRemove
            + "\" is not assigned to this meeting", HttpStatus.NOT_FOUND);
      }
      participants.add(participant);
    }

    meetingService.removeParticipants(meeting, participants);
    return new ResponseEntity<>(HttpStatus.OK);
  }



}
