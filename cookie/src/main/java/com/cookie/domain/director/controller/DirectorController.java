package com.cookie.domain.director.controller;

import com.cookie.domain.director.dto.response.DirectorDetailResponse;
import com.cookie.domain.director.service.DirectorService;
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

@Tag(name = "감독", description = "감독 API")
@RestController
@RequestMapping("/api/director")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @Operation(summary = "감독 상세 정보", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DirectorDetailResponse.class)))
    })
    @GetMapping("/{directorId}")
    public ResponseEntity<?> getDirectorDetails(@PathVariable(name="directorId") Long directorId) {
        DirectorDetailResponse directorDetailResponse = directorService.getDirectorDetails(directorId);
        return ResponseEntity.ok(ApiUtil.success(directorDetailResponse));
    }
}
