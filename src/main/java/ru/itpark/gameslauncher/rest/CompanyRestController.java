package ru.itpark.gameslauncher.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.itpark.gameslauncher.domain.CompanyDomain;
import ru.itpark.gameslauncher.domain.UserDomain;
import ru.itpark.gameslauncher.dto.company.*;
import ru.itpark.gameslauncher.service.CompanyService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class CompanyRestController {
    private final CompanyService companyService;

    @GetMapping
    public List<CompanyCondensedResponseDto> getAllApproved() {
        return companyService.getAllApproved();
    }

    @GetMapping("/{id}")
    public Optional<CompanyResponseDto> findApprovedById(@PathVariable long id) {
        return companyService.findApprovedById(id);
    }

    @GetMapping("/not-approved")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CompanyCondensedResponseDto> getAllNotApproved() {
        return companyService.getAllNotApproved();
    }

    @GetMapping("/not-approved/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<NotApprovedCompanyResponseDto> findNotApprovedById(@PathVariable long id) {
        return companyService.findNotApprovedById(id);
    }

    @GetMapping("/returned")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CompanyCondensedResponseDto> getAllReturned() {
        return companyService.getAllReturned();
    }

    @GetMapping("/returned/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<NotApprovedCompanyResponseDto> findReturnedById(@PathVariable long id) {
        return companyService.findReturnedById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('DEVELOPER')")
    public Optional<CompanyDomain> createCompany(@RequestBody CompanyRequestDto dto,
                                                 @AuthenticationPrincipal UserDomain user) {
        return companyService.createCompany(dto, user);
    }

    @PostMapping("/not-approved/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public void approveCompany(@PathVariable long id) {
        companyService.approveCompany(id);
    }

    @PostMapping("/not-approved/{id}/return")
    @PreAuthorize("hasRole('ADMIN')")
    public ReturnedCompanyResponseDto returnCompany(@PathVariable long id,
                                                    @AuthenticationPrincipal UserDomain user,
                                                    @RequestBody ReturnedCompanyRequestDto dto) {
        return companyService.returnCompany(id, user, dto);
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("@permissionService.isGameDeveloper(#id, #user.id)")
    public Optional<CompanyResponseDto> editCompany(@PathVariable long id,
                                                    @RequestBody CompanyRequestDto dto,
                                                    @AuthenticationPrincipal UserDomain user) {
        return companyService.editCompany(id, dto);
    }
}
