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
    public CompanyResponseDto findApprovedById(@PathVariable long id) {
        return companyService.findApprovedById(id);
    }

    @GetMapping("/not-approved")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CompanyCondensedResponseDto> getAllNotApproved() {
        return companyService.getAllNotApproved();
    }

    @GetMapping("/not-approved/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public NotApprovedCompanyResponseDto findNotApprovedById(@PathVariable long id) {
        return companyService.findNotApprovedById(id);
    }

    @GetMapping("/returned")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CompanyCondensedResponseDto> getAllReturned() {
        return companyService.getAllReturned();
    }

    @GetMapping("/returned/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public NotApprovedCompanyResponseDto findReturnedById(@PathVariable long id) {
        return companyService.findReturnedById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public CompanyDomain createCompany(@RequestBody CompanyRequestDto dto,
                                       @AuthenticationPrincipal UserDomain domain) {
        return companyService.createCompany(dto, domain);
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
                                                    @RequestBody ReturnedCompanyRequestDto dto) {
        return companyService.returnCompany(id, dto);
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("@permissionService.isCompanyDeveloper(#id, #user.id)")
    @ResponseStatus(HttpStatus.OK)
    public void editCompany(@PathVariable long id,
                                          @RequestBody CompanyRequestDto dto,
                                          @AuthenticationPrincipal UserDomain user) {
        companyService.editCompany(id, dto);
    }
}
