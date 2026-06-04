package com.movie.api.controller;

import com.movie.api.model.entity.Poster;
import com.movie.api.service.PosterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Tag(name = "首页海报接口")
@RequestMapping("/api/poster")
public class PosterController {

    @Resource
    private PosterService posterService;

    @PostMapping("")
    @Operation(summary = "添加首页海报")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_FILM_MANAGE')")
    public void save(@RequestBody Poster poster) {
        posterService.save(poster);
    }

    @PutMapping("")
    @Operation(summary = "更新海报")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_FILM_MANAGE')")
    public void update(@RequestBody Poster poster) {
        posterService.update(poster);
    }

    @GetMapping("")
    @Operation(summary = "获取所有海报")
    public List<Poster> list(String status) {
        if (status != null) {
            return posterService.findByStatus(Boolean.parseBoolean(status));
        }
        return posterService.findAll();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除海报")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_FILM_MANAGE')")
    public void delete(@PathVariable String id) {
        posterService.deleteById(id);
    }

    @DeleteMapping("")
    @Operation(summary = "删除所有海报")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_FILM_MANAGE')")
    public void deleteAll() {
        posterService.deleteAll();
    }

}
