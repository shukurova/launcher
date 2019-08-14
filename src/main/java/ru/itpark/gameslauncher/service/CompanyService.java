package ru.itpark.gameslauncher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itpark.gameslauncher.domain.AuthorityDomain;
import ru.itpark.gameslauncher.domain.CompanyDomain;
import ru.itpark.gameslauncher.domain.UserDomain;
import ru.itpark.gameslauncher.dto.company.*;
import ru.itpark.gameslauncher.exception.*;
import ru.itpark.gameslauncher.repository.CompanyRepository;
import ru.itpark.gameslauncher.repository.DeveloperRepository;
import ru.itpark.gameslauncher.repository.UserRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final DeveloperRepository developerRepository;

    public List<CompanyCondensedResponseDto> getAllApproved() {
        return companyRepository.getAllApproved();
    }

    public CompanyResponseDto findApprovedById(long id) {
        return companyRepository.findApprovedById(id)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found!"));
    }

    public List<CompanyCondensedResponseDto> getAllNotApproved() {
        return companyRepository.getAllNotApproved();
    }

    public NotApprovedCompanyResponseDto findNotApprovedById(long id) {
        return companyRepository.findNotApprovedById(id)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found!"));
    }

    public List<CompanyCondensedResponseDto> getAllReturned() {
        return companyRepository.getAllReturned();
    }

    public NotApprovedCompanyResponseDto findReturnedById(long id) {
        return companyRepository.findReturnedById(id)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found!"));
    }

    public CompanyDomain createCompany(CompanyRequestDto dto,
                                       UserDomain userDomain) {
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

        var companyAfterCreation = companyRepository.createCompany(company)
                .orElseThrow(()-> new CreateException("Failed create the record! Please, try later."));

        developerRepository
                .createDeveloper(userDomain.getId(), companyAfterCreation.getId());
        return companyAfterCreation;
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
                .orElseThrow(() -> new CompanyNotFoundException("Company not found!"));
        List<String> emails = developerRepository
                .getDevelopersEmailsByCompanyId(companyId)
                .orElseThrow(() ->
                        new UserNotFoundException("Not found users for this company!"));
        ;

//        emailService.sendSimpleMessage(
//                emails,
//                "Edit your record",
//                String.format("Please, check your game record. You need to edit your game.\n %s", comment));
        companyRepository.returnCompany(companyId, dto.getComment());

        return companyRepository.findNotApprovedCompanyWithCommentById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found!"));
    }

    public void editCompany(long id, CompanyRequestDto dto) {
        companyRepository.editCompany(id, dto);
    }
}
