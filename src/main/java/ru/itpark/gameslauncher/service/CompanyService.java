package ru.itpark.gameslauncher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itpark.gameslauncher.domain.AuthorityDomain;
import ru.itpark.gameslauncher.domain.CompanyDomain;
import ru.itpark.gameslauncher.dto.company.*;
import ru.itpark.gameslauncher.exception.*;
import ru.itpark.gameslauncher.repository.CompanyRepository;
import ru.itpark.gameslauncher.repository.DeveloperRepository;
import ru.itpark.gameslauncher.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final DeveloperRepository developerRepository;

    public List<CompanyCondensedResponseDto> getAllApproved() {
        return companyRepository.getAllApproved()
                .orElseThrow(() -> new CompanyNotFoundException("Companies not found!"));
    }

    public CompanyResponseDto findApprovedById(long id) {
        return companyRepository.findApprovedById(id)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found!"));
    }

    public List<CompanyCondensedResponseDto> getAllNotApproved() {
        return companyRepository.getAllNotApproved()
                .orElseThrow(() -> new CompanyNotFoundException("Companies not found!"));
    }

    public NotApprovedCompanyResponseDto findNotApprovedById(long id) {
        return companyRepository.findNotApprovedById(id)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found!"));
    }

    public List<CompanyCondensedResponseDto> getAllReturned() {
        return companyRepository.getAllReturned()
                .orElseThrow(() -> new CompanyNotFoundException("Companies not found!"));
    }

    public NotApprovedCompanyResponseDto findReturnedById(long id) {
        return companyRepository.findReturnedById(id)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found!"));
    }

    public CompanyDomain createCompany(CompanyRequestDto dto) {
        if (companyRepository.checkExistsByName(dto.getName())) {
            throw new CompanyAlreadyExistsException(
                    String.format("Company with this name %s already exists!", dto.getName()));
        }

        var company = new CompanyDomain(
                0L,
                dto.getName(),
                dto.getCountry(),
                dto.getContent(),
                dto.getCreationDate(),
                false,
                false
        );

        return companyRepository.createCompany(company).orElseThrow(()-> new CreateException("Something bad happened! Please try later"));
    }

    public void approveCompany(long companyId) {
        companyRepository.approveCompany(companyId);
        var userId = developerRepository
                .getUsersIdByCompanyId(companyId).orElseThrow(() ->
                        new UserNotFoundException("Not found users for this company!"));

        for (Long id : userId) {
            var authority = new AuthorityDomain(
                    0,
                    id,
                    "ROLE_DEVELOPER"
            );

            userRepository.addRoleToUsers(authority);
        }
    }

    public ReturnedCompanyResponseDto returnCompany(long companyId,
                                                    ReturnedCompanyRequestDto dto) {
        var company = companyRepository.findNotApprovedById(companyId)
                .orElseThrow(() -> new GameNotFoundException("Company not found!"));
        List<String> emails = developerRepository
                .getDevelopersEmailsByCompanyId(companyId)
                .orElseThrow(() ->
                        new UserNotFoundException("Not found users for this company!"));
        ;

//        emailService.sendSimpleMessage(
//                emails,
//                "Edit your record",
//                String.format("Please, check your game record. You need to edit your game.\n %s", comment));
        companyRepository.returnCompany(companyId, company.getName(), dto.getComment());

        return companyRepository.findNotApprovedCompanyWithCommentById(companyId);
    }

    public Optional<CompanyResponseDto> editCompany(long id, CompanyRequestDto dto) {
        return companyRepository.editCompany(id, dto);
    }
}
