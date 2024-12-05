package com.cookie.domain.director.controller;

import com.cookie.domain.actor.dto.response.ActorDetailResponse;
import com.cookie.domain.director.dto.response.DirectorDetailResponse;
import com.cookie.domain.director.service.DirectorService;
import com.cookie.global.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/director")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping("/{directorId}")
    public ResponseEntity<?> getDirectorDetails(@PathVariable(name="directorId") Long directorId) {
        DirectorDetailResponse directorDetailResponse = directorService.getDirectorDetails(directorId);
        return ResponseEntity.ok(ApiUtil.success(directorDetailResponse));
    }
}
