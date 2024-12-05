package com.cookie.domain.actor.controller;

import com.cookie.domain.actor.dto.response.ActorDetailResponse;
import com.cookie.domain.actor.service.ActorService;
import com.cookie.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "배우", description = "배우 API")
@RestController
@RequestMapping("/api/actor")
@RequiredArgsConstructor
public class ActorController {

    private final ActorService actorService;

    @Operation(summary = "배우 상세 정보", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ActorDetailResponse.class)))
    })
    @GetMapping("/{actorId}")
    public ResponseEntity<?> getActorDetails(@PathVariable(name="actorId") Long actorId) {
        ActorDetailResponse actorDetailResponse = actorService.getActorDetails(actorId);
        return ResponseEntity.ok(ApiUtil.success(actorDetailResponse));
    }
}
