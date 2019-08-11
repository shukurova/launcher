package ru.itpark.gameslauncher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itpark.gameslauncher.domain.CompanyDomain;
import ru.itpark.gameslauncher.domain.UserDomain;
import ru.itpark.gameslauncher.dto.company.*;
import ru.itpark.gameslauncher.exception.CompanyAlreadyExistsException;
import ru.itpark.gameslauncher.exception.GameNotFoundException;
import ru.itpark.gameslauncher.repository.CompanyRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;

    public List<CompanyCondensedResponseDto> getAllApproved() {
        return companyRepository.getAllApproved();
    }

    public Optional<CompanyResponseDto> findApprovedById(long id) {
        return companyRepository.findApprovedById(id);
    }

    public List<CompanyCondensedResponseDto> getAllNotApproved() {
        return companyRepository.getAllNotApproved();
    }

    public Optional<NotApprovedCompanyResponseDto> findNotApprovedById(long id) {
        return companyRepository.findNotApprovedById(id);
    }

    public List<CompanyCondensedResponseDto> getAllReturned() {
        return companyRepository.getAllReturned();
    }

    public Optional<NotApprovedCompanyResponseDto> findReturnedById(long id) {
        return companyRepository.findReturnedById(id);
    }

    public Optional<CompanyDomain> createCompany(CompanyRequestDto dto) {
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

        return companyRepository.createCompany(company);
    }

    public void approveCompany(long id) {
        companyRepository.approveCompany(id);
    }

    //TODO: корректно вытащить e-mail создателя заявки
    public ReturnedCompanyResponseDto returnCompany(long id,
                                                    ReturnedCompanyRequestDto dto) {
        var company = companyRepository.findNotApprovedById(id)
                .orElseThrow(() -> new GameNotFoundException("Company not found!"));

//        emailService.sendSimpleMessage(
//                userEmail,
//                "Edit your record",
//                String.format("Please, check your game record. You need to edit your game.\n %s", comment));
        companyRepository.returnCompany(id, company.getName(), dto.getComment());

        return companyRepository.findNotApprovedCompanyWithCommentById(id);
    }

    public Optional<CompanyResponseDto> editCompany(long id, CompanyRequestDto dto) {
        return companyRepository.editCompany(id, dto);
    }
}
