package com.company.enroller.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "meeting")
public class Meeting {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column
	@NotEmpty
	private String title;

	@Column
	@NotEmpty
	private String description;

	@Column
	@NotEmpty
	private String date;

	@JsonIgnore
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "meeting_participant", joinColumns = { @JoinColumn(name = "meeting_id") }, inverseJoinColumns = {
			@JoinColumn(name = "participant_login") })
	Set<Participant> participants = new HashSet<>();

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getDate() {
		return date;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void addParticipant(Participant participant) {
		this.participants.add(participant);
	}
	public void addParticipants(Collection<Participant> participants) {
		this.participants.addAll(participants);
	}

	public void removeParticipant(Participant participant) {
		this.participants.remove(participant);
	}

	public void removeParticipants(Collection<Participant> participants) {
		this.participants.removeAll(participants);
	}

	public Collection<Participant> getParticipants() {
		return participants;
	}

}