package com.example.libraryapi.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.libraryapi.dto.loans.LoanResponse;
import com.example.libraryapi.dto.members.CreateMemberRequest;
import com.example.libraryapi.dto.members.MemberResponse;
import com.example.libraryapi.model.Member;
import com.example.libraryapi.service.LoanService;
import com.example.libraryapi.service.MemberService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping ("/api/members")
public class MemberController {
    private final MemberService memberService;
    private final LoanService loanService;

    public MemberController(MemberService memberService, LoanService loanService){
        this.memberService = memberService;
        this.loanService = loanService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MemberResponse> getMembers() {
        return this.memberService.getAllMembers().stream()
                .map(member -> this.toResponse(member))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MemberResponse getMemberById(@PathVariable("id") Long id) {
        Member member = this.memberService.getMemberById(id);
        return this.toResponse(member);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MemberResponse> createMember(@RequestBody CreateMemberRequest memberRequest) {
        Member member = new Member(
            null,
            memberRequest.name(), 
            memberRequest.email(), 
            null
        );

        Member savedMember = this.memberService.createMember(member);

        MemberResponse memberResponse = this.toResponse(savedMember);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(savedMember.getId())
            .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return new ResponseEntity<>(memberResponse, headers, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}/loans")
    @ResponseStatus(HttpStatus.OK)
    public List<LoanResponse> getMemberLoans(@PathVariable("id") Long id) {
        return this.loanService.getActiveLoansByMemberId(id).stream()
                .map(loan -> new LoanResponse(
                        loan.getId(),
                        loan.getBookId(),
                        loan.getMemberId(),
                        loan.getLoanDate(),
                        loan.getReturnDate(),
                        loan.getReturnedDate(),
                        loan.isOngoing(),
                        loan.isOverdue()
                    )
                )
                .collect(Collectors.toList());
    }

    private MemberResponse toResponse(Member member) {
        return new MemberResponse(
            member.getId(),
            member.getName(),
            member.getEmail(),
            member.getInscriptionDate()
        );
    }
}
