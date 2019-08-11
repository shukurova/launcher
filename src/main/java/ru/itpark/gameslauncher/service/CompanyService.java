package ru.itpark.gameslauncher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itpark.gameslauncher.dto.company.CompanyCondensedResponseDto;
import ru.itpark.gameslauncher.dto.company.CompanyResponseDto;
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
}
