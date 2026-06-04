package com.movie.api.controller;

import com.movie.api.constant.Roles;
import com.movie.api.model.dto.LoginDto;
import com.movie.api.model.entity.Role;
import com.movie.api.model.entity.Worker;
import com.movie.api.model.vo.WorkerPublicVO;
import com.movie.api.service.RoleService;
import com.movie.api.service.WorkerService;
import com.movie.api.utils.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "员工接口")
@RequestMapping("/api/worker")
public class WorkerController {

    @Resource
    private WorkerService workerService;

    @Resource
    private RoleService roleService;

    @PostMapping("/login")
    @Operation(summary = "员工登录")
    public Map<String, Object> login(@RequestBody LoginDto dto) throws Exception {
        Worker worker = workerService.login(dto);
        Map<String, Object> map = new HashMap<>();
        //是否选择记住我
        long exp = dto.isRemember() ? JwtTokenUtil.REMEMBER_EXPIRATION_TIME : JwtTokenUtil.EXPIRATION_TIME;

        // 基础权限始终注入 JWT；库中扩展权限去重，避免与 ROLE_WORKER 重复
        List<String> roles = new ArrayList<>();
        roles.add(Roles.ROLE_WORKER);
        for (Role role : roleService.listRolesByWorkerId(worker.getId())) {
            String v = role.getValue();
            if (v != null && !Roles.ROLE_WORKER.equals(v) && !roles.contains(v)) {
                roles.add(v);
            }
        }
        map.put("token", JwtTokenUtil.createToken(dto.getUsername(), roles, exp));
        map.put("worker", worker);
        return map;
    }

    @PostMapping("")
    @Operation(summary = "添加员工")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Worker create(@RequestBody Worker worker) throws Exception {
        return workerService.create(worker);
    }

    @GetMapping("")
    @Operation(summary = "查询全部员工")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<Worker> list(){
        return workerService.findAll();
    }

    /**
     * 须声明在 /{id} 之前，避免 path 被当成 id。
     */
    @GetMapping("/public")
    @Operation(summary = "留言页等：在职客服展示信息（普通用户可访问，无敏感字段）")
    @PreAuthorize("permitAll()")
    public List<WorkerPublicVO> listPublicShowcase() {
        return workerService.listPublicShowcase();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据id查询员工")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_WORKER')")
    public Worker findById(@PathVariable String id){
        return workerService.findById(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据id删除员工")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteById(@PathVariable String id){
        workerService.deleteById(id);
    }

    @PutMapping("")
    @Operation(summary = "更新员工信息")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_WORKER')")
    public void update(@RequestBody Worker worker) throws Exception {
        workerService.update(worker);
    }

}
