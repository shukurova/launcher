package ru.itpark.gameslauncher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itpark.gameslauncher.domain.UserDomain;
import ru.itpark.gameslauncher.dto.company.CompanyCondensedResponseDto;
import ru.itpark.gameslauncher.dto.company.CompanyUpdateRequestDto;
import ru.itpark.gameslauncher.dto.game.GameCondensedResponseDto;
import ru.itpark.gameslauncher.dto.game.GameUpdateRequestDto;
import ru.itpark.gameslauncher.dto.user.UserCompanyResponseDto;
import ru.itpark.gameslauncher.dto.user.UserGameResponseDto;
import ru.itpark.gameslauncher.dto.user.UserProfileResponseDto;
import ru.itpark.gameslauncher.exception.CompanyNotFoundException;
import ru.itpark.gameslauncher.exception.GameNotFoundException;
import ru.itpark.gameslauncher.repository.CompanyRepository;
import ru.itpark.gameslauncher.repository.GameRepository;
import ru.itpark.gameslauncher.repository.UserRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final CompanyRepository companyRepository;

    public UserProfileResponseDto getUserInformation(UserDomain domain) {
        return userRepository.getUserInformation(domain);
    }

    public UserGameResponseDto findUserGameById(long id) {
        return gameRepository.findUserGameByGameId(id)
                .orElseThrow(() -> new GameNotFoundException("Users games not found!"));
    }

    public List<GameCondensedResponseDto> findUserGames(UserDomain domain) {
        return gameRepository.findUserGames(domain);
    }

    public List<GameCondensedResponseDto> findCreatedGamesByUser(UserDomain domain) {
        return gameRepository.findCreatedGamesByUser(domain);
    }

    public void createGameUpdateRequest(long id,
                                        GameUpdateRequestDto dto) {

    }

    public List<CompanyCondensedResponseDto> findUserCompanies(UserDomain domain) {
        return companyRepository.getCompanyByUserId(domain.getId());
    }

    public List<CompanyCondensedResponseDto> findCreatedCompaniesByUser(UserDomain domain) {
        return companyRepository.findCreatedCompaniesByUser(domain);
    }

    public UserCompanyResponseDto findUserCompanyById(long id) {
        return companyRepository.findUserCompanyByCompanyId(id)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found!"));
    }

    public void createCompanyUpdateRequest(long id,
                                           CompanyUpdateRequestDto dto) {

    }
}
