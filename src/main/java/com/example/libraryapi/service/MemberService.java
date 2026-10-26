package com.example.libraryapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.libraryapi.exception.members.DuplicateEmailException;
import com.example.libraryapi.exception.members.InvalidMemberException;
import com.example.libraryapi.exception.members.MemberNotFoundException;
import com.example.libraryapi.model.Member;
import com.example.libraryapi.notification.NotificationSender;
import com.example.libraryapi.repository.MemberRepository;

@Service 
public class MemberService {
    private final MemberRepository memberRepository;

    private final NotificationSender notificationSender;

    public MemberService(MemberRepository memberRepository, @Qualifier("ConsoleNotificationSender") NotificationSender notificationSender) {
        this.memberRepository = memberRepository;
        this.notificationSender = notificationSender;
    }

    public Member getMemberById(Long id) {
        return this.memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + id));
    }

    public List<Member> getAllMembers() {
        return this.memberRepository.findAll();
    }

    public Member createMember(Member member) {
        validateMember(member);

        // Make sure email is unique
        if (this.memberRepository.findByEmail(member.getEmail().trim().toLowerCase()).isPresent()) {
            throw new DuplicateEmailException("Member with email " + member.getEmail() + " already exists");
        }

        // Registration date is set to the current date when creating a new member
        member.setInscriptionDate(java.time.LocalDate.now());
        // Normalize email to lowercase
        member.setEmail(member.getEmail().trim().toLowerCase());

        // Send notification
        this.notificationSender.sendNotification(
            "Welcome to the library, " + member.getName() + "! Your registration is successful.",
            member.getEmail()
        );

        return this.memberRepository.save(member);
    }

    private void validateMember(Member member) {
        if (member.getName() == null || member.getName().trim().isEmpty()) {
            throw new InvalidMemberException("Member name cannot be null or empty");
        }
        if (member.getEmail() == null || member.getEmail().trim().isEmpty()) {
            throw new InvalidMemberException("Member email cannot be null or empty");
        }
    }
}
