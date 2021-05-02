package com.company.enroller.persistence;

import java.util.Collection;
import java.util.stream.Collectors;

import com.company.enroller.model.Participant;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Meeting;

@Component("meetingService")
public class MeetingService {

	DatabaseConnector connector;
	Session session;

	public MeetingService() {
		connector = DatabaseConnector.getInstance();
		session = connector.getSession();
	}

	public Collection<Meeting> getAll() {
		final String hql = "FROM Meeting ORDER BY title";
		Query query = connector.getSession().createQuery(hql);
		return query.list();
	}

	public Collection<Meeting> searchMeetingByTitleOrDescription(String searchPhrase) {
		final String hql = "FROM Meeting WHERE (title LIKE '%' || :searchPhrase || '%') OR (description LIKE '%' || :searchPhrase || '%') ORDER BY title";
		Query query = connector.getSession().createQuery(hql).setParameter("searchPhrase", searchPhrase);
		return query.list();
	}

	public Meeting findById(long meetingId) {
		return (Meeting) connector.getSession().get(Meeting.class, meetingId);
	}

	public void add(Meeting meeting) {
		Transaction transaction = session.beginTransaction();
		session.save(meeting);
		transaction.commit();
	}

	public void addParticipants(Meeting meeting, Collection<Participant> participant) {
		Transaction transaction = session.beginTransaction();
		meeting.addParticipants(participant);
		session.save(meeting);
		transaction.commit();
	}

	public void delete(Meeting meeting) {
		Transaction transaction = session.beginTransaction();
		session.delete(meeting);
		transaction.commit();
	}

	public void update(Meeting meeting) {
		Transaction transaction = session.beginTransaction();
		session.merge(meeting);
		transaction.commit();
	}

	public void removeParticipants(Meeting meeting, Collection<Participant> participants) {
		Transaction transaction = session.beginTransaction();
		meeting.removeParticipants(participants);
		session.save(meeting);
		transaction.commit();
	}

	public Collection<Meeting> getMeetingsForUser(Participant participant) {
		Collection<Meeting> meetings = getAll();
		return meetings.stream()
				.filter(meeting -> meeting.getParticipants().contains(participant))
				.collect(Collectors.toList());
	}

}
