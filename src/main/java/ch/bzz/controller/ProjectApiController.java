package ch.bzz.controller;

import ch.bzz.generated.api.ProjectApi;
import ch.bzz.generated.model.LoginProject200Response;
import ch.bzz.generated.model.LoginRequest;
import ch.bzz.model.Project;
import ch.bzz.repository.ProjectRepository;
import ch.bzz.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class ProjectApiController implements ProjectApi {

    private final ProjectRepository projectRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    public ProjectApiController(ProjectRepository projectRepository, JwtUtil jwtUtil) {
        this.projectRepository = projectRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public ResponseEntity<Void> createProject(LoginRequest loginRequest) {
        String projectName = loginRequest.getProjectName();
        String password = loginRequest.getPassword();

        if (projectName.isBlank() || projectName.length() > 100 || password.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        // save() would overwrite an existing project, including its password
        if (projectRepository.existsById(projectName)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Project project = new Project();
        project.setProjectName(projectName);
        project.setPasswordHash(encoder.encode(password));
        projectRepository.save(project);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<LoginProject200Response> loginProject(LoginRequest loginRequest) {
        Optional<Project> project = projectRepository.findById(loginRequest.getProjectName());

        // same response for both cases, so callers can't find out which project names exist
        if (project.isEmpty() || !encoder.matches(loginRequest.getPassword(), project.get().getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtUtil.generateToken(project.get().getProjectName());
        return ResponseEntity.ok(new LoginProject200Response().accessToken(token));
    }
}
