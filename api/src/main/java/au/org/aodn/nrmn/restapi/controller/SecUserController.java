package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.repository.UserSecEntityRepository;
import au.org.aodn.nrmn.restapi.validation.ValidationProcess;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import au.org.aodn.nrmn.restapi.model.db.SecUserEntity;
import au.org.aodn.nrmn.restapi.dto.auth.SignUpRequestDto;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import au.org.aodn.nrmn.restapi.service.UserService;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import au.org.aodn.nrmn.restapi.util.UriUtil;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import au.org.aodn.nrmn.restapi.dto.user.get.UserGetSimpleDto;
import org.modelmapper.ModelMapper;

import java.util.*;

@RestController
@CrossOrigin
@RequestMapping(path = "/api")
public class SecUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    UserSecEntityRepository userSecEntityRepository;

    @GetMapping(path = "/user", produces = "application/json")
    public List<SecUserEntity> getUsers() {
        return userSecEntityRepository.findAll();
    }

    @PostMapping(path = "/user-rawsave", consumes = "application/json", produces = "application/json")
    public Optional<SecUserEntity> save(SecUserEntity save) {
        return Optional.of(userSecEntityRepository.save(save));
    }

    @PostMapping(path = "/user", consumes = "application/json", produces = "application/json")
    public ResponseEntity registerUser(@Valid @RequestBody SignUpRequestDto signUpRequestDto, HttpServletRequest request) {

        SecUserEntity newSecUserEntity = userService.createUser(signUpRequestDto, UriUtil.baseUri(request));

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/{id}")
                .buildAndExpand(newSecUserEntity.getId()).toUri();

        return ResponseEntity.created(location).body(modelMapper.map(newSecUserEntity, UserGetSimpleDto.class));
    }



}
