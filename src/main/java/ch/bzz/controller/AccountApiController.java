package ch.bzz.controller;

import ch.bzz.generated.api.AccountApi;
import ch.bzz.generated.model.Account;
import ch.bzz.generated.model.UpdateAccountsRequest;
import ch.bzz.model.Project;
import ch.bzz.repository.AccountRepository;
import ch.bzz.repository.ProjectRepository;
import ch.bzz.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class AccountApiController implements AccountApi {

    private final AccountRepository accountRepository;
    private final ProjectRepository projectRepository;
    private final JwtUtil jwtUtil;
    private final HttpServletRequest request;

    public AccountApiController(AccountRepository accountRepository, ProjectRepository projectRepository,
                                JwtUtil jwtUtil, HttpServletRequest request) {
        this.accountRepository = accountRepository;
        this.projectRepository = projectRepository;
        this.jwtUtil = jwtUtil;
        this.request = request;
    }

    @Override
    public ResponseEntity<List<Account>> getAccounts() {
        Optional<Project> project = jwtUtil.extractProjectName(request.getHeader(HttpHeaders.AUTHORIZATION))
                .flatMap(projectRepository::findById);
        if (project.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Account> accounts = accountRepository.findByProject(project.get()).stream()
                .map(this::toApiAccount)
                .toList();
        return ResponseEntity.ok(accounts);
    }

    @Override
    public ResponseEntity<Void> updateAccounts(UpdateAccountsRequest updateAccountsRequest) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    private Account toApiAccount(ch.bzz.model.Account account) {
        return new Account()
                .number(account.getAccountNumber())
                .name(account.getName());
    }
}
