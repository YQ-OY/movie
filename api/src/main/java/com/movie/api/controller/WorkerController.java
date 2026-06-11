package com.movie.api.controller;

import com.movie.api.annotation.DisableBaseResponse;
import com.movie.api.constant.Roles;
import com.movie.api.model.dto.ForgotResetDto;
import com.movie.api.model.dto.ForgotSendCodeDto;
import com.movie.api.model.dto.LoginDto;
import com.movie.api.model.entity.Role;
import com.movie.api.model.entity.Worker;
import com.movie.api.model.support.ResponseResult;
import com.movie.api.model.vo.ForgotCaptchaVO;
import com.movie.api.model.vo.ForgotSendCodeVO;
import com.movie.api.model.vo.PageResult;
import com.movie.api.model.vo.WorkerPublicVO;
import com.movie.api.service.ForgotPasswordService;
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

    @Resource
    private ForgotPasswordService forgotPasswordService;

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

    @GetMapping("/page")
    @Operation(summary = "分页查询员工，支持按用户名/部门搜索")
    public PageResult<Worker> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        return workerService.findByPage(page, size, keyword);
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

    @GetMapping("/forgot/captcha")
    @Operation(summary = "忘记密码-获取图形验证码")
    public ForgotCaptchaVO forgotCaptcha() {
        return forgotPasswordService.createCaptcha();
    }

    @PostMapping("/forgot/send-code")
    @Operation(summary = "忘记密码-发送短信验证码（演示）")
    @DisableBaseResponse
    public ResponseResult<ForgotSendCodeVO> forgotSendCode(@RequestBody ForgotSendCodeDto dto) throws Exception {
        ForgotSendCodeVO vo = workerService.sendForgotCode(dto);
        return new ResponseResult<>("验证码已发送（演示验证码：123456）", vo);
    }

    @PostMapping("/forgot/verify-sms")
    @Operation(summary = "忘记密码-校验短信验证码")
    @DisableBaseResponse
    public ResponseResult<Void> forgotVerifySms(@RequestBody ForgotResetDto dto) throws Exception {
        workerService.verifyForgotSms(dto);
        return new ResponseResult<>("验证成功", null);
    }

    @PostMapping("/forgot/reset")
    @Operation(summary = "忘记密码-重置密码")
    @DisableBaseResponse
    public ResponseResult<Void> forgotReset(@RequestBody ForgotResetDto dto) throws Exception {
        workerService.resetPasswordByPhone(dto);
        return new ResponseResult<>("密码重置成功，请登录", null);
    }

}
