package com.cookie.domain.actor.controller;

import com.cookie.domain.actor.dto.response.ActorDetailResponse;
import com.cookie.domain.actor.service.ActorService;
import com.cookie.global.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/actor")
@RequiredArgsConstructor
public class ActorController {

    private final ActorService actorService;

    @GetMapping("/{actorId}")
    public ResponseEntity<?> getActorDetails(@PathVariable(name="actorId") Long actorId) {
        ActorDetailResponse actorDetailResponse = actorService.getActorDetails(actorId);
        return ResponseEntity.ok(ApiUtil.success(actorDetailResponse));
    }
}
